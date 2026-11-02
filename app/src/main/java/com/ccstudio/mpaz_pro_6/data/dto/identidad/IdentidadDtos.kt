package com.ccstudio.mpaz_pro_6.data.dto.identidad

import kotlinx.serialization.Serializable

// Resúmenes
@Serializable
data class RolResumen(
    val id: Long,
    val nombre: String
)

@Serializable
data class NivelResumen(
    val id: Long,
    val nombre: String
)

@Serializable
data class SeccionResumen(
    val id: Long,
    val letra: String
)

@Serializable
data class CursoResumen(
    val id: Long,
    val annio: Int,
    val nivel: NivelResumen,
    val seccion: SeccionResumen
)

@Serializable
data class UsuarioResumen(
    val id: Long,
    val nombreUsuario: String,
    val nombreCompleto: String
)

// DTOs Rol
@Serializable
data class RolRequest(
    val nombre: String
)

@Serializable
data class RolUpdateRequest(
    val nombre: String? = null
)

@Serializable
data class RolResponse(
    val id: Long,
    val nombre: String
)

// DTOs Nivel
@Serializable
data class NivelRequest(
    val nombre: String
)

@Serializable
data class NivelUpdateRequest(
    val nombre: String? = null
)

@Serializable
data class NivelResponse(
    val id: Long,
    val nombre: String
)

// DTOs Seccion
@Serializable
data class SeccionRequest(
    val letra: String
)

@Serializable
data class SeccionUpdateRequest(
    val letra: String? = null
)

@Serializable
data class SeccionResponse(
    val id: Long,
    val letra: String
)

// DTOs Curso
@Serializable
data class CursoRequest(
    val annio: Int,
    val nivelId: Long,
    val seccionId: Long
)

@Serializable
data class CursoUpdateRequest(
    val annio: Int? = null,
    val nivelId: Long? = null,
    val seccionId: Long? = null
)

@Serializable
data class CursoResponse(
    val id: Long,
    val annio: Int,
    val nivel: NivelResumen,
    val seccion: SeccionResumen
)

// DTOs Usuario
@Serializable
data class UsuarioRequest(
    val nombreUsuario: String,
    val password: String,
    val pnombre: String,
    val snombre: String? = null,
    val apaterno: String,
    val amaterno: String? = null,
    val rolId: Long,
    val activo: Boolean = true
) {
    override fun toString(): String {
        return "UsuarioRequest(nombreUsuario='$nombreUsuario', password='***', pnombre='$pnombre', snombre=$snombre, apaterno='$apaterno', amaterno=$amaterno, rolId=$rolId, activo=$activo)"
    }
}

@Serializable
data class UsuarioUpdateRequest(
    val nombreUsuario: String? = null,
    val password: String? = null,
    val pnombre: String? = null,
    val snombre: String? = null,
    val apaterno: String? = null,
    val amaterno: String? = null,
    val rolId: Long? = null
) {
    override fun toString(): String {
        return "UsuarioUpdateRequest(nombreUsuario=$nombreUsuario, password=${if (password != null) "'***'" else "null"}, pnombre=$pnombre, snombre=$snombre, apaterno=$apaterno, amaterno=$amaterno, rolId=$rolId)"
    }
}

@Serializable
data class UsuarioResponse(
    val id: Long,
    val nombreUsuario: String,
    val pnombre: String,
    val snombre: String? = null,
    val apaterno: String,
    val amaterno: String? = null,
    val rol: RolResumen,
    val activo: Boolean
) {
    val nombreCompleto: String
        get() = listOfNotNull(pnombre, snombre, apaterno, amaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}
