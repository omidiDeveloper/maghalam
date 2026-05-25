package com.example.maghalam.ui.features.AIScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maghalam.R


@Composable
fun AiScreen(
    viewModel: AiViewModel = viewModel(),
    onScrollOffsetChanged: (Float) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        onScrollOffsetChanged(scrollState.value.toFloat())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp)
        ) {
            ArticleHeader()
            Spacer(modifier = Modifier.height(22.dp))
            ArticleFormCard(
                uiState = uiState,
                onLanguageDropdownToggle = viewModel::onLanguageDropdownToggle,
                onLanguageSelect = viewModel::onLanguageSelect,
                onTitleChange = viewModel::onTitleChange,
                onAuthorChange = viewModel::onAuthorChange,
                onKeywordsChange = viewModel::onKeywordsChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onCreateArticle = viewModel::createArticle
            )
            Spacer(modifier = Modifier.height(100.dp))
        }

        if (uiState.isLoading) {
            LoadingOverlay(message = uiState.loadingMessage ?: "در حال پردازش...")
        }

        if (uiState.showActionDialog) {
            ActionSelectionDialog(
                onDismiss = { viewModel.dismissActionDialog() },
                onDownloadOnly = { viewModel.downloadOnly() },
                onDownloadAndPublish = { viewModel.downloadAndPublish() }
            )
        }

        if (uiState.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("خطا") },
                text = { Text(uiState.errorMessage ?: "") },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("باشه")
                    }
                }
            )
        }

        if (uiState.successMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearSuccess() },
                title = { Text("موفقیت") },
                text = { Text(uiState.successMessage ?: "") },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearSuccess() }) {
                        Text("باشه")
                    }
                }
            )
        }
    }
}

@Composable
private fun ArticleHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "تولید مقاله",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 32.dp).fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "اطلاعات مقاله خود را وارد کنید",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun ArticleFormCard(
    uiState: AiUiState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onKeywordsChange: (String) -> Unit,
    onLanguageDropdownToggle: () -> Unit,
    onLanguageSelect: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCreateArticle: () -> Unit
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
                .padding(20.dp),
            horizontalAlignment = Alignment.End
        ) {
            ArticleTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = "عنوان مقاله",
                placeholder = "عنوان مقاله را وارد کنید",
                error = uiState.titleError,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ArticleTextField(
                value = uiState.author,
                onValueChange = onAuthorChange,
                label = "نام نویسنده",
                placeholder = "نام نویسنده را وارد کنید",
                error = uiState.authorError,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ArticleTextField(
                value = uiState.keywords,
                onValueChange = onKeywordsChange,
                label = "کلمات کلیدی",
                placeholder = "کلمات کلیدی را با کاما جدا کنید (حداقل ۳ کلمه)",
                error = uiState.keywordsError,
                singleLine = false,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            LanguageDropdown(
                selectedLanguage = uiState.selectedLanguage,
                isExpanded = uiState.isLanguageDropdownExpanded,
                onToggle = onLanguageDropdownToggle,
                onSelect = onLanguageSelect
            )

            Spacer(modifier = Modifier.height(16.dp))

            ArticleTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = "توضیحات مقاله",
                placeholder = "توضیحات کامل مقاله را وارد کنید",
                error = uiState.descriptionError,
                singleLine = false,
                minLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateArticle,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = "ایجاد مقاله",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun ArticleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    singleLine: Boolean,
    minLines: Int = 1
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            isError = error != null,
            singleLine = singleLine,
            minLines = minLines,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Right
            )
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Right
            )
        }
    }
}

@Composable
private fun LanguageDropdown(
    selectedLanguage: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit
) {
    val availableLanguages = listOf("فارسی", "English", "العربية")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "زبان مقاله",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.dorp_down_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = selectedLanguage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = onToggle,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                availableLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = { onSelect(language) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionSelectionDialog(
    onDismiss: () -> Unit,
    onDownloadOnly: () -> Unit,
    onDownloadAndPublish: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "مقاله با موفقیت ساخته شد",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "چه کاری می‌خواهید انجام دهید؟",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDownloadAndPublish,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text(
                        text = "دانلود و انتشار در نرم‌افزار",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                OutlinedButton(
                    onClick = onDownloadOnly,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text(
                        text = "فقط دانلود کن",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "انصراف",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun LoadingOverlay(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.tertiary,
                    strokeWidth = 4.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
