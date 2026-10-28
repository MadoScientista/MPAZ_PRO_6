package com.ccstudio.mpaz_pro_6.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository
import kotlinx.coroutines.launch

/** Estado del formulario de creación de una alternativa. */
class AlternativeFormViewModel(
    private val alternativeRepository: AlternativeRepository,
    private val questionId: Int
) : ViewModel() {

    var texto by mutableStateOf("")
        private set

    var esCorrecta by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun onTextoChange(value: String) {
        texto = value
        error = null
    }

    fun onEsCorrectaChange(value: Boolean) {
        esCorrecta = value
        error = null
    }

    fun guardar(onSaved: () -> Unit) {
        val textoLimpio = texto.trim()
        if (textoLimpio.isBlank()) {
            error = "El texto de la alternativa es obligatorio."
            return
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = alternativeRepository.create(
                CreateAlternativeRequest(
                    preguntaId = questionId,
                    texto = textoLimpio,
                    esCorrecta = esCorrecta
                )
            )

            isSaving = false
            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            onSaved()
        }
    }

    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> error.message
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "La pregunta ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la alternativa."
    }

    companion object {

        fun factory(
            alternativeRepository: AlternativeRepository,
            questionId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlternativeFormViewModel(
                    alternativeRepository = alternativeRepository,
                    questionId = questionId
                )
            }
        }
    }
}
