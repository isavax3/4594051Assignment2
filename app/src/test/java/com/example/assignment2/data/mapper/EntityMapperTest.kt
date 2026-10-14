package com.example.assignment2.data.mapper

import com.example.assignment2.data.model.EntityField
import org.junit.Assert.assertEquals
import org.junit.Test

class EntityMapperTest {

    private val mapper = EntityMapper()

    @Test
    fun `description is taken out of the fields`() {
        val entity = mapper.map(
            mapOf("name" to "Koala", "habitat" to "Forest", "description" to "A tree-dwelling marsupial.")
        )

        assertEquals("A tree-dwelling marsupial.", entity.description)
        assertEquals(listOf("name", "habitat"), entity.fields.map { it.key })
    }

    @Test
    fun `description is found even with a capital letter`() {
        val entity = mapper.map(mapOf("name" to "Koala", "Description" to "Sleeps a lot."))

        assertEquals("Sleeps a lot.", entity.description)
        assertEquals(1, entity.fields.size)
    }

    @Test
    fun `missing description is an empty string`() {
        assertEquals("", mapper.map(mapOf("name" to "Koala")).description)
    }

    @Test
    fun `fields keep the order from the api`() {
        val entity = mapper.map(linkedMapOf("property1" to "a", "property2" to "b", "property3" to "c"))

        assertEquals(
            listOf(
                EntityField("property1", "Property 1", "a"),
                EntityField("property2", "Property 2", "b"),
                EntityField("property3", "Property 3", "c")
            ),
            entity.fields
        )
    }

    @Test
    fun `title is the first field and the summary is the rest`() {
        val entity = mapper.map(linkedMapOf("name" to "Koala", "habitat" to "Forest", "diet" to "Leaves"))

        assertEquals("Koala", entity.title)
        assertEquals(listOf("habitat", "diet"), entity.summaryFields.map { it.key })
    }

    @Test
    fun `property names are turned into readable labels`() {
        assertEquals("Artist name", mapper.toLabel("artistName"))
        assertEquals("Release year", mapper.toLabel("release_year"))
        assertEquals("Property 1", mapper.toLabel("property1"))
        assertEquals("Name", mapper.toLabel("name"))
    }

    @Test
    fun `whole numbers don't show a decimal point`() {
        assertEquals("1990", mapper.formatValue(1990.0))
        assertEquals("4.5", mapper.formatValue(4.5))
    }

    @Test
    fun `other values are turned into text`() {
        assertEquals("-", mapper.formatValue(null))
        assertEquals("true", mapper.formatValue(true))
        assertEquals("red, green", mapper.formatValue(listOf("red", "green")))
        assertEquals("Monet", mapper.formatValue("Monet"))
    }
}
