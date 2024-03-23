package com.parsuomash.sample.ui.screens.types

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.parsuomash.voyager_safe_args.CodeGenerationVisibility
import com.parsuomash.voyager_safe_args.Screen
import com.parsuomash.voyager_safe_args.ScreenKey
import java.io.Serializable

data class JavaSerializableDataClass(val title: String = "", val price: Int = 0) : Serializable

@kotlinx.serialization.Serializable
data class KotlinSerializableDataClass(val title: String = "", val price: Int = 0)

enum class EnumData {
  A, B, C
}

@Screen(visibility = CodeGenerationVisibility.PUBLIC, name = "TypeDestination")
@Composable
internal fun TypesScreen(
  javaDataClass: JavaSerializableDataClass,
  kotlinDataClass: KotlinSerializableDataClass,
  enum: EnumData,
  @ScreenKey id: String,
  id2: Boolean,
  id3: Int,
  id4: Long,
  id5: Float,
  id6: Double,
  array: BooleanArray,
  array2: IntArray,
  array3: LongArray,
  array4: FloatArray,
  array5: DoubleArray,
  array6: ByteArray,
  array7: Array<Any>,
  matrix: Array<Array<Int>>,
  list: List<String>,
  list2: List<Boolean>,
  list3: List<Int>,
  list4: List<Long>,
  list5: List<Float>,
  list6: List<Double>,
  set: Set<String>,
  map: Map<String, String>,
) {
  Column {
    Text(text = "Login $javaDataClass")
    Text(text = "Login $kotlinDataClass")
    Text(text = "Login $enum")
    Text(text = "Login $id")
    Text(text = "Login $id2")
    Text(text = "Login $id3")
    Text(text = "Login $id4")
    Text(text = "Login $id5")
    Text(text = "Login $id6")
    Text(text = "Login ${array.joinToString()}")
    Text(text = "Login ${array2.joinToString()}")
    Text(text = "Login ${array3.joinToString()}")
    Text(text = "Login ${array4.joinToString()}")
    Text(text = "Login ${array5.joinToString()}")
    Text(text = "Login ${array6.joinToString()}")
    Text(text = "Login ${array7.joinToString()}")
    Text(text = "Login ${buildString { matrix.forEach { append(it.joinToString()) } }}")
    Text(text = "Login $list")
    Text(text = "Login $list2")
    Text(text = "Login $list3")
    Text(text = "Login $list4")
    Text(text = "Login $list5")
    Text(text = "Login $list6")
    Text(text = "Login $set")
    Text(text = "Login $map")
  }
}
