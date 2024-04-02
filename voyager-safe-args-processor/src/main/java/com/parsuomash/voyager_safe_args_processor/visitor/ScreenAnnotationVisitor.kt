package com.parsuomash.voyager_safe_args_processor.visitor

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

internal class ScreenAnnotationVisitor(
  private val declarations: MutableList<KSFunctionDeclaration>
) : KSVisitorVoid() {
  override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
    declarations += function
  }
}
