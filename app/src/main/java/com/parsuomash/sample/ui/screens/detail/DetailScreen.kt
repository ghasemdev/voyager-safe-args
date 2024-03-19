package com.parsuomash.sample.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> T.encode(): String = Json.encodeToString(this)
inline fun <reified T> String.decode(): T = Json.decodeFromString(this)

@Serializable
data class Detail(val title: String = "", val price: Int = 0)

data class DetailScreen(
  private val id: String,
  private val _detail: String
) : Screen {
  @Composable
  override fun Content() {
    val detail = remember(_detail) { _detail.decode<Detail>() }

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(text = "id $id")
      Text(text = "detail $detail")
    }
  }
}
