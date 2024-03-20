package com.parsuomash.voyager_safe_args

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Screen(
  val key: String = "",
  val name: String = "",
)
