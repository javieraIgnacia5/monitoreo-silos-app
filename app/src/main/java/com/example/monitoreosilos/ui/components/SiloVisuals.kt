
package com.example.monitoreosilos.ui.components

import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.monitoreosilos.R
import com.example.monitoreosilos.data.SiloPhotoStorage

/**
 * Obtiene la imagen predeterminada de cada silo.
 */
private fun defaultSiloImage(siloId: String): Int {
    return when (siloId) {
        "S1" -> R.drawable.silo_thumb_1
        "S2" -> R.drawable.silo_thumb_2
        "S3" -> R.drawable.silo_thumb_3
        "S4" -> R.drawable.silo_thumb_4
        "S5" -> R.drawable.silo_thumb_5
        else -> R.drawable.silo_thumb_1
    }
}

/**
 * Muestra la fotografía personalizada si existe.
 * Actualiza la imagen al regresar a la pantalla.
 */
@Composable
private fun SiloPhoto(
    siloId: String,
    modifier: Modifier,
    cornerRadius: Dp
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var refreshCount by remember(siloId) {
        mutableIntStateOf(0)
    }

    DisposableEffect(lifecycleOwner, siloId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshCount++
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val customBitmap = remember(siloId, refreshCount) {
        try {
            SiloPhotoStorage.getPhoto(context, siloId)?.let { file ->
                BitmapFactory.decodeFile(file.absolutePath)
                    ?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    if (customBitmap != null) {
        Image(
            bitmap = customBitmap,
            contentDescription = "Fotografía del silo $siloId",
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(
                RoundedCornerShape(cornerRadius)
            )
        )
    } else {
        Image(
            painter = painterResource(
                defaultSiloImage(siloId)
            ),
            contentDescription = "Fotografía del silo $siloId",
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(
                RoundedCornerShape(cornerRadius)
            )
        )
    }
}

/**
 * Fotografía pequeña utilizada en la lista de silos.
 */
@Composable
fun SiloThumb(
    siloId: String,
    modifier: Modifier = Modifier
) {
    SiloPhoto(
        siloId = siloId,
        modifier = modifier,
        cornerRadius = 14.dp
    )
}

/**
 * Fotografía grande del detalle del silo.
 */
@Composable
fun SiloHeaderImage(
    siloId: String,
    modifier: Modifier = Modifier
) {
    SiloPhoto(
        siloId = siloId,
        modifier = modifier,
        cornerRadius = 20.dp
    )
}

/**
 * Círculo de porcentaje que se llena con animación.
 */
@Composable
fun CircleGauge(
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    var started by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        started = true
    }

    val sweep by animateFloatAsState(
        targetValue = if (started) percent / 100f else 0f,
        animationSpec = tween(1000),
        label = "gauge"
    )

    val track = MaterialTheme.colorScheme.outline

    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(
                size.width - stroke,
                size.height - stroke
            )

            drawArc(
                color = track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    stroke,
                    cap = StrokeCap.Round
                )
            )
        }

        content()
    }
}

/**
 * Gráfico de barras. Los valores van de 0f a 1f.
 */

/**
 * Gráfico de barras con porcentajes.
 * Los valores van de 0f a 1f.
 */
@Composable
fun BarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.secondary
) {
    var started by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        started = true
    }

    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(900),
        label = "bars"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { i, value ->

            val percentage = (value * 100)
                .toInt()
                .coerceIn(0, 100)

            val barHeight = 80f *
                    value.coerceIn(0f, 1f) *
                    progress

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )

                Spacer(Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .width(20.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = labels.getOrElse(i) { "" },
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

