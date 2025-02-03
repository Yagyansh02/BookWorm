package com.example.bookworm.presentation.Main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.bookworm.R
import com.example.bookworm.domain.models.BooksResponse
import com.example.bookworm.domain.models.BooksResponseItem
import com.example.bookworm.domain.models.User
import com.example.bookworm.presentation.Authentication.AuthenticationViewModel
import com.example.bookworm.utils.NetworkResponse


@Composable
fun HomeScreen(
    viewModel: BookViewModel = hiltViewModel(),
    authViewModel: AuthenticationViewModel = hiltViewModel(),
    onSearchClickButton: () -> Unit,
    onSignoutClick: () -> Unit
) {
    val primaryColor = colorResource(R.color.primary_color)
    val primaryVariantColor = colorResource(R.color.primary_variant_color)

    val userDataState by viewModel.userdata.collectAsStateWithLifecycle()
    val categoryState by viewModel.categoryState.collectAsStateWithLifecycle()

    LaunchedEffect(userDataState?.id) {
        userDataState?.id?.let { userId ->
            viewModel.fetchAllBooksByCategories(userId)
        }
    }

    Log.d("HomeScreen", "userDataState: $userDataState")
    Log.d("HomeScreen", "categoryState: $categoryState")

    Scaffold(
        containerColor = primaryVariantColor.copy(alpha = 0.15f),
        topBar = {
            ModernTopAppBar(
                onSearchClick = onSearchClickButton,
                onNotificationClick = {authViewModel.signOut()
                    onSignoutClick()
                }
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                primaryColor = primaryColor,
                primaryVariantColor = primaryVariantColor
            )
        }
    ) { paddingValues ->
        when (userDataState) {
            null -> {
                LoadingScreen(paddingValues)
            }
            else -> {
                HomeContent(
                    paddingValues = paddingValues,
                    user = userDataState!!,
                    categoryState = categoryState,
                    primaryColor = primaryColor,
                    primaryVariantColor = primaryVariantColor
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeContent(
    paddingValues: PaddingValues,
    user: User,
    categoryState: Category,
    primaryColor: Color,
    primaryVariantColor: Color
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        FeaturedBanner(
            username = user.username,
            primaryColor = primaryColor,
            primaryVariantColor = primaryVariantColor
        )

        BookSectionWithState(
            title = "Currently Reading",
            booksState = categoryState.currentlyReading,
            primaryColor = primaryColor
        )

        BookSectionWithState(
            title = "Want to Read",
            booksState = categoryState.wantToRead,
            primaryColor = primaryColor
        )

        BookSectionWithState(
            title = "Read",
            booksState = categoryState.read,
            primaryColor = primaryColor
        )
    }
}

@Composable
private fun BookSectionWithState(
    title: String,
    booksState: NetworkResponse<BooksResponse>,
    primaryColor: Color
) {
    when (booksState) {
        is NetworkResponse.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = primaryColor
                )
            }
        }
        is NetworkResponse.Error -> {
            Text(
                text = booksState.message,
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )
        }
        is NetworkResponse.Success -> {
            BookSection(
                title = title,
                books = booksState.data,
                primaryColor = primaryColor
            )
        }
    }
}

@Composable
fun BookSection(
    title: String,
    books: BooksResponse,
    primaryColor: Color
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                fontSize = 22.sp
            )
            TextButton(
                onClick = { /* Handle see all click */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryColor
                )
            ) {
                Text("Edit")
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                BookCard(book, primaryColor)
            }
        }
    }
}

@Composable
fun BookCard(book: BooksResponseItem, primaryColor: Color) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(primaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = book.volumeInfo.imageLinks?.thumbnail,
                    contentDescription = "Book cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = book.volumeInfo.title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = primaryColor
                )
                Text(
                    text = book.volumeInfo.authors?.joinToString() ?: "Unknown Author",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
@Composable
fun FeaturedBanner(
    username: String,
    primaryColor: Color,
    primaryVariantColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryVariantColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Left Section: Text Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Hello, $username!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Discover, track, and\nbuild your reading list.",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    lineHeight = 20.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }

            // Right Section: Decorative Stack of Books

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopAppBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = colorResource(R.color.primary_color)

    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onSearchClick)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Search for your books!",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        ),
        modifier = modifier
    )
}

@Composable
private fun HomeBottomNavigation(
    primaryColor: Color,
    primaryVariantColor: Color
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        Triple(Icons.Default.Home, "Home", 0),
        Triple(Icons.Default.Category, "Category", 1),
        Triple(Icons.Default.Bookmark, "Library", 2),
        Triple(Icons.Default.Person, "Profile", 3)
    )

    NavigationBar(
        containerColor = primaryColor
    ) {
        items.forEach { (icon, label, index) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = primaryVariantColor
                )
            )
        }
    }
}