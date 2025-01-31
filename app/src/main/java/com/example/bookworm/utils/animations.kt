package com.example.bookworm.utils

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource

@Composable
fun Login_animation(){
    DotLottieAnimation(
        source = DotLottieSource.Asset("login_animation.lottie"),
        autoplay = true,
        loop = true,
        speed = 1f,
        useFrameInterpolation = false,
        playMode = Mode.FORWARD,
        modifier= Modifier.size(320.dp)
    )
}