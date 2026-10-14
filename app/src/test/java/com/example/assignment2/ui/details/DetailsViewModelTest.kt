package com.example.assignment2.ui.details

import androidx.lifecycle.SavedStateHandle
import com.example.assignment2.data.model.Entity
import com.example.assignment2.data.model.EntityField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetailsViewModelTest {

    @Test
    fun `rebuilds the entity from the intent extras`() {
        // Given - the same extras that EntityExtras.newDetailsIntent puts in the intent
        val savedStateHandle = SavedStateHandle(
            mapOf(
                EntityExtras.KEYS to arrayOf("name", "habitat"),
                EntityExtras.LABELS to arrayOf("Name", "Habitat"),
                EntityExtras.VALUES to arrayOf("Koala", "Eucalyptus forest"),
                EntityExtras.DESCRIPTION to "A tree-dwelling marsupial."
            )
        )

        // When
        val viewModel = DetailsViewModel(savedStateHandle)

        // Then
        val expected = Entity(
            fields = listOf(
                EntityField(key = "name", label = "Name", value = "Koala"),
                EntityField(key = "habitat", label = "Habitat", value = "Eucalyptus forest")
            ),
            description = "A tree-dwelling marsupial."
        )
        assertEquals(expected, viewModel.entity)
    }

    @Test
    fun `entity is null when the extras are missing`() {
        val viewModel = DetailsViewModel(SavedStateHandle())

        assertNull(viewModel.entity)
    }

    @Test
    fun `entity is null when the arrays don't line up`() {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                EntityExtras.KEYS to arrayOf("name", "habitat"),
                EntityExtras.LABELS to arrayOf("Name"),
                EntityExtras.VALUES to arrayOf("Koala", "Eucalyptus forest")
            )
        )

        assertNull(DetailsViewModel(savedStateHandle).entity)
    }
}
