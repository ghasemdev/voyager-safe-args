package com.parsuomash.voyager_safe_args_processor.model

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.parsuomash.voyager_safe_args_processor.utils.getValue

internal data class VisualizationNode(
  val id: String = "",
  val name: String = "",
  val graph: String = "",
  val destinations: Array<String> = emptyArray(),
  val includes: Array<String> = emptyArray(),
  val isOptional: Boolean = false,
  val isStart: Boolean = false,
  val isEnd: Boolean = false,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as VisualizationNode

    if (id != other.id) return false
    if (name != other.name) return false
    if (graph != other.graph) return false
    if (!destinations.contentEquals(other.destinations)) return false
    if (!includes.contentEquals(other.includes)) return false
    if (isOptional != other.isOptional) return false
    if (isStart != other.isStart) return false
    if (isEnd != other.isEnd) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + graph.hashCode()
    result = 31 * result + destinations.contentHashCode()
    result = 31 * result + includes.contentHashCode()
    result = 31 * result + isOptional.hashCode()
    result = 31 * result + isStart.hashCode()
    result = 31 * result + isEnd.hashCode()
    return result
  }
}

internal fun KSFunctionDeclaration.getVisualizationNode(): VisualizationNode {
  val id = this
    .annotations
    .getValue<String>("Visualization", "id")

  val name = this
    .annotations
    .getValue<String>("Visualization", "name")

  val graph = this
    .annotations
    .getValue<String>("Visualization", "graph")

  val destinations = this
    .annotations
    .getValue<Array<String>>("Visualization", "destinations")

  val includes = this
    .annotations
    .getValue<Array<String>>("Visualization", "includes")

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
    id = id.orEmpty(),
    name = name.orEmpty(),
    graph = graph.orEmpty(),
    destinations = destinations ?: emptyArray(),
    includes = includes ?: emptyArray(),
    isOptional = isOptional ?: false,
    isStart = isStart ?: false,
    isEnd = isEnd ?: false
  )
}
