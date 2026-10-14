package com.example.assignment2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp starts Hilt's code generation. This class is the root of the
// dependency graph, so every other class can get its dependencies from Hilt.
@HiltAndroidApp
class Assignment2App : Application()
