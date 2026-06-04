package dev.meluhdy.melodia.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val ISO8601_FORMAT = "yyyy-MM-dd'T'HH-mm-ss"

@JvmName("toIsoStringNullable")
fun Date?.toIsoString(): String? {
    return this?.toIsoString()
}

fun Date.toIsoString(): String {
    val dateFormat: DateFormat = SimpleDateFormat(ISO8601_FORMAT, Locale.US)
    return dateFormat.format(this)
}