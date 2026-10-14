package com.example.assignment2.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.assignment2.data.model.Entity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// holds the entity the user tapped on. it's rebuilt from the intent extras, so it
// survives things like rotating the phone
@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val entity: Entity? = EntityExtras.readEntity(savedStateHandle)
}
