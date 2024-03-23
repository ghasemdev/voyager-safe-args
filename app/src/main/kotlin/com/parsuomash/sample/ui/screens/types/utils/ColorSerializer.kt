package com.parsuomash.sample.ui.screens.types.utils

import android.graphics.Color as AndroidColor
import com.parsuomash.voyager_safe_args.annotation.ParamSerializer
import com.parsuomash.voyager_safe_args.serializer.ScreenParamSerializer

@ParamSerializer
object ColorSerializer : ScreenParamSerializer<AndroidColor> {
  override fun encode(value: AndroidColor): String = value.toArgb().toString()
  override fun decode(value: String): AndroidColor = AndroidColor.valueOf(value.toInt())
}
