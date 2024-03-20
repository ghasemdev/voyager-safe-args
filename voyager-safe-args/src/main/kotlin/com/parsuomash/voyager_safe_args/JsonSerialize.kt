package com.parsuomash.voyager_safe_args

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> T.encode(): String = Json.encodeToString(this)
inline fun <reified T> String.decode(): T = Json.decodeFromString(this)
