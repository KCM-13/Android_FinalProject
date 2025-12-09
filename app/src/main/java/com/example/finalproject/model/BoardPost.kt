package com.example.finalproject.model

data class BoardPost(
    val title: String = "",
    val writer: String = "",
    val facility: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
