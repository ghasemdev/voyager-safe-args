package com.parsuomash.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.parsuomash.sample.ui.screens.detail.Detail
import com.parsuomash.sample.ui.theme.VoyagerSafeArgsTheme
import com.parsuomash.voyager_safe_args.DetailDestination
import com.parsuomash.voyager_safe_args.HomeScreen
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      VoyagerSafeArgsTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          Navigator(HomeScreen) { navigator ->
            SlideTransition(navigator)
            HandleIntent()
          }
        }
      }
    }
  }

  @Composable
  private fun HandleIntent() {
    var isIntentServe by rememberSaveable { mutableStateOf(false) }
    val componentActivity = LocalContext.current as ComponentActivity
    val navigator = LocalNavigator.currentOrThrow

    LaunchedEffect(Unit) {
      callbackFlow<Intent> {
        val currentIntent = componentActivity.intent
        if (currentIntent?.data != null) {
          isIntentServe = false
          trySend(currentIntent)
        }
        val consumer = Consumer<Intent> { trySend(it) }
        componentActivity.addOnNewIntentListener(consumer)
        awaitClose { componentActivity.removeOnNewIntentListener(consumer) }
      }.collectLatest {
        if (!isIntentServe) {
          handleIntentAction(it, navigator)
          componentActivity.clearIntent()
          isIntentServe = true
        }
      }
    }
  }

  private fun ComponentActivity.clearIntent() {
    if (intent != null) {
      intent.data = null
      intent.flags = 0
      intent.action = ""
      intent.putExtras(Bundle())
      intent = null
    }
  }

  // adb shell am start -a android.intent.action.VIEW -d "voyager://details/20/?title=hello\&price=234" com.parsuomash.sample
  private fun handleIntentAction(
    intent: Intent,
    navigator: Navigator,
  ) {
    if (intent.dataString?.contains("https://www.voyager.com/details") == true) {
      val matchResult = pattern.find(intent.dataString!!)
      if (matchResult != null) {
        val id = matchResult.groups["id"]?.value ?: return
        val title = matchResult.groups["title"]?.value ?: return
        val price = matchResult.groups["price"]?.value ?: return

        navigator.popAll()
        navigator.push(
          DetailDestination(
            id = id.toLong(),
            detail = Detail(title = title, price = price.toInt())
          )
        )
      }
    }
  }

  companion object {
    val pattern =
      "https://www.voyager.com/details/(?<id>\\w+)/\\?title=(?<title>\\w+)&price=(?<price>\\w+)".toRegex()
  }
}
