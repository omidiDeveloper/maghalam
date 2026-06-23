package com.example.maghalam.ui.features.articleDetails

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.maghalam.R
import com.example.maghalam.model.data.Article

@Composable
fun ArticleDetailScreen(
    navController: NavController,
    articleId: String,
    viewModel: ArticleDetailViewModel,
    onScrollOffsetChanged: (Float) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var lastScrollOffset by remember { mutableFloatStateOf(0f) }
    var isDownloadButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(articleId) {
        viewModel.loadArticle(articleId)
    }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val offset =
            listState.firstVisibleItemIndex * 1000f + listState.firstVisibleItemScrollOffset
        val delta = offset - lastScrollOffset
        lastScrollOffset = offset
        isDownloadButtonVisible = when {
            offset < 24f -> false
            delta > 10f -> false
            delta < -10f -> true
            else -> isDownloadButtonVisible
        }
        onScrollOffsetChanged(offset)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading && state.article == null -> ArticleDetailSkeleton()
            state.article != null -> ArticleDetailContent(
                article = state.article,
                listState = listState,
                onBack = { navController.popBackStack() }
            )

            else -> ErrorContent(
                message = state.error ?: "مقاله پیدا نشد",
                onBack = { navController.popBackStack() })
        }

        AnimatedVisibility(
            visible = state.article != null && isDownloadButtonVisible,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(220)
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(180)
            ) + fadeOut(animationSpec = tween(180)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            DownloadArticleButton(
                isDownloading = state.isDownloading,
                onDownload = viewModel::downloadPdf
            )
        }
    }

    state.downloadPath?.let { path ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessages,
            title = {
                Text(
                    "دانلود کامل شد",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            },
            text = { Text("فایل در حافظه دستگاه ذخیره شد:\n$path", textAlign = TextAlign.Right) },
            confirmButton = {
                TextButton(onClick = viewModel::clearMessages) {
                    Text("باشه")
                }
            }
        )
    }

    if (state.isDownloading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    "در حال دانلود",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("لطفا چند لحظه صبر کنید", textAlign = TextAlign.Right)
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                }
            },
            confirmButton = {}
        )
    }

    state.error?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessages,
            title = {
                Text(
                    "خطا",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            },
            text = { Text(message, textAlign = TextAlign.Right) },
            confirmButton = {
                TextButton(onClick = viewModel::clearMessages) {
                    Text("باشه")
                }
            }
        )
    }
}

@Composable
private fun ArticleDetailContent(
    article: Article?,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onBack: () -> Unit
) {
    val currentArticle = article ?: return

    //the list of article as : title , keywords , abstract , content ...etc.
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_back_icon),
                        contentDescription = "بازگشت"
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Surface (
                        modifier = Modifier.align(  Alignment.End )
                    ) {
                        Text(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            text = currentArticle.language,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Left
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentArticle.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "نویسنده: ${currentArticle.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "کلمات کلیدی",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        currentArticle.getKeywordsList().take(4).forEach {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Divider()
                    ArticleMetaSection(currentArticle)
                    Divider()
                    ArticleSection(
                        "چکیده",
                        currentArticle.abstract.ifBlank { currentArticle.description })
                    ArticleSection(
                        "متن مقاله",
                        currentArticle.content.ifBlank { currentArticle.description })
                }
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}


@Composable
private fun DownloadArticleButton(
    isDownloading: Boolean,
    onDownload: () -> Unit
) {
    Button(
        onClick = onDownload,
        enabled = !isDownloading,
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        if (isDownloading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.download_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(if (isDownloading) "در حال دانلود" else "دانلود مقاله")
    }
}

@Composable
private fun ArticleMetaSection(article: Article) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "تعداد کلمات",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${article.wordCount} کلمه",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "تاریخ ساخت مقاله",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = article.getFormattedDate().ifBlank { "ثبت نشده" },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ArticleSection(title: String, body: String) {
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = body.ifBlank { "محتوایی برای نمایش وجود ندارد." },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ArticleDetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (it == 0) .55f else 1f)
                    .height(if (it == 2) 120.dp else 22.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .65f),
                        RoundedCornerShape(8.dp)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("بازگشت")
        }
    }
}
