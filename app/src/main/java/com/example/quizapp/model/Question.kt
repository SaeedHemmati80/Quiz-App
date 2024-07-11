package com.example.quizapp.model


data class Question(
    val image_url: String = "",
    val question: String = "Choose the correct option.",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val ans: String = "",

    ) {
}