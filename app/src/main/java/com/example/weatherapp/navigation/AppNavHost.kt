package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.feature.city.presentation.CityViewModel
import com.example.weatherapp.feature.city.presentation.CityRoute
import com.example.weatherapp.feature.home.presentation.HomeRoute
import com.example.weatherapp.feature.home.presentation.HomeViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ){
        composable(route = AppDestination.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeRoute(
                viewModel = viewModel,
                onOpenCity = { navController.navigate(AppDestination.City.route) }
            )
        }

        composable(route = AppDestination.City.route) {
            val viewModel: CityViewModel = hiltViewModel()
            CityRoute(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}