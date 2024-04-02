package com.parsuomash.voyager_safe_args_processor.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.parsuomash.voyager_safe_args_processor.model.VoyagerSafaArgsConfig

internal class VoyagerSafaArgsSymbolProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
    with(environment) {
      VoyagerSafaArgsSymbolProcessor(
        config = VoyagerSafaArgsConfig(options[ARG_MODULE_NAME].orEmpty()),
        codeGenerator = codeGenerator,
        logger = logger
      )
    }

  private companion object {
    const val ARG_MODULE_NAME = "voyager.moduleName"
  }
}
