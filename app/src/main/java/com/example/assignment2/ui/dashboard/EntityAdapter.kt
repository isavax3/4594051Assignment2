package com.example.assignment2.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2.data.model.Entity
import com.example.assignment2.databinding.ItemEntityBinding

// connects the list of entities to the RecyclerView. the click is passed in as a lambda
// so the adapter doesn't need to know about the details screen
class EntityAdapter(
    private val onItemClick: (Entity) -> Unit
) : RecyclerView.Adapter<EntityViewHolder>() {

    private var entities: List<Entity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(entities[position], onItemClick)
    }

    override fun getItemCount(): Int = entities.size

    // the whole list arrives at once from the api, so refreshing everything is fine here
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newEntities: List<Entity>) {
        entities = newEntities
        notifyDataSetChanged()
    }
}
