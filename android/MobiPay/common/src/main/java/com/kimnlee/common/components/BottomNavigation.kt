package com.kimnlee.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kimnlee.common.R

@Composable
fun BottomNavigation(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem("home", R.string.home, R.drawable.home_24px),
        BottomNavItem("payment", R.string.payment, R.drawable.receipt_long_24px),
        BottomNavItem("cardmanagement", R.string.card_management, R.drawable.credit_card_24px),
        BottomNavItem("settings", R.string.settings, R.drawable.settings_24px)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute?.startsWith(item.route) == true

                    NavigationBarItem(
                        icon = { Icon(painterResource(id = item.iconResId), contentDescription = null) },
                        label = { Text(stringResource(item.titleResId)) },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {  // Only navigate if it's not the current route
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}