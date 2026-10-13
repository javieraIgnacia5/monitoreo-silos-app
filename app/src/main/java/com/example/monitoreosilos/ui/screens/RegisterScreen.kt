package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.R
import com.example.monitoreosilos.ui.components.AppTextField
import com.example.monitoreosilos.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Box(Modifier.fillMaxSize()) {
        // Hojas decorativas del diseño
        Image(
            painter = painterResource(R.drawable.logo_login),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
                .rotate(12f)
                .alpha(0.18f)
        )
        Image(
            painter = painterResource(R.drawable.logo_login),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(150.dp)
                .rotate(-25f)
                .alpha(0.18f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Únete a SiloControl", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))

            AppTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Nombre completo",
                leadingIcon = Icons.Filled.Person,
                error = state.nameError
            )
            Spacer(Modifier.height(8.dp))
            AppTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "Correo electrónico",
                leadingIcon = Icons.Filled.Email,
                error = state.emailError,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(8.dp))
            AppTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                leadingIcon = Icons.Filled.Lock,
                error = state.passwordError,
                isPassword = true
            )
            Spacer(Modifier.height(8.dp))
            AppTextField(
                value = state.confirm,
                onValueChange = viewModel::onConfirmChange,
                label = "Confirmar contraseña",
                leadingIcon = Icons.Filled.Lock,
                error = state.confirmError,
                isPassword = true
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.acceptedTerms,
                    onCheckedChange = viewModel::onTermsChange
                )
                Text(
                    "Acepto los Términos y Condiciones y la Política de Privacidad",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (state.termsError != null) {
                Text(
                    state.termsError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = viewModel::onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Registrarme", fontWeight = FontWeight.Bold) }
        }
    }
}