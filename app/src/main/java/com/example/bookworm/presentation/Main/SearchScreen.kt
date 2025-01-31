package com.example.bookworm.presentation.Main

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookworm.R
import com.example.bookworm.domain.models.BooksResponseItem
import com.example.bookworm.utils.NetworkResponse


@Composable
fun SearchScreen(
    bookViewModel: BookViewModel = hiltViewModel()
) {
    val primaryColor = colorResource(R.color.primary_color)
    val primaryVariantColor = colorResource(R.color.primary_variant_color)

    var query by remember { mutableStateOf("") }
    val searchResults by bookViewModel.searchBooksResults.collectAsState()

    // Custom colors for consistent design
    val backgroundColor = primaryColor
    val textColor = Color.White
    val cardBackgroundColor = primaryVariantColor
    val cardTextColor = primaryColor

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    bookViewModel.searchBooks(query)
                },
                label = { Text("Search Books", color = textColor) },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(top = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = textColor.copy(alpha = 0.7f),
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor.copy(alpha = 0.7f),
                    cursorColor = textColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )
            if (query.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your results will show here",
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                when (searchResults) {
                    is NetworkResponse.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = textColor)
                        }
                    }
                    is NetworkResponse.Success -> {
                        val books: List<BooksResponseItem>? = (searchResults as NetworkResponse.Success).data?.items
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(books ?: emptyList()) { book ->
                                BookItem(
                                    bookViewModel = bookViewModel,
                                    book = book,
                                    cardBackgroundColor = cardBackgroundColor,
                                    cardTextColor = cardTextColor,
                                    onActionButtonClicked = { query= ""}
                                )
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (searchResults as NetworkResponse.Error).message,
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(
    bookViewModel: BookViewModel,
    book: BooksResponseItem,
    cardBackgroundColor: Color,
    cardTextColor: Color,
    onActionButtonClicked : () -> Unit
) {
    val userData by bookViewModel.userdata.collectAsState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = book.volumeInfo.imageLinks?.thumbnail,
                    contentDescription = "Book cover",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Top)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = book.volumeInfo.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = cardTextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.volumeInfo.authors?.joinToString(", ") ?: "Unknown author",
                        style = MaterialTheme.typography.bodyMedium,
                        color = cardTextColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BookActionButton(
                    text = "Read",
                    backgroundColor = cardTextColor,
                    contentColor = cardBackgroundColor,
                    modifier = Modifier.weight(1f)
                ) {
                    userData?.let { user ->
                        bookViewModel.saveBookToCategory(user.id, book, "read")
                    }
                    onActionButtonClicked()
                }

                BookActionButton(
                    text = "Reading",
                    backgroundColor = cardTextColor,
                    contentColor = cardBackgroundColor,
                    modifier = Modifier.weight(1f)
                ) {
                    userData?.let { user ->
                        bookViewModel.saveBookToCategory(user.id, book, "currentlyReading")
                    }
                    onActionButtonClicked()
                }

                BookActionButton(
                    text = "Wishlist",
                    backgroundColor = cardTextColor,
                    contentColor = cardBackgroundColor,
                    modifier = Modifier.weight(1f)
                ) {
                    userData?.let { user ->
                        bookViewModel.saveBookToCategory(user.id, book, "wantToRead")
                    }
                    onActionButtonClicked()
                }
            }
        }
    }
}

@Composable
private fun BookActionButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val primaryColor = colorResource(R.color.primary_color)
    val primaryVariantColor = colorResource(R.color.primary_variant_color)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = primaryColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = "Harry Potter",  // Sample search text
                onValueChange = { },
                label = { Text("Search Books") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    cursorColor = primaryColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleBooks) { book ->
                    BookItemPreview(book, primaryColor, primaryVariantColor)
                }
            }
        }
    }
}

@Composable
private fun BookItemPreview(
    book: PreviewBook,
    primaryColor: androidx.compose.ui.graphics.Color,
    primaryVariantColor: androidx.compose.ui.graphics.Color
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Using a placeholder box instead of AsyncImage for preview
                Surface(
                    modifier = Modifier
                        .size(100.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) { }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = primaryVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BookActionButtonPreview(
                    text = "Read",
                    color = primaryColor,
                    modifier = Modifier.weight(1f)
                )

                BookActionButtonPreview(
                    text = "Reading",
                    color = primaryColor,
                    modifier = Modifier.weight(1f)
                )

                BookActionButtonPreview(
                    text = "Wishlist",
                    color = primaryColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BookActionButtonPreview(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = { },
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = color,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Sample data for preview
private data class PreviewBook(
    val title: String,
    val author: String
)

private val sampleBooks = listOf(
    PreviewBook(
        "The Lord of the Rings",
        "J.R.R. Tolkien"
    ),
    PreviewBook(
        "Harry Potter and the Philosopher's Stone",
        "J.K. Rowling"
    ),
    PreviewBook(
        "The Hunger Games",
        "Suzanne Collins"
    ),
    PreviewBook(
        "1984",
        "George Orwell"
    )
)