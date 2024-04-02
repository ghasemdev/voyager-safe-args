package com.parsuomash.voyager_safe_args_processor.model

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.parsuomash.voyager_safe_args_processor.utils.getValue

internal data class VisualizationNode(
  val id: String = "",
  val name: String = "",
  val graph: String = "",
  val destinations: List<String> = emptyList(),
  val includes: List<String> = emptyList(),
  val isOptional: Boolean = false,
  val isStart: Boolean = false,
  val isEnd: Boolean = false,
)

internal fun KSFunctionDeclaration.getVisualizationNode(): VisualizationNode {
  val name = this
    .annotations
    .getValue<String>("Visualization", "name")

  val graph = this
    .annotations
    .getValue<String>("Visualization", "graph")

  val destinations = this
    .annotations
    .getValue("Visualization", "destinations")
    ?.removePrefix("[")
    ?.removeSuffix("]")

  val includes = this
    .annotations
    .getValue("Visualization", "includes")
    ?.removePrefix("[")
    ?.removeSuffix("]")

  val isOptional = this
    .annotations
    .getValue<Boolean>("Visualization", "isOptional")

  val isStart = this
    .annotations
    .getValue<Boolean>("Visualization", "isStart")

  val isEnd = this
    .annotations
    .getValue<Boolean>("Visualization", "isEnd")

  return VisualizationNode(
    id = simpleName.getShortName(),
    name = if (name.isNullOrBlank()) simpleName.getShortName() else name,
    graph = graph.orEmpty(),
    destinations = if (!destinations.isNullOrBlank()) {
      destinations.split(", ")
    } else {
      emptyList()
    },
    includes = if (!includes.isNullOrBlank()) {
      includes.split(", ")
    } else {
      emptyList()
    },
    isOptional = isOptional ?: false,
    isStart = isStart ?: false,
    isEnd = isEnd ?: false
  )
}
