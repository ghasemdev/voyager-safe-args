package com.parsuomash.sample.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.parsuomash.sample.ui.screens.types.EnumData
import com.parsuomash.sample.ui.screens.types.JavaSerializableDataClass
import com.parsuomash.sample.ui.screens.types.KotlinSerializableDataClass
import com.parsuomash.voyager_safe_args.CodeGenerationVisibility
import com.parsuomash.voyager_safe_args.Screen
import com.parsuomash.voyager_safe_args.TypeDestination

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
          TypeDestination(
            javaDataClass = JavaSerializableDataClass(title = "java", price = 1),
            kotlinDataClass = KotlinSerializableDataClass(title = "kotlin", price = 2),
            enum = EnumData.C,
            id = "asds",
            id2 = false,
            id3 = 0,
            id4 = 0L,
            id5 = 0.0f,
            id6 = 0.0,
            array = booleanArrayOf(true, false),
            array2 = intArrayOf(1, 2, 3),
            array3 = longArrayOf(1, 2, 3, 4),
            array4 = floatArrayOf(1f, 2f, 3f),
            array5 = doubleArrayOf(1.0, 2.0, 3.0),
            array6 = byteArrayOf(1, 2, 3),
            array7 = arrayOf("lk", "sfd", 4, 5, true),
            matrix = arrayOf(arrayOf(1, 2), arrayOf(3, 4)),
            list = mutableListOf("lk", "sfd"),
            list2 = arrayListOf(true, false),
            list3 = listOf(1, 2, 3),
            list4 = listOf(1, 2, 3, 4),
            list5 = listOf(1f, 2f, 3f),
            list6 = listOf(1.0, 2.0, 3.0),
            set = setOf("lk", "sfd"),
            map = mapOf("a" to "1", "b" to "2"),
          )
        )
        Log.d("DDDD", "${navigator.items}")
      }
    ) {
      Text(text = "go to detail")
    }
  }
}
