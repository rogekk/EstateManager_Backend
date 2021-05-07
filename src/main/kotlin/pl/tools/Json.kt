package pl.tools

import com.snitch.extensions.json

fun <T> l(vararg a: T) = a.asList()

fun json(obj: JsonBuilder.() -> Unit): Map<String, Any> {
    return JsonBuilder().apply { obj() }.map
}

object l {
    operator fun <T> get(vararg yo: T) = listOf(*yo)
}

class JsonBuilder {
    val map = mutableMapOf<String, Any>()
    infix fun String.`_`(str: String) {
        map[this] = str
    }

    infix fun String.`_`(i: Number) {
        map[this] = i
    }

    infix fun <T> String.`_`(i: List<T>) {
        map[this] = i
    }

    infix fun String.`_`(i: Map<String, Any>) {
        map[this] = i
    }
}
