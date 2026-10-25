package com.ccstudio.mpaz_pro_6.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.themeDataStore by preferencesDataStore(name = "mpaz_theme")

/**
 * Persistencia del modo de tema en DataStore Preferences.
 *
 * Usa un archivo propio (`mpaz_theme`) para que cerrar sesión o restablecer los
 * datos locales no borre la preferencia del usuario.
 */
class ThemePreferenceStore(private val context: Context) {

    /** Modo guardado; `SYSTEM` si todavía no hay una elección explícita. */
    val mode: Flow<ThemeMode> = context.themeDataStore.data
        .catch { exception ->
            // Un archivo ilegible no debe impedir que la app arranque: se usa el
            // modo del sistema y el usuario puede volver a elegir.
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ThemeMode.fromStored(preferences[Keys.MODE])
        }

    /** Guarda el modo elegido por el usuario. */
    suspend fun setMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[Keys.MODE] = mode.name
        }
    }

    private object Keys {
        val MODE = stringPreferencesKey("theme_mode")
    }
}
