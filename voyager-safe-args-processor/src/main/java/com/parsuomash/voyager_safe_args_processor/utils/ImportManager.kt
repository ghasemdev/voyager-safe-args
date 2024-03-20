package com.parsuomash.voyager_safe_args_processor.utils

internal class ImportManager {

  private val imports = mutableListOf<String>()

  init {
    append(COMPOSABLE)
    append(VOYAGER_SCREEN)
  }

  fun append(dependency: String) {
    imports.add(dependency)
  }

  fun finalize(): String = imports.sorted().joinToString(separator = "\n") { "import $it" }

  companion object {
    const val VOYAGER_SCREEN = "cafe.adriel.voyager.core.screen.Screen"
    const val COMPOSABLE = "androidx.compose.runtime.Composable"
  }
}
