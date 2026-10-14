package com.example.assignment2.ui.details

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import com.example.assignment2.data.model.Entity
import com.example.assignment2.data.model.EntityField

// puts an entity into an intent as plain string arrays (keys, labels, values and the
// description) and reads it back out on the details screen. strings and string arrays
// can go in an intent as they are, so the Entity class doesn't need to be Parcelable.
object EntityExtras {
    const val KEYS = "entity_keys"
    const val LABELS = "entity_labels"
    const val VALUES = "entity_values"
    const val DESCRIPTION = "entity_description"

    fun newDetailsIntent(context: Context, entity: Entity): Intent =
        Intent(context, DetailsActivity::class.java)
            .putExtra(KEYS, entity.fields.map { it.key }.toTypedArray())
            .putExtra(LABELS, entity.fields.map { it.label }.toTypedArray())
            .putExtra(VALUES, entity.fields.map { it.value }.toTypedArray())
            .putExtra(DESCRIPTION, entity.description)

    // Hilt gives the details viewmodel the intent extras in a SavedStateHandle.
    // returns null if something is missing so the screen can close instead of crashing
    fun readEntity(savedStateHandle: SavedStateHandle): Entity? {
        val keys = savedStateHandle.get<Array<String>>(KEYS) ?: return null
        val labels = savedStateHandle.get<Array<String>>(LABELS) ?: return null
        val values = savedStateHandle.get<Array<String>>(VALUES) ?: return null
        if (keys.size != labels.size || keys.size != values.size) return null

        val fields = keys.indices.map { i -> EntityField(key = keys[i], label = labels[i], value = values[i]) }
        val description = savedStateHandle.get<String>(DESCRIPTION).orEmpty()
        return Entity(fields = fields, description = description)
    }
}
