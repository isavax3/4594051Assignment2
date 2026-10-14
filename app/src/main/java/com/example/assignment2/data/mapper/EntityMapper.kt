package com.example.assignment2.data.mapper

import com.example.assignment2.data.model.Entity
import com.example.assignment2.data.model.EntityField
import javax.inject.Inject
import kotlin.math.abs

// turns one raw entity from the api (a map of property name -> value) into an Entity
// the app can show. @Inject constructor means Hilt knows how to make this class by itself.
class EntityMapper @Inject constructor() {

    fun map(raw: Map<String, Any?>): Entity {
        var description = ""
        val fields = mutableListOf<EntityField>()

        for ((key, value) in raw) {
            if (key.equals(DESCRIPTION_KEY, ignoreCase = true)) {
                description = formatValue(value)
            } else {
                fields.add(EntityField(key = key, label = toLabel(key), value = formatValue(value)))
            }
        }
        return Entity(fields = fields, description = description)
    }

    // makes the property names readable, e.g. "artistName" -> "Artist name",
    // "release_year" -> "Release year" and "property1" -> "Property 1"
    fun toLabel(key: String): String {
        val words = key
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace(Regex("([A-Za-z])([0-9])"), "$1 $2")
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .lowercase()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
        return words.joinToString(" ").replaceFirstChar { it.uppercase() }
    }

    // moshi reads every json number as a Double, so 1990 would show as "1990.0".
    // whole numbers get turned back into "1990", other values are just shown as text
    fun formatValue(value: Any?): String = when (value) {
        null -> MISSING_VALUE
        is Double -> if (value % 1.0 == 0.0 && abs(value) < 1e15) value.toLong().toString() else value.toString()
        is List<*> -> value.joinToString(", ") { formatValue(it) }
        is Map<*, *> -> value.entries.joinToString(", ") { "${it.key}: ${formatValue(it.value)}" }
        else -> value.toString()
    }

    companion object {
        const val DESCRIPTION_KEY = "description"
        const val MISSING_VALUE = "-"
    }
}
