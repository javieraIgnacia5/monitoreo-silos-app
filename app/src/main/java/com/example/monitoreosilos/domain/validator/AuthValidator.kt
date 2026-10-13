package com.example.monitoreosilos.domain.validator

object AuthValidator {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateName(name: String): String? = when {
        name.isBlank() -> "Ingresa tu nombre completo"
        name.trim().length < 3 -> "El nombre es muy corto"
        !name.all { it.isLetter() || it == ' ' } -> "Solo se permiten letras"
        else -> null
    }

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Ingresa tu correo"
        !emailRegex.matches(email.trim()) -> "El correo no es válido"
        else -> null
    }

    /** Para el login solo se exige que no esté vacía. */
    fun validateLoginPassword(password: String): String? = when {
        password.isBlank() -> "Ingresa tu contraseña"
        else -> null
    }

    /** Para el registro se exigen reglas de seguridad. */
    fun validateNewPassword(password: String): String? = when {
        password.isBlank() -> "Ingresa una contraseña"
        password.length < 8 -> "Mínimo 8 caracteres"
        !password.any { it.isDigit() } -> "Debe incluir al menos un número"
        !password.any { it.isLetter() } -> "Debe incluir al menos una letra"
        else -> null
    }

    fun validateConfirm(password: String, confirm: String): String? = when {
        confirm.isBlank() -> "Confirma tu contraseña"
        password != confirm -> "Las contraseñas no coinciden"
        else -> null
    }

    fun validateTerms(accepted: Boolean): String? =
        if (accepted) null else "Debes aceptar los términos"
}
