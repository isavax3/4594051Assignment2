package com.example.assignment2.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assignment2.R
import com.example.assignment2.data.model.Entity
import com.example.assignment2.databinding.ActivityDashboardBinding
import com.example.assignment2.ui.common.padForSystemBars
import com.example.assignment2.ui.common.setUpEdgeToEdge
import com.example.assignment2.ui.details.EntityExtras
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Screen 2 - Dashboard
// Shows the entities for the user's topic in a RecyclerView. Tapping one opens Details.
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val adapter = EntityAdapter(onItemClick = ::openDetails)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.padForSystemBars()

        // the keypass is the topic name, e.g. "animals" -> "Animals"
        binding.textTopic.text = viewModel.keypass.replaceFirstChar { it.uppercase() }
        binding.recyclerEntities.adapter = adapter

        binding.buttonRetry.setOnClickListener { viewModel.loadEntities() }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: DashboardUiState) {
        binding.groupLoading.isVisible = state is DashboardUiState.Loading
        binding.groupError.isVisible = state is DashboardUiState.Error
        binding.recyclerEntities.isVisible = state is DashboardUiState.Success
        binding.textCount.isVisible = state is DashboardUiState.Success

        when (state) {
            is DashboardUiState.Loading -> Unit
            is DashboardUiState.Error -> binding.textError.text = getString(state.messageId)
            is DashboardUiState.Success -> {
                adapter.setData(state.entities)
                binding.textCount.text = getString(R.string.item_count, state.total)
                binding.textEmpty.isVisible = state.entities.isEmpty()
            }
        }
        if (state !is DashboardUiState.Success) binding.textEmpty.isVisible = false
    }

    private fun openDetails(entity: Entity) {
        startActivity(EntityExtras.newDetailsIntent(this, entity))
    }

    companion object {
        fun newIntent(context: Context, keypass: String): Intent =
            Intent(context, DashboardActivity::class.java)
                .putExtra(DashboardViewModel.KEY_KEYPASS, keypass)
    }
}
