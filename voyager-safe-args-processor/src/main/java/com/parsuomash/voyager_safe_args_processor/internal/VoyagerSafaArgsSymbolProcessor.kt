package com.parsuomash.voyager_safe_args_processor.internal

import com.fleshgrinder.extensions.kotlin.toUpperCamelCase
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.Visibility
import com.google.devtools.ksp.validate
import com.parsuomash.voyager_safe_args_processor.utils.ImportManager

internal class VoyagerSafaArgsSymbolProcessor(
  private val config: VoyagerSafaArgsConfig,
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger
) : SymbolProcessor {
  private val screenAnnotationDeclarations = mutableListOf<KSFunctionDeclaration>()
  private val screenAnnotationVisitor = ScreenAnnotationVisitor(screenAnnotationDeclarations)

  override fun process(resolver: Resolver): List<KSAnnotated> {
    resolver.screenAnnotationProcess()
    return emptyList()
  }

  override fun finish() {
    screenAnnotationFinish()
  }

  // TODO Refactor
  private fun screenAnnotationFinish() {
    screenAnnotationDeclarations.forEach { declaration ->
      val functionName = declaration.simpleName.getShortName()
      val packageName = declaration.packageName.asString()

      val importManager = ImportManager().apply {
        append("$packageName.$functionName")
      }

      val screenKey = declaration
        .annotations
        .getValue<String>("Screen", "key")
      if (!screenKey.isNullOrBlank()) {
        importManager.append("cafe.adriel.voyager.core.screen.ScreenKey")
      }

      val screenName = declaration
        .annotations
        .getValue<String>("Screen", "name")

      val paramsWithType = declaration.parameters.map {
        val dec = it.type.resolve().declaration
        val type = dec.simpleName.getShortName()
        val packageNameType = dec.packageName.asString()

        var isSerializable = false
        dec.annotations.forEach { annotation ->
          if (
            annotation.annotationType.resolve().declaration.packageName.asString() == "kotlinx.serialization" &&
            annotation.shortName.asString() == "Serializable"
          ) {
            isSerializable = true
          }
        }

        if (packageNameType != "kotlin") {
          importManager.append("$packageNameType.$type")
        }

        Triple(it.name!!.getShortName(), type, isSerializable)
      }
      val isSerializableParamExist = paramsWithType.any { it.third }
      if (isSerializableParamExist) {
        importManager.append("androidx.compose.runtime.remember")
        importManager.append("com.parsuomash.voyager_safe_args.encode")
        importManager.append("com.parsuomash.voyager_safe_args.decode")
      }

      val classParams = StringBuilder()
      val subclassParams = StringBuilder()
      val supperClassParams = StringBuilder()
      val composableParams = StringBuilder()
      val equalsConditions = StringBuilder()
      val hashCodeFormula = StringBuilder()
      val toStringFormula = StringBuilder()

      paramsWithType.forEachIndexed { index, (param, type, isSerializable) ->
        if (isSerializable) {
          classParams.append("${INDENTATION}val $param: String")
          subclassParams.append("${INDENTATION}$param: $type")
          if (index != paramsWithType.lastIndex) subclassParams.appendSeparator()

          equalsConditions.append("${INDENTATION2x}if ($param != other.$param) return false")
          equalsConditions.appendLine()

          if (index == 0) {
            hashCodeFormula.append("${INDENTATION2x}var result = $param.hashCode()")
          } else {
            hashCodeFormula.append("${INDENTATION2x}result = 31 * result + $param.hashCode()")
          }
          hashCodeFormula.appendLine()

          toStringFormula.append("$param=\${$param.decode<$type>()}")
          if (index != paramsWithType.lastIndex) toStringFormula.append(", ")
        } else if (isSerializableParamExist) {
          classParams.append("${INDENTATION}open val $param: $type")
          subclassParams.append("${INDENTATION}override val $param: $type")
          if (index != paramsWithType.lastIndex) subclassParams.appendSeparator()

          equalsConditions.append("${INDENTATION2x}if ($param != other.$param) return false")
          equalsConditions.appendLine()

          if (index == 0) {
            hashCodeFormula.append("${INDENTATION2x}var result = $param.hashCode()")
          } else {
            hashCodeFormula.append("${INDENTATION2x}result = 31 * result + $param.hashCode()")
          }
          hashCodeFormula.appendLine()

          toStringFormula.append("$param=$$param")
          if (index != paramsWithType.lastIndex) toStringFormula.append(", ")
        } else {
          classParams.append("${INDENTATION}val $param: $type")
        }

        if (isSerializable) {
          composableParams.append("${INDENTATION3x}$param = remember($param) { $param.decode() }")
          supperClassParams.append("${INDENTATION}$param = $param.encode()")
        } else {
          composableParams.append("${INDENTATION3x}$param = $param")
          supperClassParams.append("${INDENTATION}$param = $param")
        }

        if (index != paramsWithType.lastIndex) {
          classParams.appendSeparator()
          supperClassParams.appendSeparator()
          composableParams.appendSeparator()
        }
      }

      val isInternal = when (declaration.getVisibility()) {
        Visibility.INTERNAL -> true
        Visibility.PUBLIC -> false
        else -> {
          logger.error(
            "Visibility of $packageName.$functionName function must be internal or public!!"
          )
          return
        }
      }
      val visibility = if (isInternal) "internal " else ""

      writeToFile(
        imports = importManager.finalize(),
        visibility = visibility,
        functionName = if (!screenName.isNullOrBlank()) screenName else functionName,
        classParams = classParams.toString(),
        subclassParams = subclassParams.toString(),
        supperClassParams = supperClassParams.toString(),
        composableParams = composableParams.toString(),
        isSerializableParamExist = isSerializableParamExist,
        equalsConditions = equalsConditions.toString(),
        hashCodeFormula = hashCodeFormula.toString(),
        toStringFormula = toStringFormula.toString(),
        screenKey = screenKey
      )
    }
  }

  private fun writeToFile(
    imports: String,
    visibility: String,
    functionName: String,
    classParams: String,
    subclassParams: String,
    supperClassParams: String,
    composableParams: String,
    isSerializableParamExist: Boolean,
    equalsConditions: String,
    hashCodeFormula: String,
    toStringFormula: String,
    screenKey: String?,
  ) {
    val fileName = "${config.moduleName.toUpperCamelCase()}${functionName}"

    val classType = if (isSerializableParamExist) {
      "abstract class"
    } else if (classParams.isNotBlank()) {
      "data class"
    } else {
      "data object"
    }
    val className = if (isSerializableParamExist) "${functionName}SafeArg" else functionName
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
        """${visibility}class $functionName$subClassParameter : $className$supperClassParameter
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
          |${INDENTATION2x}other as ${functionName}SafeArg
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
          |${INDENTATION2x}return "$functionName($toStringFormula)"
          |${INDENTATION}}"""
      )
    }

    val key = StringBuilder()
    if (!screenKey.isNullOrBlank()) {
      key.append(
        """${INDENTATION}override val key: ScreenKey
          |${INDENTATION2x}get() = "$screenKey"
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

  private fun Sequence<KSAnnotation>.withName(annotationName: String): KSAnnotation? =
    firstOrNull { it.shortName.getShortName() == annotationName }

  private fun List<KSValueArgument>.withName(argumentName: String): KSValueArgument? =
    firstOrNull { it.name?.getShortName() == argumentName }

  private fun Resolver.screenAnnotationProcess() {
    getSymbolsWithAnnotation(SCREEN_ANNOTATION_PACKAGE)
      .filter { it is KSFunctionDeclaration && it.validate() }
      .forEach { it.accept(screenAnnotationVisitor, Unit) }
  }

  private fun StringBuilder.appendSeparator() {
    append(",")
    appendLine()
  }

  companion object {
    val INDENTATION = " ".repeat(2)
    val INDENTATION2x = " ".repeat(4)
    val INDENTATION3x = " ".repeat(6)

    private const val PACKAGE_NAME = "com.parsuomash.voyager_safe_args"
    private const val SCREEN_ANNOTATION_ANNOTATION = "Screen"
    const val SCREEN_ANNOTATION_PACKAGE = "$PACKAGE_NAME.$SCREEN_ANNOTATION_ANNOTATION"
  }
}
