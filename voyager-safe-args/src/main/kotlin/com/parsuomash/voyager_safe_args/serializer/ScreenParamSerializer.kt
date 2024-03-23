package com.parsuomash.voyager_safe_args.serializer

interface ScreenParamSerializer<T> {
  fun encode(value: T): String
  fun decode(value: String): T
}
