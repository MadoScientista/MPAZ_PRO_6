# MPAZ_PRO_6

Aplicación **Android nativa** (Kotlin + Jetpack Compose + Material 3) que consume la API REST de
planificación pedagógica **MPAZ**. El backend Spring Boot vive **fuera de este repositorio**; el
contrato disponible está en [`specs/endpoints`](specs/endpoints).

- `applicationId` / `namespace`: `com.ccstudio.mpaz_pro_6`
- `minSdk 24`, `compileSdk` / `targetSdk 37`
- Gradle 9.8.0, AGP 9.2.1, Kotlin 2.2.10

## Requisitos

- Android Studio con soporte para AGP 9.2.1 (Gradle JDK **17 o superior**).
- Android SDK 37 instalado.
- El SDK se configura solo en `local.properties` (`sdk.dir=...`), que **no se versiona**.

## JDK de Gradle: configuración por máquina

El JDK con el que corre Gradle **no se define en el repositorio**. Cada desarrollador usa el que
tenga instalado, sin importar el sistema operativo:

| Sistema | Gradle JDK recomendado | Ruta típica |
|---|---|---|
| Windows | JBR que trae Android Studio (`jbr-25`) | `C:\Program Files\Android\Android Studio\jbr` |
| Linux | JBR del IDE o JDK del sistema (`jbr-25` / OpenJDK 25) | `~/.local/share/JetBrains/Toolbox/apps/android-studio/jbr` |

Se elige en **Android Studio → Settings → Build, Execution, Deployment → Build Tools → Gradle →
Gradle JDK**.

> **No definir `org.gradle.java.home` en `gradle.properties`.**
> Esa propiedad es configuración de cada máquina, no del proyecto. Una ruta absoluta versionada
> (por ejemplo la de Windows) hace fallar el sync en el otro sistema operativo con
> `Value '...' given for org.gradle.java.home Gradle property is invalid`.
> Si hace falta fijar un JDK, se define fuera del repo:
> - Linux/macOS: `~/.gradle/gradle.properties`
> - Windows: `%USERPROFILE%\.gradle\gradle.properties`

Por el mismo motivo la carpeta `.idea/` **no se versiona**: cada IDE guarda ahí su configuración
local (Gradle JDK, dispositivo seleccionado, inspecciones) y Android Studio la regenera al abrir el
proyecto. `local.properties` también está ignorado por `.gitignore`.

## Comandos

```bash
./gradlew assembleDebug        # APK de debug (Linux/macOS)
./gradlew compileDebugKotlin   # solo compila Kotlin
./gradlew test                 # tests unitarios JVM
./gradlew lintDebug            # lint de Android
./gradlew installDebug         # instala en el dispositivo conectado

gradlew.bat assembleDebug      # equivalente en Windows (CMD/PowerShell)
```

En Linux/macOS, si `./gradlew` responde *Permission denied*, restaurar el permiso de ejecución con
`chmod +x gradlew`. El script se versiona con ese permiso, así que no debería ser necesario.

## Configuración de la API

La URL base y el modo local se toman de propiedades de Gradle o variables de entorno:

| Propiedad / variable | Default | Uso |
|---|---|---|
| `MPAZ_BASE_URL` | `http://10.0.2.2:8080/` | Backend en el emulador; en dispositivo físico usar la IP LAN |
| `MPAZ_LOCAL_ONLY` | `true` | Login local y CRUD contra Room, sin backend |

```bash
./gradlew assembleDebug -PMPAZ_BASE_URL=http://192.168.1.10:8080/
./gradlew assembleDebug -PMPAZ_LOCAL_ONLY=false
```

Las llamadas autenticadas usan `Authorization: Bearer <token>` (JWT en DataStore).

En modo local, la base Room solo se siembra al crearse. Si cambia `assets/database/seed.sql`,
hay que borrar los datos de la app o desinstalarla para que se vuelva a cargar la semilla:

```bash
adb shell pm clear com.ccstudio.mpaz_pro_6
```

## Firma del APK release

El APK de release no usa la firma de debug. La configuración se toma de
`keystore.properties`, un archivo local que no se versiona:

```properties
storeFile=/ruta/a/mpaz-release.jks
storePassword=...
keyAlias=mpaz-release
keyPassword=...
```

Generación de ejemplo:

```bash
keytool -genkeypair -v \
  -keystore ~/mpaz-release.jks \
  -alias mpaz-release \
  -keyalg RSA -keysize 2048 -validity 10000
```

Luego:

```bash
./gradlew assembleRelease
```

Sin `keystore.properties`, el build de debug sigue funcionando, pero
`assembleRelease` falla con un mensaje explícito.

## Documentación

- [`AGENTS.md`](AGENTS.md): arquitectura, convenciones, reglas de trabajo y estado del proyecto.
- [`specs/endpoints`](specs/endpoints): contrato de la API (fuente de verdad).
- [`specs/components.md`](specs/components.md): inventario de componentes de UI.
