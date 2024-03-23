package com.parsuomash.voyager_safe_args_processor.internal

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

internal class ParamSerializerAnnotationVisitor(
  private val declarations: MutableList<KSClassDeclaration>
) : KSVisitorVoid() {
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
    declarations += classDeclaration
  }
}
