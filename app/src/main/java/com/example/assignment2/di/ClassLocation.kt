package com.example.assignment2.di

import javax.inject.Qualifier

// a qualifier so Hilt knows which String to inject. without it Hilt wouldn't know
// what "a String" means, with it this one is always my class location
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ClassLocation
