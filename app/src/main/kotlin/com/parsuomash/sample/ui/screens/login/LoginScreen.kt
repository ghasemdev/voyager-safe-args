package com.parsuomash.sample.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import com.parsuomash.voyager_safe_args.Screen
import kotlinx.serialization.Serializable

@Serializable
data class LoginData(val title: String = "", val price: Int = 0)
data class LoginData2(val title: String = "", val price: Int = 0)

@Screen
@Composable
@NonRestartableComposable
internal fun LoginScreen(
  id: String,
  id2: Int,
  id3: Boolean,
  data: LoginData,
  id4: Float,
  id5: Double,
  id6: Long,
  data2: LoginData2
) {
  Column {
    Text(text = "Login $id")
    Text(text = "Login $id2")
    Text(text = "Login $id3")
    Text(text = "Login $id4")
    Text(text = "Login $id5")
    Text(text = "Login $id6")
    Text(text = "Login $data")
    Text(text = "Login $data2")
  }
}
