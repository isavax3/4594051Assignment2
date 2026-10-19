package com.example.assignment2.ui.common

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// helpers all 3 screens use so the same code isn't repeated in each activity

// draws the app behind the status bar and nav bar. "light" style = dark icons,
// which suits the cream background even if the phone is in dark mode
fun ComponentActivity.setUpEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    )
}

// adds the status bar, nav bar and keyboard height on top of the view's own padding
// so nothing ends up hidden behind them
fun View.padForSystemBars() {
    val startPadding = paddingLeft
    val topPadding = paddingTop
    val endPadding = paddingRight
    val bottomPadding = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        view.setPadding(
            startPadding + bars.left,
            topPadding + bars.top,
            endPadding + bars.right,
            bottomPadding + bars.bottom
        )
        insets
    }
}
