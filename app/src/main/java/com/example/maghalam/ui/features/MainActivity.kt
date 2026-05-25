package com.example.maghalam.ui.features

import android.R.attr.type
import com.example.maghalam.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maghalam.di.myModule
import com.example.maghalam.ui.features.AIScreen.AiScreen
import com.example.maghalam.ui.features.login.LoginScreen
import com.example.maghalam.ui.features.register.RegisterScreen
import com.example.maghalam.ui.theme.MaghalamTheme
import com.example.maghalam.utills.Screens
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.navigation.KoinNavHost
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.maghalam.ui.features.Items.ItemsScreen
import com.example.maghalam.ui.features.articleDetails.ArticleDetailScreen
import com.example.maghalam.ui.features.profile.ProfileScreen
import com.example.maghalam.utills.RtlLayout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Koin(appDeclaration = { modules(myModule) }) {
                MaghalamTheme {
                        MaghalamScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview(function: @Composable () -> Unit) {
    MaghalamTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            RegisterScreen(navController = rememberNavController())
        }
    }
}

@Composable
fun MaghalamScreen() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(1) }

    var isBottomNavVisible by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableStateOf(0f) }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(currentRoute) {
        when {
            currentRoute == Screens.ItemsScreen.rute -> selectedItem = 0
            currentRoute == Screens.AiScreen.rute -> selectedItem = 1
            currentRoute == Screens.ProfileScreen.rute -> selectedItem = 2
            currentRoute?.startsWith("articleDetailScreen") == true -> {
                isBottomNavVisible = false
            }
        }
        if (currentRoute != null && !currentRoute.startsWith("articleDetailScreen")) {
            isBottomNavVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KoinNavHost(
            navController = navController,
            startDestination = Screens.AiScreen.rute
        ) {
            composable(Screens.RegisterScreen.rute) { RegisterScreen(navController) }
            composable(Screens.LoginScreen.rute) { LoginScreen(navController) }
            composable(Screens.AiScreen.rute) {
                AiScreen(
                    onScrollOffsetChanged = { offset ->
                        val delta = offset - lastScrollOffset
                        lastScrollOffset = offset

                        if (delta > 10f) {
                            isBottomNavVisible = false
                        } else if (delta < -10f) {
                            isBottomNavVisible = true
                        }
                    }
                )
            }
            composable(Screens.ProfileScreen.rute) {
                ProfileScreen(
                    navController = navController,
                    onScrollOffsetChanged = { offset ->
                        val delta = offset - lastScrollOffset
                        lastScrollOffset = offset

                        if (delta > 10f) {
                            isBottomNavVisible = false
                        } else if (delta < -10f) {
                            isBottomNavVisible = true
                        }
                    }
                )
            }
            composable(Screens.ItemsScreen.rute) {
                ItemsScreen(
                    navController = navController,
                    onScrollOffsetChanged = { offset ->
                        val delta = offset - lastScrollOffset
                        lastScrollOffset = offset

                        if (delta > 10f) {
                            isBottomNavVisible = false
                        } else if (delta < -10f) {
                            isBottomNavVisible = true
                        }
                    }
                )
            }
            composable(
                route = "articleDetailScreen/{article_id}",
                arguments = listOf(navArgument("article_id") {
                    type = NavType.StringType
                })
            ) {
                ArticleDetailScreen(
                    articleId = it.arguments?.getString("article_id") ?: "null",
                    navController = navController
                )
            }
        }

        AnimatedVisibility(
            visible = isBottomNavVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            GlassBottomNavigation(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    selectedItem = index
                    lastScrollOffset = 0f
                    isBottomNavVisible = true
                    when (index) {
                        0 -> navController.navigate(Screens.ItemsScreen.rute) {
                            popUpTo(Screens.AiScreen.rute) { inclusive = false }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate(Screens.AiScreen.rute) {
                            popUpTo(Screens.AiScreen.rute) { inclusive = false }
                            launchSingleTop = true
                        }
                        2 -> navController.navigate(Screens.ProfileScreen.rute) {
                            popUpTo(Screens.AiScreen.rute) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}



@Composable
fun GlassBottomNavigation(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("مقالات", R.drawable.items_icon),
        BottomNavItem("ساختن", R.drawable.write_icon),
        BottomNavItem("پروفایل", R.drawable.outline_person_2_24)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // افکت شیشه‌ای
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                )
                .blur(.3.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    GlassNavItem(
                        icon = item.icon,
                        label = item.label,
                        selected = selectedItem == index,
                        onClick = { onItemSelected(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassNavItem(
    icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary.copy(
                    alpha = 0.6f
                ),
                modifier = Modifier.size(32.dp)
            )
//

            if (selected) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Int
)

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // محتوای اصلی صفحه
        when (selectedItem) {
            0 -> ItemsScreen(rememberNavController())
            1 -> AiScreen(viewModel()){}
            2 -> ProfileScreen(rememberNavController())
        }

        // Bottom Navigation شناور
        GlassBottomNavigation(
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
