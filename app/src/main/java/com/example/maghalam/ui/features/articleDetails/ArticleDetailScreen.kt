package com.example.maghalam.ui.features.articleDetails


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.maghalam.R


@Composable
fun ArticleDetailScreen(
    navController: NavController,
    articleId: String,
    viewModel: ArticleDetailViewModel,
    onScrollOffsetChanged: (Float) -> Unit = {}
) {
    val article by viewModel.getArticleById(articleId).collectAsState(initial = null)
    val listState = rememberLazyListState()

    // ردیابی تغییرات اسکرول
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val offset = listState.firstVisibleItemIndex * 1000f + listState.firstVisibleItemScrollOffset
        onScrollOffsetChanged(offset)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (article == null) {
            // حالت بارگذاری
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val currentArticle = article ?: return@Box
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                item {
                    // دکمه بازگشت به مقالات
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.popBackStack() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بازگشت به مقالات",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.dorp_down_icon),
                            contentDescription = "بازگشت",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // کارت اصلی مقاله
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                            // زبان
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = currentArticle.language,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // عنوان مقاله
                            Text(
                                text = currentArticle.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // نویسنده
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "نویسنده",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentArticle.author,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            Spacer(modifier = Modifier.height(24.dp))

                            // کلمات کلیدی
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "کلمات کلیدی",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    currentArticle.getKeywordsList().asReversed().forEach { keyword ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text(
                                                text = keyword,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            Spacer(modifier = Modifier.height(24.dp))

                            // تعداد کلمات
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "تعداد کلمات",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${currentArticle.wordCount} کلمه",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            Spacer(modifier = Modifier.height(24.dp))

                            // چکیده
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "چکیده",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = currentArticle.abstract,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                )
                            }

                            // بدنه
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "بدنه",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = currentArticle.content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // دکمه دانلود
                            Button(
                                onClick = { viewModel.downloadOnly() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.dorp_down_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "دانلود مقاله (PDF)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                // فاصله پایین برای Bottom Navigation
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
