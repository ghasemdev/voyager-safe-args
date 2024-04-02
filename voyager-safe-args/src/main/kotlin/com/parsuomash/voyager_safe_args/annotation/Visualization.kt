package com.parsuomash.voyager_safe_args.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Visualization(
  val name: String = "",
  val graph: String = "",
  val destinations: Array<String> = [],
  val includes: Array<String> = [],
  val isOptional: Boolean = false,
  val isStart: Boolean = false,
  val isEnd: Boolean = false,
)
