package com.parsuomash.sample.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.parsuomash.voyager_safe_args.Screen

data class TypesData(val title: String = "", val price: Int = 0)

@Screen
@Composable
internal fun TypesScreen(
  id: String,
  id2: Int,
  id3: Boolean,
  data: TypesData,
  id4: Float,
  id5: Double,
  id6: Long
) {
  Column {
    Text(text = "Login $id")
    Text(text = "Login $id2")
    Text(text = "Login $id3")
    Text(text = "Login $id4")
    Text(text = "Login $id5")
    Text(text = "Login $id6")
    Text(text = "Login $data")
  }
}
