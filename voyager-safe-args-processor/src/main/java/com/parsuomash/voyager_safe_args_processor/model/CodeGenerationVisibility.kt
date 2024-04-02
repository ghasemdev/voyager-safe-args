package com.parsuomash.voyager_safe_args_processor.model

internal enum class CodeGenerationVisibility {
  INTERNAL, PUBLIC, FOLLOW_COMPOSABLE_FUNCTION
}

internal fun String.toCodeGenerationVisibility(): CodeGenerationVisibility = when (this) {
  "$PACKAGE_NAME.INTERNAL" -> CodeGenerationVisibility.INTERNAL
  "$PACKAGE_NAME.PUBLIC" -> CodeGenerationVisibility.PUBLIC
  "$PACKAGE_NAME.FOLLOW_COMPOSABLE_FUNCTION" -> CodeGenerationVisibility.FOLLOW_COMPOSABLE_FUNCTION
  else -> error("Illegal State $this")
}

private const val PACKAGE_NAME = "com.parsuomash.voyager_safe_args.utils.CodeGenerationVisibility"
