package com.lesterade.oneira.gameHandling

fun Float.trim(number : Int): String {
    var count = 0
    return this.toString().takeWhile {
        if (count > 0)
            count++
        if (it == '.')
            count = 1
        (count < (number + 1))
    }
}

fun String.format(value : Int): String {
    return this.replaceFirst("%d", value.toString())
}

fun String.format(value : String): String {
    return this.replaceFirst("%s", value)
}

fun String.format(value: Float): String {
    return this.replaceFirst("%f", value.trim(2))
}