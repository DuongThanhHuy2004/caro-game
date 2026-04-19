package com.example.caro.model

enum class Player { X, O }

fun Player.opponent(): Player = if (this == Player.X) Player.O else Player.X