# MPAZ PRO 6° - App Android

Aplicación Android (Jetpack Compose + Room + Retrofit) para el backend
MPAZ PRO 6°. La app funciona en modo remoto contra el backend Spring Boot y
usa Room como caché local; también puede ejecutarse sin backend con
`MPAZ_LOCAL_ONLY=true`.

## Requisitos

- Android Studio o IntelliJ IDEA con soporte para Android.
- Android SDK con **Android SDK Platform-Tools** (`adb`).
- JDK configurado en Android Studio/IntelliJ (Gradle JDK).
- Backend Spring Boot + PostgreSQL levantado en el PC para el modo remoto.
- Teléfono físico con **Depuración USB** o un emulador Android.

`local.properties` contiene la ruta local del SDK (`sdk.dir`), no se versiona y
Android Studio la regenera al abrir el proyecto en otro PC.

La app usa HTTP local. El `AndroidManifest.xml` ya declara
`android:usesCleartextTraffic="true"`, por lo que no hace falta configurar HTTPS
para la demo.

## Matriz de conexión

| Entorno | URL de la app | Requisito |
|---|---|---|
| USB | `http://127.0.0.1:8080/` | `adb reverse tcp:8080 tcp:8080` |
| Emulador | `http://10.0.2.2:8080/` | Backend en el mismo PC |
| Wi-Fi | `http://IP_DEL_PC:8080/` | Mismo Wi-Fi y firewall abierto |

La URL se define al compilar mediante `MPAZ_BASE_URL`. El
`gradle.properties` del repositorio tiene actualmente:

```properties
MPAZ_LOCAL_ONLY=false
MPAZ_BASE_URL=http://127.0.0.1:8080/
```

Por eso el APK compilado con la configuración actual está preparado para USB
con `adb reverse`.

## Backend en el PC

Desde el repositorio del backend, crear el `.env` local si es la primera vez:

```powershell
# Windows
copy .env.example .env
```

```bash
# Linux/macOS
cp .env.example .env
```

Luego levantar los contenedores:

```bash
docker compose up -d --build
curl http://localhost:8080/actuator/health
```

Debe responder `200`. Si usas otro puerto, cambia `APP_PORT` en `.env` y usa ese
mismo puerto en la URL de la app y en `adb reverse`.

## Conexión por USB

### 1. Preparar el teléfono

1. Activar **Opciones de desarrollador**.
2. Activar **Depuración USB**.
3. Conectar el teléfono por USB.
4. Aceptar el diálogo **“¿Permitir depuración USB?”** en el teléfono.

### 2. Verificar ADB

```bash
adb devices
```

Debe aparecer el dispositivo como `device`:

```text
List of devices attached
XXXXXXXX    device
```

Si aparece `unauthorized`, acepta el diálogo RSA o revoca las autorizaciones
USB en el teléfono y vuelve a conectar.

### 3. Crear el túnel USB

```bash
adb reverse tcp:8080 tcp:8080
adb reverse --list
```

Debe aparecer:

```text
host-... tcp:8080 tcp:8080
```

El túnel hace que `127.0.0.1:8080` en el teléfono apunte al `localhost:8080`
del PC. Por eso no necesitas la IP LAN ni abrir el firewall para USB.

> `adb reverse` se pierde si desconectas el cable, reinicias el teléfono o
> reinicias el servidor ADB. Vuelve a ejecutarlo antes de probar.

### 4. Compilar e instalar

Linux/macOS:

```bash
./gradlew installDebug
```

Windows PowerShell/CMD:

```powershell
.\gradlew.bat installDebug
```

También puedes abrir el proyecto y pulsar **Run** desde Android Studio o
IntelliJ.

### 5. Iniciar sesión

Usa cualquiera de los usuarios de demo del backend, por ejemplo:

| Usuario | Contraseña | Rol |
|---|---|---|
| `utp` | `mpaz123` | UTP |
| `docente` | `mpaz123` | Docente |
| `alumno` | `mpaz123` | Estudiante |

## Ejecutar desde Android Studio

1. Abrir la carpeta raíz del proyecto mobile.
2. Esperar a que termine **Gradle Sync**.
3. Verificar el **Gradle JDK** en `Settings > Build, Execution, Deployment >
   Build Tools > Gradle`.
4. Conectar el teléfono y comprobar que aparezca en el selector de
   dispositivos.
5. Abrir la pestaña **Terminal** de Android Studio y ejecutar:

   ```bash
   adb reverse tcp:8080 tcp:8080
   adb reverse --list
   ```

6. Seleccionar el dispositivo físico y pulsar **Run ‘app’**.
7. Si falla la conexión, abrir **Logcat** y filtrar por `OkHttp`,
   `ConnectException` o `HTTP FAILED`.

Android Studio **no crea el túnel `adb reverse` automáticamente**. Debes
ejecutarlo antes de probar y repetirlo si reconectas el teléfono.

## Ejecutar desde IntelliJ IDEA

1. Abrir la carpeta raíz del proyecto mobile.
2. Verificar que el plugin/soporte de Android esté habilitado.
3. Configurar el Android SDK y el Gradle JDK.
4. Sincronizar Gradle.
5. Crear o seleccionar una configuración **Android App**.
6. Abrir el terminal integrado y ejecutar:

   ```bash
   adb reverse tcp:8080 tcp:8080
   adb reverse --list
   ```

7. Seleccionar el dispositivo y ejecutar la configuración.

Para proyectos Jetpack Compose, Android Studio suele ser la opción más directa;
IntelliJ necesita el plugin de Android correctamente configurado.

## Conexión por emulador

La forma más simple es usar la URL especial del emulador:

```bash
./gradlew installDebug -PMPAZ_BASE_URL=http://10.0.2.2:8080/
```

`10.0.2.2` es la dirección del PC host vista desde el emulador Android. El
backend debe estar corriendo en el mismo PC.

Alternativa: dejar `127.0.0.1` y ejecutar también:

```bash
adb reverse tcp:8080 tcp:8080
```

En ese caso el emulador usa el mismo túnel que un teléfono físico.

## Conexión por Wi-Fi

1. PC y teléfono deben estar en la misma red Wi-Fi, sin aislamiento de clientes.
2. Obtener la IP del PC:

   Windows:

   ```powershell
   ipconfig
   ```

   Linux/macOS:

   ```bash
   hostname -I
   # o
   ip addr
   ```

3. Abrir el puerto `8080/TCP` en el firewall del PC.
4. Compilar apuntando a esa IP:

   ```bash
   ./gradlew installDebug -PMPAZ_BASE_URL=http://192.168.1.50:8080/
   ```

   Cambia `192.168.1.50` por la IP real del PC.

5. Probar desde el navegador del teléfono:

   ```text
   http://192.168.1.50:8080/actuator/health
   ```

Si responde `200`, la app también debería conectar. En Wi-Fi **no necesitas**
`adb reverse`; ese túnel es para USB o para el emulador.

## Cambiar la URL sin recompilar

Actualmente `MPAZ_BASE_URL` es una propiedad de compilación (`BuildConfig`), por
lo que cambiarla requiere reinstalar el APK. Para la presentación:

- USB: usa `http://127.0.0.1:8080/` + `adb reverse`.
- Emulador: usa `http://10.0.2.2:8080/` o `adb reverse`.
- Wi-Fi: recompila con la IP del PC.

Si se implementa el selector de servidor en tiempo de ejecución, la misma APK
podrá cambiar entre estas URLs desde el login sin recompilar.

## Cambiar el puerto del backend

Si en el backend usas, por ejemplo, `APP_PORT=9090`:

```bash
adb reverse tcp:9090 tcp:9090
```

Y compila la app con:

```bash
./gradlew installDebug -PMPAZ_BASE_URL=http://127.0.0.1:9090/
```

La URL, el puerto publicado por Docker y `adb reverse` deben coincidir.

## Cambiar de PC o de backend

Si la app tenía una sesión contra otro PC/base de datos:

1. Cerrar sesión, o
2. Usar **Resetear datos locales** en la pantalla de login.

Esto limpia el token y Room. Si el backend usa otro `MPAZ_JWT_SECRET`, el token
anterior será inválido y la app pedirá login de nuevo.

## Modo local sin backend

Para una demo completamente offline:

```bash
./gradlew installDebug -PMPAZ_LOCAL_ONLY=true
```

En este modo la app usa el seed de Room y el login local; no necesita el
backend ni `adb reverse`.

## Solución de problemas

| Problema | Causa / solución |
|---|---|
| `adb: command not found` | Agregar `platform-tools` al PATH o usar el `adb` del SDK de Android. En Windows suele estar en `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`. |
| `device unauthorized` | Aceptar el diálogo RSA o revocar autorizaciones USB y reconectar. |
| `adb devices` vacío | Revisar cable, modo USB y depuración USB. |
| `adb reverse --list` vacío | Ejecutar de nuevo `adb reverse tcp:8080 tcp:8080`. |
| `Connection refused` | Backend apagado, puerto incorrecto o `adb reverse` no activo. |
| `401 Unauthorized` | Token viejo o backend distinto: cerrar sesión/resetear datos y volver a entrar. |
| La app conecta por USB pero no por Wi-Fi | Revisar IP, firewall, misma red y aislamiento de clientes. |
| Varios dispositivos conectados | Usar `adb -s <serial> reverse tcp:8080 tcp:8080`. |
| El emulador no conecta | Usar `10.0.2.2:8080` o ejecutar `adb reverse`. |

## Estructura rápida

```text
app/src/main/java/com/ccstudio/mpaz_pro_6/
├── data/remote/       # Retrofit, JWT y sincronización
├── data/local/        # Room, DAOs y DataStore
├── ui/                # Compose: login, UTP, docente y estudiante
└── MpazApplication.kt
```

La URL base se usa en `data/remote/ApiClient.kt` y se obtiene de
`BuildConfig.MPAZ_BASE_URL`.
