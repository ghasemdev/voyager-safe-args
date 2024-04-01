package com.parsuomash.voyager_safe_args_processor

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

internal class VisualizationAnnotationVisitor(
  private val declarations: MutableList<KSFunctionDeclaration>
) : KSVisitorVoid() {
  override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
    declarations += function
  }
}
