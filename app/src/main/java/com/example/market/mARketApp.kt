package com.example.market

import BottomNavItem
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.market.presentation.view.ARScreen
import com.example.market.presentation.view.AccountScreen
import com.example.market.presentation.view.AlbumScreen
import com.example.market.presentation.view.CategoriesScreen
import com.example.market.presentation.view.CreateScreen
import com.example.market.presentation.view.HomeScreen
import com.example.market.presentation.view.SignInScreen
import com.example.market.presentation.view.SignUpScreen
import com.example.market.presentation.view.SplashScreen
import com.example.market.presentation.viewModel.AlbumViewModel
import com.example.market.presentation.viewModel.CreateViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.MARketTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun mARketApp() {
    MARketTheme {
        Surface(color = Beige) {
            val appState = rememberAppState()

            Scaffold { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    notesGraph(appState)
                }
            }
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        MARketAppState(navController)
    }

fun NavGraphBuilder.notesGraph(appState: MARketAppState) {

    composable(SIGN_IN_SCREEN) {
        SignInScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
    composable(HOME_SCREEN) {
        HomeScreen(
            navigate = { route -> appState.navigate(route) }
        )
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(CATEGORIES_SCREEN) {
        CategoriesScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })

    }

    composable(ACCOUNT_SCREEN) {
        AccountScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(CREATE_LISTING_SCREEN) {
        CreateScreen(
            navigate = { route -> appState.navigate(route) }
        )
    }
    composable(ALBUM_SCREEN) {
        AlbumScreen( viewModel = AlbumViewModel(Dispatchers.Default))
    }

    composable(AR_SCREEN) {
        ARScreen()
    }

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", HOME_SCREEN, Icons.Filled.Home),
        BottomNavItem("Categories", CATEGORIES_SCREEN, Icons.Filled.Menu),
        BottomNavItem("Create", CREATE_LISTING_SCREEN, Icons.Filled.Add),
        BottomNavItem("Browse", BROWSE_SCREEN, Icons.Filled.Search),
        BottomNavItem("Account", ACCOUNT_SCREEN, Icons.Filled.Person)
    )

    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreenView(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        BottomNavHost(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun BottomNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN,
        modifier = modifier
    ) {
        composable(HOME_SCREEN) { }
        composable(CATEGORIES_SCREEN) { /* Add CategoriesScreen composable */ }
        composable(CREATE_LISTING_SCREEN) { /* Add CreateListingScreen composable */ }
        composable(BROWSE_SCREEN) { /* Add BrowseScreen composable */ }
        composable(ACCOUNT_SCREEN) { /* Add AccountScreen composable */ }
    }
}