package com.parsuomash.voyager_safe_args_processor.internal

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class VoyagerSafaArgsSymbolProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
    with(environment) {
      VoyagerSafaArgsSymbolProcessor(
        config = VoyagerSafaArgsConfig(
          moduleName = options[ARG_MODULE_NAME].orEmpty(),
          mermaidGraph = options[ARG_MERMAID_GRAPH],
          markdownMermaidGraph = options[ARG_MARKDOWN_MERMAID_GRAPH],
        ),
        codeGenerator = codeGenerator,
        logger = logger
      )
    }

  private companion object {
    const val ARG_MODULE_NAME = "voyager.moduleName"
    const val ARG_MERMAID_GRAPH = "voyager.mermaidGraph"
    const val ARG_MARKDOWN_MERMAID_GRAPH = "voyager.markdownMermaidGraph"
  }
}
