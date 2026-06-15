package com.example.maghalam.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import com.example.maghalam.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.maghalam.utills.Screens
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel(),
    onScrollOffsetChanged: (Float) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val selectedFont by viewModel.selectedFont.collectAsState()
    val articlesCount by viewModel.articlesCount.collectAsState()

    // ردیابی تغییرات اسکرول
    LaunchedEffect(scrollState.value) {
        onScrollOffsetChanged(scrollState.value.toFloat())
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // بخش اطلاعات کاربر
        UserInfoSection(
            userProfile = userProfile,
            articlesCount = articlesCount
        )

        Spacer(modifier = Modifier.height(24.dp))

        // بخش تنظیمات ظاهری
        AppearanceSettingsSection(
            isDarkMode = isDarkMode,
            selectedFont = selectedFont,
            onDarkModeToggle = { viewModel.toggleDarkMode() },
            onFontClick = { showFontDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // دکمه خروج از حساب
        LogoutButton(
            onClick = { showLogoutDialog = true }
        )

        // فاصله پایین برای Bottom Navigation
        Spacer(modifier = Modifier.height(100.dp))
    }

    // دیالوگ تایید خروج
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                viewModel.logout()
                showLogoutDialog = false
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // دیالوگ انتخاب فونت
    if (showFontDialog) {
        FontSelectionDialog(
            selectedFont = selectedFont,
            onFontSelected = { font ->
                viewModel.setFont(font)
                showFontDialog = false
            },
            onDismiss = { showFontDialog = false }
        )
    }
}

@Composable
private fun UserInfoSection(
    userProfile: UserProfile,
    articlesCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // عنوان بخش
            Text(
                text = "اطلاعات کاربری",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // آواتار کاربر
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_person_2_24),
                    contentDescription = "آواتار کاربر",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // نام و نام خانوادگی
            ProfileInfoRow(
                icon = ImageVector.vectorResource(R.drawable.outline_person_2_24),
                label = "نام و نام خانوادگی",
                value = userProfile.fullName
            )

            Spacer(modifier = Modifier.height(16.dp))

            // نام کاربری
            ProfileInfoRow(
                icon = ImageVector.vectorResource(R.drawable.account_circle_icon),
                label = "نام کاربری",
                value = userProfile.username
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ایمیل
            ProfileInfoRow(
                icon = ImageVector.vectorResource(R.drawable.outline_alternate_email_24),
                label = "ایمیل",
                value = userProfile.email
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // تعداد مقالات منتشر شده
            ProfileInfoRow(
                icon = ImageVector.vectorResource(R.drawable.write_icon),
                label = "مقالات منتشر شده",
                value = "$articlesCount مقاله"
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // مقدار
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )

        // برچسب و آیکون
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AppearanceSettingsSection(
    isDarkMode: Boolean,
    selectedFont: String,
    onDarkModeToggle: () -> Unit,
    onFontClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // عنوان بخش
            Text(
                text = "تنظیمات ظاهری",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(24.dp))

            // حالت شب
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onDarkModeToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "حالت شب",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isDarkMode) "تم تاریک / روشن" else "تم تاریک / روشن",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (isDarkMode) ImageVector.vectorResource(R.drawable.dark_mode_icon) else ImageVector.vectorResource(
                            R.drawable.outline_brightness_5_24
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(16.dp))

            // اندازه فونت
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onFontClick),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.dorp_down_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedFont,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "اندازه فونت",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "تغییر اندازه متن برنامه",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_serif_24),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_right_alt_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "خروج از حساب کاربری",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_arrow_right_alt_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "خروج از حساب کاربری",
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "آیا مطمئن هستید که می‌خواهید از حساب کاربری خود خارج شوید؟",
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("خروج")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
private fun FontSelectionDialog(
    selectedFont: String,
    onFontSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val fonts = listOf("Vazir", "B Nazanin", "Roboto")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_font_download_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "انتخاب فونت",
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                fonts.forEach { font ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFontSelected(font) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFont == font,
                            onClick = { onFontSelected(font) }
                        )
                        Text(
                            text = font,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("بستن")
            }
        }
    )
}
