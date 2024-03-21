package com.parsuomash.voyager_safe_args

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Screen(
  val name: String = "",
  val key: String = "",
)
