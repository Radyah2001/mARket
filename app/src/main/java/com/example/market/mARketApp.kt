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
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.market.model.Category
import com.example.market.model.Condition
import com.example.market.model.Listing
import com.example.market.presentation.view.ARScreen
import com.example.market.presentation.view.AccountScreen
import com.example.market.presentation.view.AlbumScreen
import com.example.market.presentation.view.CategoriesScreen
import com.example.market.presentation.view.CreateScreen
import com.example.market.presentation.view.HomeScreen
import com.example.market.presentation.view.ListingDetailsScreen
import com.example.market.presentation.view.ListingsScreen
import com.example.market.presentation.view.ModelViewerScreen
import com.example.market.presentation.view.SignInScreen
import com.example.market.presentation.view.SignUpScreen
import com.example.market.presentation.view.SplashScreen
import com.example.market.presentation.viewModel.AlbumViewModel
import com.example.market.presentation.viewModel.CreateViewModel
import com.example.market.presentation.viewModel.ListingSharedViewModel
import com.example.market.ui.theme.Beige
import com.example.market.ui.theme.Black
import com.example.market.ui.theme.DarkOrange
import com.example.market.ui.theme.MARketTheme
import com.example.market.ui.theme.Orange
import com.example.market.ui.theme.Teal
import kotlinx.coroutines.Dispatchers

@Composable
fun mARketApp() {
    MARketTheme {
        Surface(color = Beige) {
            val appState = rememberAppState()
            val listingsSharedViewModel: ListingSharedViewModel = viewModel()

            Scaffold(bottomBar = {
                BottomNavigationBar(appState.navController)
            }) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    notesGraph(appState, listingsSharedViewModel)
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

fun NavGraphBuilder.notesGraph(
    appState: MARketAppState,
    listingSharedViewModel: ListingSharedViewModel
) {

    composable(SIGN_IN_SCREEN) {
        SignInScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            navigate = { route -> appState.navigate(route) }
            )
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
    composable(HOME_SCREEN) {
        HomeScreen(
            navigate = { route -> appState.navigate(route) },
            listingSharedViewModel = listingSharedViewModel
        )
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(CATEGORIES_SCREEN) {
        CategoriesScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            listingSharedViewModel = listingSharedViewModel
        )

    }

    composable(ACCOUNT_SCREEN) {
        AccountScreen(navigate = { route -> appState.clearAndNavigate(route) })
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
        ARScreen(listingSharedViewModel)
    }

    composable(MODEL_VIEWER_SCREEN) {
        ModelViewerScreen(listingSharedViewModel)
    }

    composable(LISTINGS_SCREEN) {
        ListingsScreen(
            navigate = { route -> appState.navigate(route) },
            listingSharedViewModel = listingSharedViewModel
        )
    }
    composable(LISTING_DETAILS_SCREEN) {
        ListingDetailsScreen(
            navigate = { route ->
                appState.navigate(route)
            },
            listingSharedViewModel = listingSharedViewModel
        )
    }

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", HOME_SCREEN, Icons.Filled.Home),
        BottomNavItem("Categories", CATEGORIES_SCREEN, Icons.Filled.Menu),
        BottomNavItem("Create", CREATE_LISTING_SCREEN, Icons.Filled.Add),
        BottomNavItem("Account", ACCOUNT_SCREEN, Icons.Filled.Person)
    )

    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    val excludedRoutes = setOf(SPLASH_SCREEN, SIGN_IN_SCREEN, SIGN_UP_SCREEN)

    if (!excludedRoutes.contains(currentRoute)) {
        CustomNavigationBar(navController, items, currentRoute)
    }
}

@Composable
fun CustomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Beige,
        contentColor = Beige
    ) {
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
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Black,
                    unselectedIconColor = Black,
                    selectedTextColor = Black,
                    unselectedTextColor = Black,
                    indicatorColor = Teal
                )
            )
        }
    }
}