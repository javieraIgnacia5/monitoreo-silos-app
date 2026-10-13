package com.example.monitoreosilos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.R

/** Foto del silo según su id (S1 a S4). */
@Composable
fun SiloThumb(siloId: String, modifier: Modifier = Modifier) {
    val res = when (siloId) {
        "S1" -> R.drawable.silo_thumb_1
        "S2" -> R.drawable.silo_thumb_2
        "S3" -> R.drawable.silo_thumb_3
        else -> R.drawable.silo_thumb_4
    }
    Image(
        painter = painterResource(res),
        contentDescription = "Foto del silo",
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(14.dp))
    )
}

/** Foto grande de la parte superior del detalle. */
@Composable
fun SiloHeaderImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.silo_header),
        contentDescription = "Foto del silo",
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(20.dp))
    )
}

/** Círculo de porcentaje que se llena con animación. */
@Composable
fun CircleGauge(
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val sweep by animateFloatAsState(
        targetValue = if (started) percent / 100f else 0f,
        animationSpec = tween(1000),
        label = "gauge"
    )
    val track = MaterialTheme.colorScheme.outline
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = track, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke)
            )
            drawArc(
                color = color, startAngle = -90f, sweepAngle = 360f * sweep, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        content()
    }
}

/** Gráfico de barras. values van de 0f a 1f. */
@Composable
fun BarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.secondary
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(900),
        label = "bars"
    )
    Row(
        modifier.fillMaxWidth().height(110.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { i, v ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .width(16.dp)
                        .height((80f * v * progress).dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
                Text(labels.getOrElse(i) { "" }, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}