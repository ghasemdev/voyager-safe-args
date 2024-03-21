package com.parsuomash.sample.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.parsuomash.voyager_safe_args.Screen
import com.parsuomash.voyager_safe_args.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class Detail(val title: String = "", val price: Int = 0)

@Screen("DetailDestination")
@Composable
fun DetailScreen(
  @ScreenKey id: Long,
  detail: Detail
  // TODO Add List, Array, ...
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(text = "id $id")
    Text(text = "detail $detail")
  }
}
