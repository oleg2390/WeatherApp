package com.example.weatherapp.navigation

sealed interface AppDestination {
    val route: String

    data object Home : AppDestination {
        override val route: String = "home"
    }

    data object City : AppDestination {
        override val route: String = "city"
    }
}
