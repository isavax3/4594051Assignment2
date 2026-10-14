package com.example.assignment2.ui.dashboard

import com.example.assignment2.data.model.Entity
import com.example.assignment2.data.model.EntityField
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// same idea as the adapter test from the session 8 slides
class EntityAdapterTest {

    private lateinit var adapter: EntityAdapter

    private val koala = Entity(
        fields = listOf(EntityField(key = "name", label = "Name", value = "Koala")),
        description = "A tree-dwelling marsupial."
    )

    @Before
    fun setUp() {
        // spyk keeps the real adapter but lets notifyDataSetChanged be faked,
        // because it needs a real RecyclerView to work
        adapter = spyk(EntityAdapter(onItemClick = {}))
        every { adapter.notifyDataSetChanged() } returns Unit
    }

    @Test
    fun `starts with no items`() {
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `setData updates the item count and refreshes the list`() {
        // Given
        val entities = listOf(koala, koala.copy(description = "Another one"), koala.copy(description = "And another"))

        // When
        adapter.setData(entities)

        // Then
        assertEquals(3, adapter.itemCount)
        verify { adapter.notifyDataSetChanged() }
    }

    @Test
    fun `setData replaces the old list`() {
        adapter.setData(listOf(koala, koala))
        adapter.setData(listOf(koala))

        assertEquals(1, adapter.itemCount)
    }
}
