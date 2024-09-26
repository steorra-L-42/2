package com.kimnlee.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
        BottomNavItem("home", R.drawable.house, R.drawable.housef),
        BottomNavItem("paymenthistory", R.drawable.car, R.drawable.carf),
        BottomNavItem("cardmanagement", R.drawable.creditcard, R.drawable.creditcardf),
        BottomNavItem("settings", R.drawable.person, R.drawable.personf)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(id = R.color.mobi_blue),
                contentColor = Color.White,
                modifier = Modifier.height(70.dp)
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute?.startsWith(item.route) == true
                    val iconResId = if (isSelected) item.filledIconResId else item.iconResId

                    NavigationBarItem(
                        icon = { Icon(painterResource(id = iconResId), contentDescription = null, tint = Color.White) },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {  // Only navigate if it's not the current route
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White, // Keeps icon color same when selected
                            unselectedIconColor = Color.White, // Keeps icon color same when unselected
                            indicatorColor = Color.Transparent // Removes the background indicator
                        )
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