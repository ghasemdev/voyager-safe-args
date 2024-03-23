@file:Suppress("unused")

package com.parsuomash.voyager_safe_args.annotation

import com.parsuomash.voyager_safe_args.utils.CodeGenerationVisibility

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Screen(
  val name: String = "",
  val key: String = "",
  val visibility: CodeGenerationVisibility = CodeGenerationVisibility.FOLLOW_COMPOSABLE_FUNCTION
)
