package com.example.assignment2.ui.dashboard

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2.data.model.Entity
import com.example.assignment2.databinding.ItemEntityBinding

// holds the views for one card in the list (item_entity.xml) and fills them in
class EntityViewHolder(
    private val binding: ItemEntityBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: Entity, onItemClick: (Entity) -> Unit) {
        binding.textTitle.text = entity.title

        // summary = every property except the description, one per line, e.g. "Artist: Monet"
        val summary = buildSpannedString {
            entity.summaryFields.forEachIndexed { index, field ->
                if (index > 0) append("\n")
                bold { append(field.label) }
                append(": ")
                append(field.value)
            }
        }
        binding.textSummary.text = summary
        binding.textSummary.isVisible = entity.summaryFields.isNotEmpty()

        binding.root.setOnClickListener { onItemClick(entity) }
    }
}
