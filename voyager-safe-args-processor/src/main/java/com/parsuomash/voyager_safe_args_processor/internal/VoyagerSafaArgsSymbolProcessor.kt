package com.parsuomash.voyager_safe_args_processor.internal

import com.fleshgrinder.extensions.kotlin.toUpperCamelCase
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
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

  private fun screenAnnotationFinish() {
    screenAnnotationDeclarations.forEach { declaration ->
      val functionName = declaration.simpleName.getShortName()
      val packageName = declaration.packageName.asString()

      val importManager = ImportManager().apply {
        append("$packageName.$functionName")
      }

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

      paramsWithType.forEachIndexed { index, (param, type, isSerializable) ->
        if (isSerializable) {
          classParams.append("${INDENTATION}val $param: String")
          subclassParams.append("${INDENTATION}$param: $type")
          if (index != paramsWithType.lastIndex) subclassParams.appendSeparator()
        } else if (isSerializableParamExist) {
          classParams.append("${INDENTATION}open val $param: $type")
          subclassParams.append("${INDENTATION}override val $param: $type")
          if (index != paramsWithType.lastIndex) subclassParams.appendSeparator()
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
        functionName = functionName,
        classParams = classParams.toString(),
        subclassParams = subclassParams.toString(),
        supperClassParams = supperClassParams.toString(),
        composableParams = composableParams.toString(),
        isSerializableParamExist = isSerializableParamExist
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
          |$INDENTATION@Composable
          |$INDENTATION@NonRestartableComposable
          |${INDENTATION}override fun Content() {
          |$INDENTATION2x$functionName$composableParameter
          |$INDENTATION}
          |}
          |
          """.trimMargin().toByteArray()
      )
    }
  }

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
