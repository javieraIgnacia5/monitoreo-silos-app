package com.example.monitoreosilos.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (started) 1f else 0f, tween(900), label = "fade")
    val progress by animateFloatAsState(if (started) 1f else 0f, tween(2200), label = "load")

    LaunchedEffect(Unit) {
        started = true
        delay(2500)
        onFinished()
    }

    val teal = Color(0xFF0E4D4D)

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFA9C4DC), Color(0xFFF7E4C4), Color(0xFFF9DDB0))
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.splash_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.58f)
        )
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_splash),
                contentDescription = "Logo",
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "SiloControl",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = teal
            )
            Text(
                "Control hoy,\ncosecha mañana",
                style = MaterialTheme.typography.titleMedium,
                color = teal,
                textAlign = TextAlign.Center
            )
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .width(120.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.5f))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF4CC38A), Color(0xFFF2B01E)))
                    )
            )
        }
    }
}