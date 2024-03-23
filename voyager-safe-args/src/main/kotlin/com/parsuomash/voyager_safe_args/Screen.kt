package com.parsuomash.voyager_safe_args

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Screen(
  val name: String = "",
  val key: String = "",
  val visibility: CodeGenerationVisibility = CodeGenerationVisibility.FOLLOW_COMPOSABLE_FUNCTION
)
