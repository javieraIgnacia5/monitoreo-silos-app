package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.monitoreosilos.data.repository.UserRepository
import com.example.monitoreosilos.domain.validator.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val acceptedTerms: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val termsError: String? = null,
    val success: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update {
        it.copy(
            name = value,
            nameError = if (it.nameError != null) AuthValidator.validateName(value) else null
        )
    }

    fun onEmailChange(value: String) = _state.update {
        it.copy(
            email = value,
            emailError = if (it.emailError != null) AuthValidator.validateEmail(value) else null
        )
    }

    fun onPasswordChange(value: String) = _state.update {
        it.copy(
            password = value,
            passwordError = if (it.passwordError != null) AuthValidator.validateNewPassword(value) else null,
            confirmError = if (it.confirmError != null) AuthValidator.validateConfirm(value, it.confirm) else null
        )
    }

    fun onConfirmChange(value: String) = _state.update {
        it.copy(
            confirm = value,
            confirmError = if (it.confirmError != null) AuthValidator.validateConfirm(it.password, value) else null
        )
    }

    fun onTermsChange(value: Boolean) = _state.update {
        it.copy(acceptedTerms = value, termsError = if (value) null else it.termsError)
    }

    fun onSubmit() {
        val s = _state.value
        val nameError = AuthValidator.validateName(s.name)
        val emailError = AuthValidator.validateEmail(s.email)
            ?: if (UserRepository.emailExists(s.email)) "Este correo ya está registrado" else null
        val passwordError = AuthValidator.validateNewPassword(s.password)
        val confirmError = AuthValidator.validateConfirm(s.password, s.confirm)
        val termsError = AuthValidator.validateTerms(s.acceptedTerms)
        val ok = listOf(nameError, emailError, passwordError, confirmError, termsError).all { it == null }
        if (ok) UserRepository.register(s.name, s.email, s.password)
        _state.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmError = confirmError,
                termsError = termsError,
                success = ok
            )
        }
    }
}