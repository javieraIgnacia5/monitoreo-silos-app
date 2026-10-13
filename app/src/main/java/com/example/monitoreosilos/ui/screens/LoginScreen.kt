package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.R
import com.example.monitoreosilos.ui.components.AppTextField
import com.example.monitoreosilos.viewmodel.LoginViewModel
import androidx.compose.foundation.Image

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Image(
            painter = painterResource(R.drawable.logo_login),
            contentDescription = "Logo",
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "SiloControl",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Controla hoy, cosecha mañana", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(28.dp))

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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {}) { Text("¿Olvidaste tu contraseña?") }
        }

        Button(
            onClick = viewModel::onSubmit,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Iniciar sesión", fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f))
            Text("  o continúa con  ", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SocialButton(R.drawable.ic_google, "Google")
            SocialButton(R.drawable.ic_apple, "Apple")
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes una cuenta?")
            TextButton(onClick = onRegisterClick) { Text("Regístrate", fontWeight = FontWeight.Bold) }
        }
    }
}

/** Botón redondo solo visual (el caso usa datos ficticios). */
@Composable
private fun SocialButton(iconRes: Int, description: String) {
    OutlinedButton(
        onClick = {},
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.size(52.dp)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = description,
            modifier = Modifier.fillMaxSize()
        )
    }
}