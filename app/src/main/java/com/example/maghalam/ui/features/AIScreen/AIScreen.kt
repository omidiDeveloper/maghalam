package com.example.maghalam.ui.features.AIScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.maghalam.R
import com.example.maghalam.ui.features.register.RegisterScreenView
import com.example.maghalam.ui.features.register.RegisterViewModel
import com.example.maghalam.utills.RtlLayout
import com.example.maghalam.utills.Screens
import org.koin.compose.viewmodel.koinViewModel



@Composable
fun AiScreen(
    viewModel: AiViewModel,
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
            Text(
                text = "تولید مقاله",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 64.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "اطلاعات مقاله خود را وارد کنید",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

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

        if (uiState.isLoading || uiState.isDownloading || uiState.isPublishing) {
            LoadingOverlay(
                message = uiState.loadingMessage ?: when {
                    uiState.isDownloading -> "در حال دانلود..."
                    uiState.isPublishing -> "در حال انتشار..."
                    else -> "در حال پردازش..."
                }
            )
        }

        if (uiState.showActionDialog) {
            ActionSelectionDialog(
                onDismiss = { viewModel.dismissActionDialog() },
                onDownloadOnly = { viewModel.downloadOnly() },
                onPublish = { viewModel.downloadAndPublish() }
            )
        }

        uiState.errorMessage?.let { message ->
            MessageDialog(
                title = "خطا",
                message = message,
                onDismiss = viewModel::clearError
            )
        }

        uiState.successMessage?.let { message ->
            MessageDialog(
                title = "موفقیت",
                message = message,
                onDismiss = viewModel::clearSuccess
            )
        }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.End
        ) {

                ArticleTextField(
                    uiState.title,
                    onTitleChange,
                    "عنوان مقاله",
                    "عنوان مقاله را وارد کنید",
                    uiState.titleError,
                    true
                )
                Spacer(modifier = Modifier.height(16.dp))
                ArticleTextField(
                    uiState.author,
                    onAuthorChange,
                    "نام نویسنده",
                    "نام نویسنده را وارد کنید",
                    uiState.authorError,
                    true
                )
                Spacer(modifier = Modifier.height(16.dp))
                ArticleTextField(
                    uiState.keywords,
                    onKeywordsChange,
                    "کلمات کلیدی",
                    "کلمات کلیدی را با کاما جدا کنید",
                    uiState.keywordsError,
                    false,
                    2
                )
                Spacer(modifier = Modifier.height(16.dp))
                LanguageDropdown(
                    uiState.selectedLanguage,
                    uiState.isLanguageDropdownExpanded,
                    onLanguageDropdownToggle,
                    onLanguageSelect
                )
                Spacer(modifier = Modifier.height(16.dp))
                ArticleTextField(
                    uiState.description,
                    onDescriptionChange,
                    "توضیحات مقاله",
                    "توضیحات کامل مقاله را وارد کنید",
                    uiState.descriptionError,
                    false,
                    5
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCreateArticle,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(text = "ایجاد مقاله", style = MaterialTheme.typography.titleMedium)
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
    RtlLayout {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
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
                        textAlign = TextAlign.Start,
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
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right)
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

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = "زبان مقاله",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.dorp_down_icon),
                        contentDescription = null
                    )
                    Text(text = selectedLanguage, style = MaterialTheme.typography.bodyMedium)
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
                                textAlign = TextAlign.Start,
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
    onPublish: () -> Unit
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
                text = "می‌خواهید مقاله را منتشر کنید یا فقط فایل آن را دانلود کنید؟",
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
                Button(onClick = onPublish, modifier = Modifier.fillMaxWidth()) {
                    Text("انتشار در لیست مقاله‌ها")
                }
                OutlinedButton(onClick = onDownloadOnly, modifier = Modifier.fillMaxWidth()) {
                    Text("فقط دانلود")
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("انصراف")
                }
            }
        }
    )
}

@Composable
private fun MessageDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = { Text(title, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
        text = { Text(message, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("باشه")
            }
        }
    )
}

@Composable
private fun LoadingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
