package com.kimnlee.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        BottomNavItem("home", R.string.home, R.drawable.house, R.drawable.housef),
        BottomNavItem("paymenthistory", R.string.vehicle_management , R.drawable.car, R.drawable.carf),
        BottomNavItem("cardmanagement", R.string.card_management , R.drawable.creditcard, R.drawable.creditcardf),
        BottomNavItem("settings", R.string.show_more , R.drawable.more, R.drawable.moref)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val pMediumFontFamily = FontFamily(Font(R.font.pmedium))

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(id = R.color.mobi_blue),
                contentColor = Color.White,
                modifier = Modifier
                    .height(60.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute?.startsWith(item.route) == true
                    val iconResId = if (isSelected) item.filledIconResId else item.iconResId

                    NavigationBarItem(
                        icon = {
                            Column(
//                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    painterResource(id = iconResId),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(22.dp)
//                                        .align(Alignment.Center)
                                )
                                Text(
                                    text = stringResource(id = item.titleResId),
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontFamily = pMediumFontFamily,
//                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White,
                            indicatorColor = Color.Transparent,
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