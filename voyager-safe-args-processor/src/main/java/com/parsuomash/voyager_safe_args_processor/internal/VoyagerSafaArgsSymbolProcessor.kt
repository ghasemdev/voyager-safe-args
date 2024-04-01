package com.parsuomash.voyager_safe_args_processor.internal

import com.fleshgrinder.extensions.kotlin.toUpperCamelCase
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.Visibility
import com.google.devtools.ksp.validate
import com.parsuomash.voyager_safe_args_processor.VisualizationAnnotationVisitor
import com.parsuomash.voyager_safe_args_processor.utils.CodeGenerationVisibility
import com.parsuomash.voyager_safe_args_processor.utils.ImportManager
import com.parsuomash.voyager_safe_args_processor.utils.Logger
import com.parsuomash.voyager_safe_args_processor.utils.times
import com.parsuomash.voyager_safe_args_processor.utils.toCodeGenerationVisibility

internal class VoyagerSafaArgsSymbolProcessor(
  private val config: VoyagerSafaArgsConfig,
  private val codeGenerator: CodeGenerator,
  logger: KSPLogger
) : SymbolProcessor {
  private val logger = Logger(logger)

  private val screenAnnotationDeclarations = mutableListOf<KSFunctionDeclaration>()
  private val screenAnnotationVisitor = ScreenAnnotationVisitor(screenAnnotationDeclarations)

  private val paramSerializerAnnotationDeclarations = mutableListOf<KSClassDeclaration>()
  private val paramSerializeAnnotationVisitor =
    ParamSerializerAnnotationVisitor(paramSerializerAnnotationDeclarations)

  private val visualizationAnnotationDeclarations = mutableListOf<KSFunctionDeclaration>()
  private val visualizationAnnotationVisitor =
    VisualizationAnnotationVisitor(visualizationAnnotationDeclarations)

  private val customSerializer = mutableMapOf<String, String>()

  override fun process(resolver: Resolver): List<KSAnnotated> {
    resolver.visualizationAnnotationProcess()
    resolver.paramSerializeAnnotationProcess()
    resolver.screenAnnotationProcess()
    return emptyList()
  }

  override fun finish() {
    visualizationValidation()
    screenParamSerializerValidation()
    screenAnnotationFinish()
  }

  private fun visualizationValidation() {
    visualizationAnnotationDeclarations.forEach { declaration ->
      val className = declaration.simpleName.getShortName()
      val packageName = declaration.packageName.asString()
    }
  }

  private fun screenParamSerializerValidation() {
    paramSerializerAnnotationDeclarations.forEach { declaration ->
      val className = declaration.simpleName.getShortName()
      val packageName = declaration.packageName.asString()

      if (declaration.classKind != ClassKind.OBJECT) {
        logger.error(
          """
            |[$packageName.$className]
            |   ScreenParamSerializer must be object!!
            """.trimMargin()
        )
        return
      }

      val superTypes = declaration.superTypes.first()
        .element?.typeArguments?.first()
        ?.type?.resolve()?.declaration
      val typeName = superTypes?.simpleName?.getShortName().orEmpty()
      val typePackageName = superTypes?.packageName?.asString().orEmpty()

      customSerializer["$typePackageName.$typeName"] = "$packageName.$className"
    }
  }

  // TODO Refactor
  private fun screenAnnotationFinish() {
    screenAnnotationDeclarations.forEach { declaration ->
      val functionName = declaration.simpleName.getShortName()
      val packageName = declaration.packageName.asString()

      if (declaration.getVisibility() == Visibility.PRIVATE) {
        logger.error(
          """
              |[$packageName.$functionName]
              |   visibility of function must be internal or public!!
              """.trimMargin()
        )
        return
      }

      val importManager = ImportManager().apply {
        append("$packageName.$functionName")
      }

      val screenName = declaration
        .annotations
        .getValue<String>("Screen", "name")

      val screenKey = declaration
        .annotations
        .getValue<String>("Screen", "key")

      val classParams = StringBuilder()
      val subclassParams = StringBuilder()
      val supperClassParams = StringBuilder()
      val composableParams = StringBuilder()
      val equalsConditions = StringBuilder()
      val hashCodeFormula = StringBuilder()
      val toStringFormula = StringBuilder()

      var screenKeyCount = 0
      var screenKeyAnnotation: String? = null
      val isSerializable = BooleanArray(declaration.parameters.size) { false }

      for ((index, ksValueParameter) in declaration.parameters.withIndex()) {
        val typeDeclaration = ksValueParameter.type.resolve().declaration

        val param = ksValueParameter.name!!.getShortName()
        val type = ksValueParameter.type.resolve().toString()
        val simpleType = typeDeclaration.simpleName.getShortName()
        val packageNameType = typeDeclaration.packageName.asString()

        for (annotation in ksValueParameter.annotations) {
          if (
            annotation.annotationType.resolve().declaration.packageName.asString() == ANNOTATION_PACKAGE_NAME &&
            annotation.shortName.asString() == "ScreenKey"
          ) {
            if (packageNameType == "kotlin" && type in listOf("Int", "Long", "String")) {
              screenKeyAnnotation = param
              screenKeyCount++
              if (screenKeyCount == 2) {
                logger.error(
                  """
                     |[$packageName.$functionName]
                     |   (@ScreenKey ...,@ScreenKey $param: $type, ...) You can only use one screenKey!!
                     """.trimMargin()
                )
              }
              break
            } else {
              logger.error(
                """
                   |[$packageName.$functionName]
                   |   (@ScreenKey $param: $type, ...) screen key type must be Int, Long or String!!
                   """.trimMargin()
              )
            }
          }
        }

        for (annotation in typeDeclaration.annotations) {
          if (
            annotation.annotationType.resolve().declaration.packageName.asString() == "kotlinx.serialization" &&
            annotation.shortName.asString() == "Serializable"
          ) {
            isSerializable[index] = true
            break
          }
        }

        if (packageNameType != "kotlin") {
          importManager.append("$packageNameType.$simpleType")
        }
        val customSerializer = customSerializer["$packageNameType.$simpleType"]
        if (customSerializer != null) {
          importManager.append("$customSerializer.encode")
          importManager.append("$customSerializer.decode")
        }
      }

      val isSerializableParamExist = isSerializable.any { true }

      for ((index, ksValueParameter) in declaration.parameters.withIndex()) {
        val typeDeclaration = ksValueParameter.type.resolve().declaration

        val param = ksValueParameter.name!!.getShortName()
        val type = ksValueParameter.type.resolve().toString()
        val simpleType = typeDeclaration.simpleName.getShortName()
        val packageNameType = typeDeclaration.packageName.asString()

        val customSerializer = customSerializer["$packageNameType.$simpleType"]

        if (isSerializable[index] || customSerializer != null) {
          classParams.append("${INDENTATION}val $param: String")
          subclassParams.append("${INDENTATION}$param: $type")
          if (index != declaration.parameters.lastIndex) subclassParams.appendSeparator()

          equalsConditions.append("${INDENTATION2x}if ($param != other.$param) return false")
          equalsConditions.appendLine()

          if (index == 0) {
            hashCodeFormula.append("${INDENTATION2x}var result = $param.hashCode()")
          } else {
            hashCodeFormula.append("${INDENTATION2x}result = 31 * result + $param.hashCode()")
          }
          hashCodeFormula.appendLine()

          if (customSerializer != null) {
            toStringFormula.append("$param=\${decode($param)}")
          } else {
            toStringFormula.append("$param=\${$param.decode<$type>()}")
          }
          if (index != declaration.parameters.lastIndex) toStringFormula.append(", ")
        } else if (isSerializableParamExist) {
          classParams.append("${INDENTATION}open val $param: $type")
          subclassParams.append("${INDENTATION}override val $param: $type")
          if (index != declaration.parameters.lastIndex) subclassParams.appendSeparator()

          equalsConditions.append("${INDENTATION2x}if ($param != other.$param) return false")
          equalsConditions.appendLine()

          if (index == 0) {
            hashCodeFormula.append("${INDENTATION2x}var result = $param.hashCode()")
          } else {
            hashCodeFormula.append("${INDENTATION2x}result = 31 * result + $param.hashCode()")
          }
          hashCodeFormula.appendLine()

          if (type in TYPE_ARRAYS || type.contains("Array")) {
            toStringFormula.append("$param=\${$param.joinToString(prefix = \"[\", postfix = \"]\")}")
          } else {
            toStringFormula.append("$param=$$param")
          }
          if (index != declaration.parameters.lastIndex) toStringFormula.append(", ")
        } else {
          classParams.append("${INDENTATION}val $param: $type")
        }

        if (customSerializer != null) {
          composableParams.append("${INDENTATION3x}$param = remember($param) { decode($param) }")
          supperClassParams.append("${INDENTATION}$param = encode($param)")
        } else if (isSerializable[index]) {
          composableParams.append("${INDENTATION3x}$param = remember($param) { $param.decode() }")
          supperClassParams.append("${INDENTATION}$param = $param.encode()")
        } else {
          composableParams.append("${INDENTATION3x}$param = $param")
          supperClassParams.append("${INDENTATION}$param = $param")
        }

        if (index != declaration.parameters.lastIndex) {
          classParams.appendSeparator()
          supperClassParams.appendSeparator()
          composableParams.appendSeparator()
        }
      }

      if (!screenKey.isNullOrBlank() && screenKeyCount != 0) {
        logger.error(
          """
            |[$packageName.$functionName]
            |   @Screen(key=...) and @ScreenKey not allow at the same time!!
            """.trimMargin()
        )
      }
      if (!screenKey.isNullOrBlank() || screenKeyCount != 0) {
        importManager.append("cafe.adriel.voyager.core.screen.ScreenKey")
      }

      if (isSerializableParamExist) {
        importManager.append("androidx.compose.runtime.remember")
        importManager.append("com.parsuomash.voyager_safe_args.serializer.encode")
        importManager.append("com.parsuomash.voyager_safe_args.serializer.decode")
      }

      val screenVisibility = declaration
        .annotations
        .getValue("Screen", "visibility")!!
        .toCodeGenerationVisibility()

      val isInternal = when (screenVisibility) {
        CodeGenerationVisibility.INTERNAL -> true
        CodeGenerationVisibility.PUBLIC -> false
        CodeGenerationVisibility.FOLLOW_COMPOSABLE_FUNCTION -> {
          declaration.getVisibility() == Visibility.INTERNAL
        }
      }
      val visibility = if (isInternal) "internal " else ""

      writeToFile(
        imports = importManager.finalize(),
        visibility = visibility,
        functionName = functionName,
        screenName = if (!screenName.isNullOrBlank()) screenName else functionName,
        classParams = classParams.toString(),
        subclassParams = subclassParams.toString(),
        supperClassParams = supperClassParams.toString(),
        composableParams = composableParams.toString(),
        isSerializableParamExist = isSerializableParamExist,
        equalsConditions = equalsConditions.toString(),
        hashCodeFormula = hashCodeFormula.toString(),
        toStringFormula = toStringFormula.toString(),
        screenKey = screenKey,
        screenKeyAnnotation = screenKeyAnnotation
      )
    }
  }

  private fun writeToFile(
    imports: String,
    visibility: String,
    functionName: String,
    screenName: String?,
    classParams: String,
    subclassParams: String,
    supperClassParams: String,
    composableParams: String,
    isSerializableParamExist: Boolean,
    equalsConditions: String,
    hashCodeFormula: String,
    toStringFormula: String,
    screenKey: String?,
    screenKeyAnnotation: String?,
  ) {
    val fileName = "${config.moduleName.toUpperCamelCase()}${screenName}"

    val classType = if (isSerializableParamExist) {
      "abstract class"
    } else if (classParams.isNotBlank()) {
      "data class"
    } else {
      "data object"
    }
    val className = if (isSerializableParamExist) "${screenName}SafeArg" else screenName
    val classParameter = if (classParams.isNotBlank()) "(\n$classParams\n)" else ""
    val subClassParameter = if (subclassParams.isNotBlank()) "(\n$subclassParams\n)" else ""
    val supperClassParameter =
      if (supperClassParams.isNotBlank()) "(\n$supperClassParams\n)" else ""

    val composableParameter = if (composableParams.isNotBlank()) {
      "(\n$composableParams\n$INDENTATION2x)"
    } else {
      "()"
    }

    val screenWrapperClass = StringBuilder()
    if (isSerializableParamExist) {
      screenWrapperClass.append(
        """${visibility}class $screenName$subClassParameter : $className$supperClassParameter
          |
          |"""
      )
    }

    val equalsFunction = StringBuilder()
    if (isSerializableParamExist) {
      equalsFunction.append(
        """
          |
          |${INDENTATION}override fun equals(other: Any?): Boolean {
          |${INDENTATION2x}if (this === other) return true
          |${INDENTATION2x}if (javaClass != other?.javaClass) return false
          |
          |${INDENTATION2x}other as ${screenName}SafeArg
          |
          |$equalsConditions
          |${INDENTATION2x}return true
          |${INDENTATION}}"""
      )
    }

    val hashCodeFunction = StringBuilder()
    if (isSerializableParamExist) {
      equalsFunction.append(
        """
          |
          |${INDENTATION}override fun hashCode(): Int {
          |$hashCodeFormula
          |${INDENTATION2x}return result
          |${INDENTATION}}"""
      )
    }

    val toStringFunction = StringBuilder()
    if (isSerializableParamExist) {
      toStringFunction.append(
        """
          |
          |${INDENTATION}override fun toString(): String {
          |${INDENTATION2x}return "$screenName($toStringFormula)"
          |${INDENTATION}}"""
      )
    }

    val key = StringBuilder()
    if (!screenKey.isNullOrBlank()) {
      key.append(
        """${INDENTATION}override val key: ScreenKey
          |${INDENTATION2x}get() = "Screen#$screenKey"
          |
          |"""
      )
    }
    if (!screenKeyAnnotation.isNullOrBlank()) {
      key.append(
        """${INDENTATION}override val key: ScreenKey
          |${INDENTATION2x}get() = "Screen#$$screenKeyAnnotation"
          |
          |"""
      )
    }

    codeGenerator.createNewFile(
      dependencies = Dependencies(
        aggregating = true,
        sources = screenAnnotationDeclarations.map { it.containingFile!! }.toTypedArray()
      ),
      packageName = PACKAGE_NAME,
      fileName = fileName
    ).use { stream ->
      stream.write(
        """
          |package $PACKAGE_NAME
          |
          |${imports}
          |
          |$screenWrapperClass${visibility}$classType $className$classParameter : Screen {
          |$key$INDENTATION@Composable
          |$INDENTATION@NonRestartableComposable
          |${INDENTATION}override fun Content() {
          |$INDENTATION2x$functionName$composableParameter
          |$INDENTATION}$equalsFunction$hashCodeFunction$toStringFunction
          |}
          |
          """.trimMargin().toByteArray()
      )
    }
  }

  private inline fun <reified T> Sequence<KSAnnotation>.getValue(
    annotationName: String,
    argumentName: String
  ): T? = withName(annotationName)
    ?.arguments
    ?.withName(argumentName)
    ?.value as? T

  @JvmName("getValueAsString")
  private fun Sequence<KSAnnotation>.getValue(
    annotationName: String,
    argumentName: String
  ): String? = withName(annotationName)
    ?.arguments
    ?.withName(argumentName)
    ?.value
    ?.toString()

  private fun Sequence<KSAnnotation>.withName(annotationName: String): KSAnnotation? =
    firstOrNull { it.shortName.getShortName() == annotationName }

  private fun List<KSValueArgument>.withName(argumentName: String): KSValueArgument? =
    firstOrNull { it.name?.getShortName() == argumentName }

  private fun Resolver.screenAnnotationProcess() {
    getSymbolsWithAnnotation(SCREEN_ANNOTATION_PACKAGE)
      .filter { it is KSFunctionDeclaration && it.validate() }
      .forEach { it.accept(screenAnnotationVisitor, Unit) }
  }

  private fun Resolver.paramSerializeAnnotationProcess() {
    getSymbolsWithAnnotation(PARAM_SERIALIZE_ANNOTATION_PACKAGE)
      .filter { it is KSClassDeclaration && it.validate() }
      .forEach { it.accept(paramSerializeAnnotationVisitor, Unit) }
  }

  private fun Resolver.visualizationAnnotationProcess() {
    getSymbolsWithAnnotation(VISUALIZATION_ANNOTATION_PACKAGE)
      .filter { it is KSClassDeclaration && it.validate() }
      .forEach { it.accept(visualizationAnnotationVisitor, Unit) }
  }

  private fun StringBuilder.appendSeparator() {
    append(",")
    appendLine()
  }

  companion object {
    private const val SPACE = " "
    val INDENTATION = SPACE * 2
    val INDENTATION2x = SPACE * 4
    val INDENTATION3x = SPACE * 6

    private const val PACKAGE_NAME = "com.parsuomash.voyager_safe_args"
    private const val ANNOTATION_PACKAGE_NAME = "com.parsuomash.voyager_safe_args.annotation"
    private const val SCREEN_ANNOTATION = "Screen"
    private const val PARAM_SERIALIZE_ANNOTATION = "ParamSerializer"
    private const val VISUALIZATION_ANNOTATION = "Visualization"
    private const val SCREEN_ANNOTATION_PACKAGE = "$ANNOTATION_PACKAGE_NAME.$SCREEN_ANNOTATION"
    private const val PARAM_SERIALIZE_ANNOTATION_PACKAGE =
      "$ANNOTATION_PACKAGE_NAME.$PARAM_SERIALIZE_ANNOTATION"
    private const val VISUALIZATION_ANNOTATION_PACKAGE =
      "$ANNOTATION_PACKAGE_NAME.$VISUALIZATION_ANNOTATION"

    private val TYPE_ARRAYS = listOf(
      "ByteArray",
      "CharArray",
      "ShortArray",
      "IntArray",
      "LongArray",
      "FloatArray",
      "DoubleArray",
      "BooleanArray"
    )
  }
}
