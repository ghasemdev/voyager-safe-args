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
import com.parsuomash.sample.ui.screens.detail.Detail
import com.parsuomash.voyager_safe_args.DetailDestination
import com.parsuomash.voyager_safe_args.Screen

@Screen(key = "1234")
@Composable
fun HomeScreen() {
  val navigator = LocalNavigator.currentOrThrow

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Button(
      onClick = {
        navigator.push(
          DetailDestination(
            id = 1,
            detail = Detail(title = "some title", price = 1000)
          )
        )
      }
    ) {
      Text(text = "go to detail")
    }
  }
}
