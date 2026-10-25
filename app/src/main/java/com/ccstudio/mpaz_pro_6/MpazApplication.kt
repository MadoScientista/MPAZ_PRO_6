package com.ccstudio.mpaz_pro_6

import android.app.Application
import com.ccstudio.mpaz_pro_6.data.AppContainer

/**
 * Aplicación de MPAZ.
 *
 * Mantiene una única instancia de [AppContainer] por proceso para que Room,
 * DataStore y OkHttp se compartan entre pantallas y sobrevivan a los cambios de
 * configuración.
 */
class MpazApplication : Application() {

    val container: AppContainer by lazy { AppContainer(applicationContext) }
}
