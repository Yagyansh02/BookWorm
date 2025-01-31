package com.example.bookworm.presentation

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookworm.Navigation.Screens
import com.example.bookworm.R
import com.example.bookworm.presentation.Authentication.AuthenticationViewModel
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthenticationViewModel = hiltViewModel()) {

    val authValue by authViewModel.isUserAuthenticated.collectAsState()

    // State for scale and alpha animation
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // State for typewriter effect
    var displayedText by remember { mutableStateOf("") }
    val fullText = "Book Worm"

    LaunchedEffect(key1 = true) {
        // Animate both scale and fade-in (alpha) together
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 1500, easing = {
                OvershootInterpolator(2f).getInterpolation(it)
            })
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )

        // Typewriter effect logic
        for (i in fullText.indices) {
            displayedText = fullText.substring(0, i + 1)
            delay(100)  // Adjust delay for typing speed
        }

        delay(2000)

        // Navigation logic after animation completes
        if (authValue) {
            navController.navigate(Screens.HomeScreen.route) {
                popUpTo(Screens.SplashScreen.route) {
                    inclusive = true
                }
            }
        } else {
            navController.navigate(Screens.LoginScreen.route) {
                popUpTo(Screens.SplashScreen.route) {
                    inclusive = true
                }
            }
        }
    }

    // Cool animated UI
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF502f4c),  // Primary color
                        Color(0xFFB38D97)   // Lighter variant
                    )
                )
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Splash Screen Logo",
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .size(320.dp)  // Adjust size as needed
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = displayedText,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace   ,
                color = colorResource(R.color.primary_color),
                modifier = Modifier.alpha(alpha.value)  // Fade-in effect along with scale
            )
        }
    }
}
