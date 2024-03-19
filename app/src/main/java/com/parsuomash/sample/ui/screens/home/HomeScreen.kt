package com.parsuomash.sample.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.parsuomash.sample.ui.screens.detail.Detail
import com.parsuomash.sample.ui.screens.detail.DetailScreen
import com.parsuomash.sample.ui.screens.detail.encode

class HomeScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Button(
        onClick = {
          navigator.push(
            DetailScreen(
              id = "1",
              _detail = Detail(title = "some title", price = 1000).encode()
            )
          )
        }
      ) {
        Text(text = "go to detail")
      }
    }
  }
}
