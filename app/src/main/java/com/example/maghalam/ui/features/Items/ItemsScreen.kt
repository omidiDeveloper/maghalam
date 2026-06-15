package com.example.maghalam.ui.features.Items

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.maghalam.model.data.Article
import com.example.maghalam.R
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ItemsScreen(
    navController: NavController,
    onScrollOffsetChanged: (Float) -> Unit = {}
) {
//------------------------------------------------------------------------

    val viewModel: ItemsViewModel = koinViewModel()
    val listState = rememberLazyListState()
    val article by viewModel.article.collectAsState()

//------------------------------------------------------------------------
    // ردیابی تغییرات اسکرول
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val offset =
            listState.firstVisibleItemIndex * 1000f + listState.firstVisibleItemScrollOffset
        onScrollOffsetChanged(offset)
    }

//------------------------------------------------------------------------

    LaunchedEffect(Unit) {
        viewModel.navigateToArticle.collect { id ->
            navController.navigate(
                "articleDetailScreen/$id"
            )
        }
    }


//------------------------------------------------------------------------

    // محاسبه میزان اسکرول برای محو شدن عنوان
    val titleAlpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                1f - (listState.firstVisibleItemScrollOffset / 200f).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

//------------------------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
//------------------------------------------------------------------------
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // عنوان صفحه با قابلیت محو شدن
//------------------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
//------------------------------------------------------------------------
                Text(
                    text = "لیست مقالات",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
            }
//------------------------------------------------------------------------

            Spacer(modifier = Modifier.height(24.dp))
//------------------------------------------------------------------------
            // لیست مقالات
            if (article.isEmpty()) {
                EmptyState()
            } else {

//------------------------------------------------------------------------

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {

//------------------------------------------------------------------------

                    items(
                        items = article,
                        key = { it.id ?: it.hashCode().toLong() }
                    ) { article ->
                        val articleId = article.id

//------------------------------------------------------------------------

                        ArticleCard(
                            article = article,
                            onDelete = { articleId?.let(viewModel::deleteArticle) },
                            onClick = {
                                articleId?.let(viewModel::onArticleClicked)
                            }
                        )
//------------------------------------------------------------------------

                        Spacer(modifier = Modifier.height(22.dp))
                    }
//------------------------------------------------------------------------

                    // فاصله پایین برای Bottom Navigation
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
//------------------------------------------------------------------------
                }
            }
        }
    }
}

//------------------------------------------------------------------------

@Composable
private fun ArticleCard(
    article: Article,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ردیف بالا: زبان و دکمه حذف
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // دکمه حذف
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.dorp_down_icon),
                        contentDescription = "حذف مقاله",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // نمایش زبان
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = article.language,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // عنوان مقاله
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // نویسنده
            Text(
                text = "نویسنده: ${article.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(12.dp))

            // چکیده
            Text(
                text = article.abstract,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )

            Spacer(modifier = Modifier.height(16.dp))

            // کلمات کلیدی (فقط ۳ تای اول)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                article.getKeywordsList().take(3).forEach { keyword ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = keyword,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

//------------------------------------------------------------------------

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.list_icon),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "هنوز مقاله‌ای منتشر نشده است",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "مقالات منتشر شده در اینجا نمایش داده می‌شوند",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
