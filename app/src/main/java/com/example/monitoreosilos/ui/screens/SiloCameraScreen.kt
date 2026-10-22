
package com.example.monitoreosilos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import com.example.monitoreosilos.data.SiloPhotoStorage
import com.example.monitoreosilos.data.repository.SiloRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloCameraScreen(
    siloId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val silos by SiloRepository.silos.collectAsState()
    val silo = silos.firstOrNull { it.id == siloId }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var cameraFile by remember { mutableStateOf<File?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            message = "Fotografía seleccionada. Presiona Guardar fotografía."
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraFile?.let { file ->
                selectedUri = Uri.fromFile(file)
                message = "Fotografía tomada. Presiona Guardar fotografía."
            }
        } else {
            cameraFile?.delete()
            cameraFile = null
            message = "No se tomó ninguna fotografía."
        }
    }

    val previewBitmap = remember(selectedUri) {
        selectedUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotografía del silo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = silo?.name ?: "Cargando silo...",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Toma una fotografía del silo o selecciónala desde tu galería."
            )

            if (previewBitmap != null) {
                Image(
                    bitmap = previewBitmap,
                    contentDescription = "Vista previa de la fotografía",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Todavía no has seleccionado una fotografía.")
            }

            OutlinedButton(
                onClick = {
                    try {
                        val (file, uri) =
                            SiloPhotoStorage.createCameraPhoto(context)

                        cameraFile = file
                        cameraLauncher.launch(uri)
                    } catch (e: Exception) {
                        message = "No se pudo abrir la cámara: ${e.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tomar fotografía")
            }

            OutlinedButton(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Seleccionar de galería")
            }

            message?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    val uri = selectedUri ?: return@Button

                    saving = true
                    message = null

                    scope.launch {
                        val success = withContext(Dispatchers.IO) {
                            SiloPhotoStorage.savePhoto(
                                context,
                                siloId,
                                uri
                            )
                        }

                        saving = false

                        if (success) {
                            cameraFile?.delete()
                            onSaved()
                        } else {
                            message = "No se pudo guardar la fotografía."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedUri != null && silo != null && !saving
            ) {
                Text(
                    if (saving) "Guardando..."
                    else "Guardar fotografía"
                )
            }
        }
    }
}
