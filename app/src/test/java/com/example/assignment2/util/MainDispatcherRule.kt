package com.example.assignment2.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// viewModelScope runs on Dispatchers.Main, which doesn't exist in a normal JVM test.
// this rule swaps Main for a test dispatcher before each test and puts it back after,
// so I don't have to repeat setMain/resetMain in every test class.
// StandardTestDispatcher doesn't run coroutines until advanceUntilIdle() is called,
// which lets the tests check the Loading state too.
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
