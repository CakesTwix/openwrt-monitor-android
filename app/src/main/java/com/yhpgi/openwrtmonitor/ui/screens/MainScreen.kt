package com.yhpgi.openwrtmonitor.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yhpgi.openwrtmonitor.R
import com.yhpgi.openwrtmonitor.domain.helper.repository.MainRepository
import com.yhpgi.openwrtmonitor.ui.activity.MainActivity
import com.yhpgi.openwrtmonitor.ui.navigation.BottomBarItem
import com.yhpgi.openwrtmonitor.ui.navigation.Screens
import com.yhpgi.openwrtmonitor.ui.viewModel.MainViewModel

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    mainActivity: MainActivity
) {
    val navController = rememberNavController()
    val listOfBottomBarItem = listOf(
        BottomBarItem(
            label = stringResource(id = R.string.navbar_home),
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            route = Screens.MainScreen.name
        ),
        BottomBarItem(
            label = stringResource(id = R.string.navbar_luci),
            unselectedIcon = Icons.Outlined.List,
            selectedIcon = Icons.Filled.List,
            route = Screens.LuciScreen.name
        ),
        BottomBarItem(
            label = stringResource(id = R.string.navbar_clash),
            unselectedIcon = Icons.Outlined.ShoppingCart,
            selectedIcon = Icons.Filled.ShoppingCart,
            route = Screens.OpenClashScreen.name
        ),
        BottomBarItem(
            label = stringResource(id = R.string.navbar_settings),
            unselectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings,
            route = Screens.SettingsScreen.name
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                listOfBottomBarItem.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination?.hierarchy?.any { it.route == navItem.route } == true) navItem.selectedIcon else navItem.unselectedIcon,
                                contentDescription = navItem.label)
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        },
    ) { paddingValues ->
        var ipAddress by rememberSaveable {
            mutableStateOf(MainRepository.DEFAULT_IP)
        }
        mainViewModel.savedIpString.observe(mainActivity) {
            ipAddress = it
        }
        var authToken by rememberSaveable {
            mutableStateOf(MainRepository.DEFAULT_TOKEN)
        }
        mainViewModel.savedTokenString.observe(mainActivity) {
            authToken = it
        }
        var luciPath by rememberSaveable {
            mutableStateOf(MainRepository.DEFAULT_LUCI_PATH)
        }
        mainViewModel.savedLuciPathString.observe(mainActivity) {
            luciPath = it
        }
        var openClashPath by rememberSaveable {
            mutableStateOf(MainRepository.DEFAULT_CLASH_PATH)
        }
        mainViewModel.savedClashString.observe(mainActivity) {
            openClashPath = it
        }

        val luciConfigChanged = mainViewModel.luciConfigChanged
        val clashConfigChanged = mainViewModel.clashConfigChanged

        NavHost(
            navController = navController,
            startDestination = Screens.MainScreen.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = Screens.MainScreen.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                HomeScreen(
                    mainViewModel.hostname,
                    mainViewModel.model,
                    mainViewModel.firmwareVersion,
                    mainViewModel.kernelVersion,
                    mainViewModel::getSystemInformation
                )
            }
            composable(
                route = Screens.LuciScreen.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                LuciScreen(
                    luciUrl = "http://$ipAddress/$luciPath",
                    internetConfigurationIsChanged = luciConfigChanged,
                    onBackPressed = { navController.popBackStack() },
                    onPageLoadedAfterValueChanged = mainViewModel::setLuciFalse
                )
            }
            composable(
                route = Screens.OpenClashScreen.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                OpenClashScreen(
                    openClashUrl = "http://$ipAddress$openClashPath",
                    internetConfigurationIsChanged = clashConfigChanged,
                    onBackPressed = { navController.popBackStack() },
                    onPageLoadedAfterValueChanged = mainViewModel::setClashFalse
                )
            }
            composable(
                route = Screens.SettingsScreen.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                SettingsScreen(mainViewModel, mainActivity)

            }
        }
    }
}
