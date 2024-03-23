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
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import cafe.adriel.voyager.navigator.Navigator
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
            HandleIntent(this, navigator)
          }
        }
      }
    }
  }

  @Composable
  private fun HandleIntent(context: MainActivity, navigator: Navigator) {
    LaunchedEffect(Unit) {
      callbackFlow<Intent> {
        val componentActivity = context as ComponentActivity
        val currentIntent = componentActivity.intent
        if (currentIntent?.data != null) {
          trySend(currentIntent)
        }
        val consumer = Consumer<Intent> { trySend(it) }
        componentActivity.addOnNewIntentListener(consumer)
        awaitClose { componentActivity.removeOnNewIntentListener(consumer) }
      }.collectLatest { handleIntentAction(it, navigator) }
    }
  }

  private fun handleIntentAction(intent: Intent, navigator: Navigator) {
    if (intent.dataString?.contains("voyager://details") == true) {
      val matchResult = pattern.find(intent.dataString!!)
      if (matchResult != null) {
        val id = matchResult.groupValues[1]
        val title = matchResult.groupValues[2]
        val price = matchResult.groupValues[3]

        navigator.replaceAll(
          listOf(
            HomeScreen,
            DetailDestination(
              id = id.toLong(),
              detail = Detail(title = title, price = price.toInt())
            )
          )
        )
      }
    }
  }

  companion object {
    val pattern = Regex("voyager://details/(.*?)/\\?title=(.*?)&price=(.*?)")
  }
}
