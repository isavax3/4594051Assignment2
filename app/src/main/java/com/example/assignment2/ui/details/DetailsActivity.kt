package com.example.assignment2.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.assignment2.R
import com.example.assignment2.data.model.Entity
import com.example.assignment2.databinding.ActivityDetailsBinding
import com.example.assignment2.databinding.ItemDetailFieldBinding
import com.example.assignment2.ui.common.padForSystemBars
import com.example.assignment2.ui.common.setUpEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

// Screen 3 - Details
// Shows every property of the entity that was tapped, plus its description.
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.padForSystemBars()

        binding.buttonBack.setOnClickListener { finish() }

        val entity = viewModel.entity
        if (entity == null) {
            // shouldn't happen, but if the extras are missing there's nothing to show
            finish()
            return
        }
        showEntity(entity)
    }

    private fun showEntity(entity: Entity) {
        binding.textTitle.text = entity.title

        // one row (item_detail_field.xml) for each property, with a line between rows
        val inflater = LayoutInflater.from(this)
        entity.fields.forEachIndexed { index, field ->
            val row = ItemDetailFieldBinding.inflate(inflater, binding.containerFields, false)
            row.divider.isVisible = index > 0
            row.textLabel.text = field.label
            row.textValue.text = field.value
            binding.containerFields.addView(row.root)
        }

        binding.textDescription.text = entity.description.ifBlank { getString(R.string.no_description) }
    }
}
