package com.example.maghalam.ui.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp)
    ) {
        Text(
            text = "پنل مدیریت",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("مقاله‌ها") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("کاربران") })
        }

        if (state.isLoading && state.articles.isEmpty() && state.users.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("در حال بارگذاری اطلاعات مدیریت")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (selectedTab == 0) {
                    item {
                        AdminHint("برای افزودن مقاله جدید از تب ساختن استفاده کنید. مقاله‌های ساخته‌شده اینجا قابل مدیریت هستند.")
                    }
                    items(state.articles, key = { it.id ?: it.hashCode().toLong() }) { article ->
                        AdminItemCard(
                            title = article.title,
                            subtitle = "نویسنده: ${article.author}",
                            meta = "${article.wordCount} کلمه",
                            deleteText = "حذف مقاله",
                            onDelete = { article.id?.let(viewModel::deleteArticle) }
                        )
                    }
                } else {
                    items(state.users, key = { it.id ?: it.hashCode().toLong() }) { user ->
                        AdminItemCard(
                            title = user.fullName.ifBlank { user.username },
                            subtitle = user.email,
                            meta = "نقش: ${user.role}",
                            deleteText = "حذف کاربر",
                            onDelete = { user.id?.let(viewModel::deleteUser) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(96.dp)) }
            }
        }
    }

    state.message?.let {
        AdminMessageDialog(title = "انجام شد", body = it, onDismiss = viewModel::clearMessages)
    }
    state.error?.let {
        AdminMessageDialog(title = "خطا", body = it, onDismiss = viewModel::clearMessages)
    }
}

@Composable
private fun AdminHint(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Right
        )
    }
}

@Composable
private fun AdminItemCard(
    title: String,
    subtitle: String,
    meta: String,
    deleteText: String,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { confirmDelete = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(deleteText)
                }
                Text(meta, style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("تأیید حذف", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
            text = { Text("این عملیات قابل بازگشت نیست.", textAlign = TextAlign.Right) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun AdminMessageDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
        text = { Text(body, textAlign = TextAlign.Right) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("باشه")
            }
        }
    )
}
