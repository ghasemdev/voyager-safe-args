package com.parsuomash.voyager_safe_args_processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

internal class Logger(
  private val logger: KSPLogger
) : KSPLogger by logger {
  override fun error(message: String, symbol: KSNode?) {
    logger.error("${"~" * 50}\n$message\n${"~" * 50}${"~" * 9}", symbol)
  }
}

private operator fun String.times(n: Int): String = repeat(n)
