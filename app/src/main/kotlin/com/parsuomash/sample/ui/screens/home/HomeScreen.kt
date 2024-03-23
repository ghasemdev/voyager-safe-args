package com.parsuomash.sample.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.parsuomash.sample.ui.screens.types.TypesData
import com.parsuomash.voyager_safe_args.CodeGenerationVisibility
import com.parsuomash.voyager_safe_args.Screen
import com.parsuomash.voyager_safe_args.TypesScreen

@Screen(visibility = CodeGenerationVisibility.PUBLIC)
@Composable
internal fun HomeScreen() {
  val navigator = LocalNavigator.currentOrThrow

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Button(
      onClick = {
        navigator.push(
          TypesScreen(
            id = "quod",
            id2 = 8236,
            id3 = false,
            data = TypesData(title = "causae", price = 6431),
            id4 = 4.5f,
            id5 = 6.7,
            id6 = 1974
          )
        )
      }
    ) {
      Text(text = "go to detail")
    }
  }
}
