package com.example.assignment2.ui.login

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assignment2.R
import com.example.assignment2.data.remote.ApiConfig
import com.example.assignment2.databinding.ActivityLoginBinding
import com.example.assignment2.ui.common.padForSystemBars
import com.example.assignment2.ui.common.setUpEdgeToEdge
import com.example.assignment2.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Screen 1 - Login
// The user types their student id and first name. The LoginViewModel does the work,
// this activity only passes on the button taps and draws whatever state it gets back.
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.padForSystemBars()

        val location = ApiConfig.CLASS_LOCATION.replaceFirstChar { it.uppercase() }
        binding.textLocation.text = getString(R.string.login_location_note, location)

        setUpListeners()
        observeState()
    }

    private fun setUpListeners() {
        binding.buttonLogin.setOnClickListener { submit() }

        // pressing Done on the keyboard does the same as the button
        binding.inputFirstName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else {
                false
            }
        }

        binding.inputStudentId.doAfterTextChanged { viewModel.clearError() }
        binding.inputFirstName.doAfterTextChanged { viewModel.clearError() }
    }

    private fun submit() {
        hideKeyboard()
        viewModel.login(
            studentId = binding.inputStudentId.text.toString(),
            firstName = binding.inputFirstName.text.toString()
        )
    }

    // collects the state only while the screen is visible
    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: LoginUiState) {
        val loading = state is LoginUiState.Loading
        binding.buttonLogin.isEnabled = !loading
        binding.buttonLogin.text = getString(if (loading) R.string.logging_in else R.string.log_in)
        binding.progressLogin.isVisible = loading
        binding.textSlowNote.isVisible = loading
        binding.layoutStudentId.isEnabled = !loading
        binding.layoutFirstName.isEnabled = !loading

        binding.textError.isVisible = state is LoginUiState.Error
        if (state is LoginUiState.Error) {
            binding.textError.text = getString(state.messageId)
        }

        if (state is LoginUiState.Success) {
            openDashboard(state.keypass)
        }
    }

    // finish() closes login so pressing back on the dashboard doesn't come back here
    private fun openDashboard(keypass: String) {
        viewModel.onNavigated()
        startActivity(DashboardActivity.newIntent(this, keypass))
        finish()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
