package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.monitoreosilos.data.repository.UserRepository
import com.example.monitoreosilos.domain.validator.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val success: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update {
        it.copy(
            email = value,
            emailError = if (it.emailError != null) AuthValidator.validateEmail(value) else null
        )
    }

    fun onPasswordChange(value: String) = _state.update {
        it.copy(
            password = value,
            passwordError = if (it.passwordError != null) AuthValidator.validateLoginPassword(value) else null
        )
    }

    fun onSubmit() {
        val current = _state.value
        val emailError = AuthValidator.validateEmail(current.email)
        val formError = AuthValidator.validateLoginPassword(current.password)
        val formOk = emailError == null && formError == null
        val validAccount = formOk && UserRepository.login(current.email, current.password)
        _state.update {
            it.copy(
                emailError = emailError,
                passwordError = formError
                    ?: if (formOk && !validAccount) "Correo o contraseña incorrectos" else null,
                success = validAccount
            )
        }
    }
}