package com.example.assignment2.data.model

// what the dashboard screen needs: the topic's entities and how many there are
data class Dashboard(
    val entities: List<Entity>,
    val total: Int
)
