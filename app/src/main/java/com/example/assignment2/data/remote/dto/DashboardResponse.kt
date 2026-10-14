package com.example.assignment2.data.remote.dto

// the dashboard response. every topic has different property names, so each entity is
// read as a map of name -> value instead of a fixed class. that way it works for any topic.
data class DashboardResponse(
    val entities: List<Map<String, Any?>>,
    val entityTotal: Int? = null
)
