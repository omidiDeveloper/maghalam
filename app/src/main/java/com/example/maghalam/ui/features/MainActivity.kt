package com.example.maghalam.ui.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.maghalam.R
import com.example.maghalam.ui.features.AIScreen.AiScreen
import com.example.maghalam.ui.features.Items.ItemsScreen
import com.example.maghalam.ui.features.admin.AdminScreen
import com.example.maghalam.ui.features.articleDetails.ArticleDetailScreen
import com.example.maghalam.ui.features.auth.ForgotPasswordScreen
import com.example.maghalam.ui.features.intro.IntroScreen
import com.example.maghalam.ui.features.login.LoginScreen
import com.example.maghalam.ui.features.profile.ProfileScreen
import com.example.maghalam.ui.features.register.RegisterScreen
import com.example.maghalam.ui.features.startup.StartupViewModel
import com.example.maghalam.ui.theme.MaghalamTheme
import com.example.maghalam.utills.Screens
import dev.burnoo.cokoin.navigation.KoinNavHost
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaghalamTheme {
                    MaghalamScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview(function: @Composable () -> Unit = {}) {
    MaghalamTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            function()
        }
    }
}

@Composable
fun MaghalamScreen(
    startupViewModel: StartupViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val isAdmin by startupViewModel.isAdmin.collectAsState()
    var selectedItem by remember { mutableIntStateOf(1) }
    var isBottomNavVisible by remember { mutableStateOf(false) }
    var lastScrollOffset by remember { mutableFloatStateOf(0f) }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val mainRoutes = listOf(
        Screens.ItemsScreen.rute,
        Screens.AiScreen.rute,
        Screens.ProfileScreen.rute,
        Screens.AdminScreen.rute
    )

    LaunchedEffect(currentRoute, isAdmin) {
        isBottomNavVisible = currentRoute in mainRoutes
        selectedItem = when (currentRoute) {
            Screens.ItemsScreen.rute -> 0
            Screens.AiScreen.rute -> 1
            Screens.ProfileScreen.rute -> 2
            Screens.AdminScreen.rute -> 3
            else -> selectedItem
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KoinNavHost(
            navController = navController,
            startDestination = Screens.SplashScreen.rute
        ) {
            composable(Screens.SplashScreen.rute) {
                SplashScreen {
                    navController.navigate(startupViewModel.firstDestinationAfterSplash()) {
                        popUpTo(Screens.SplashScreen.rute) { inclusive = true }
                    }
                }
            }
            composable(Screens.IntroScreen.rute) {
                IntroScreen(
                    onStartClick = {
                        navController.navigate(startupViewModel.completeIntro()) {
                            popUpTo(Screens.IntroScreen.rute) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screens.RegisterScreen.rute) { RegisterScreen(navController) }
            composable(Screens.LoginScreen.rute) {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        startupViewModel.refreshRole()
                        navController.navigate(Screens.AiScreen.rute) {
                            popUpTo(Screens.LoginScreen.rute) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screens.ForgotPasswordScreen.rute) { ForgotPasswordScreen(navController) }
            composable(Screens.AiScreen.rute) {
                AiScreen(
                    viewModel = koinViewModel(),
                    onScrollOffsetChanged = { offset ->
                        val delta = offset - lastScrollOffset
                        lastScrollOffset = offset
                        isBottomNavVisible = delta <= 10f
                    }
                )
            }
            composable(Screens.ProfileScreen.rute) {
                ProfileScreen(
                    navController = navController,
                    onScrollOffsetChanged = { offset ->
                        val delta = offset - lastScrollOffset
                        lastScrollOffset = offset
                        isBottomNavVisible = delta <= 10f
                    }
                )
            }
            composable(Screens.ItemsScreen.rute) {
                ItemsScreen(
                    navController = navController,
                    onScrollOffsetChanged = { offset ->
                        val delta = offset - lastScrollOffset
                        lastScrollOffset = offset
                        isBottomNavVisible = delta <= 10f
                    }
                )
            }
            composable(Screens.AdminScreen.rute) { AdminScreen() }
            composable(
                route = "${Screens.ArticleDetailScreen.rute}/{article_id}",
                arguments = listOf(navArgument("article_id") { type = NavType.StringType })
            ) {
                ArticleDetailScreen(
                    articleId = it.arguments?.getString("article_id").orEmpty(),
                    navController = navController,
                    viewModel = koinViewModel(),
                )
            }
        }

        AnimatedVisibility(
            visible = isBottomNavVisible,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            GlassBottomNavigation(
                selectedItem = selectedItem,
                isAdmin = isAdmin,
                onItemSelected = { index ->
                    selectedItem = index
                    lastScrollOffset = 0f
                    isBottomNavVisible = true
                    val route = when (index) {
                        0 -> Screens.ItemsScreen.rute
                        1 -> Screens.AiScreen.rute
                        2 -> Screens.ProfileScreen.rute
                        else -> Screens.AdminScreen.rute
                    }
                    navController.navigate(route) {
                        popUpTo(Screens.AiScreen.rute) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(250))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.write_icon),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("مقالم", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ساخت و مدیریت مقاله با تجربه‌ای روان", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun GlassBottomNavigation(
    selectedItem: Int,
    isAdmin: Boolean,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = buildList {
        add(BottomNavItem("مقاله‌ها", R.drawable.items_icon))
        add(BottomNavItem("ساختن", R.drawable.write_icon))
        add(BottomNavItem("پروفایل", R.drawable.outline_person_2_24))
        if (isAdmin) add(BottomNavItem("مدیریت", R.drawable.list_icon))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                .blur(.3.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(30.dp))
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
    IconButton(onClick = onClick, modifier = modifier.size(58.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f),
                modifier = Modifier.size(28.dp)
            )
            if (selected) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Int
)
