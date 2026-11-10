package com.ccstudio.mpaz_pro_6.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.R

/** Wordmark aspect ratio (1174 x 175). */
private const val WORDMARK_ASPECT_RATIO = 1174f / 175f

/**
 * Splash de arranque con el wordmark de MPAZ.
 *
 * El fondo usa el color de fondo del tema activo y el wordmark cambia entre la
 * versión clara y oscura.
 */
@Composable
fun SplashScreen(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                if (isDarkTheme) {
                    R.drawable.splash_wordmark_dark
                } else {
                    R.drawable.splash_wordmark_light
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .widthIn(max = 420.dp)
                .aspectRatio(WORDMARK_ASPECT_RATIO),
            contentScale = ContentScale.Fit
        )
    }
}
