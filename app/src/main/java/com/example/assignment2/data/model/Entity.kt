package com.example.assignment2.data.model

// one property of an entity, e.g. key = "artistName", label = "Artist name", value = "Monet"
data class EntityField(
    val key: String,
    val label: String,
    val value: String
)

// one item from the dashboard. the description is kept separate because the list
// only shows the summary and the details screen shows the description as well
data class Entity(
    val fields: List<EntityField>,
    val description: String
) {
    // the first property is used as the title of the card
    val title: String
        get() = fields.firstOrNull()?.value.orEmpty()

    // the rest of the properties are shown under the title
    val summaryFields: List<EntityField>
        get() = fields.drop(1)
}
