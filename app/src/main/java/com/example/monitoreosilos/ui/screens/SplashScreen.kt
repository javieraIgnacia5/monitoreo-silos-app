
package com.example.monitoreosilos.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreosilos.R
import kotlinx.coroutines.delay

private val TealProfundo = Color(0xFF0E6973)
private val AmarilloLimon = Color(0xFFF2BB16)

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    var iniciarAnimacion by remember {
        mutableStateOf(false)
    }

    val progreso by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 2800),
        label = "ProgresoSplash"
    )

    LaunchedEffect(Unit) {
        iniciarAnimacion = true
        delay(3000)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Fotografía de los silos
        Image(
            painter = painterResource(R.drawable.fondo_silos),
            contentDescription = "Silos agrícolas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Degradado para mejorar la lectura
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x550E6973),
                            Color(0x33000000),
                            Color(0xCC062F34)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 55.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌾",
                    fontSize = 65.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SiloControl",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Control hoy,\ncosecha mañana",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .width(190.dp)
                        .height(7.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = AmarilloLimon,
                    trackColor = TealProfundo
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Monitorea tu producción",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}
