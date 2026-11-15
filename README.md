# MPAZ PRO 6° - App Android

App Android con Jetpack Compose, Room y Retrofit para el backend MPAZ PRO 6°.
En modo remoto usa el backend Spring Boot y guarda el catálogo en Room como
caché local. También puede ejecutarse sin backend con `MPAZ_LOCAL_ONLY=true`.

Esta guía explica paso a paso cómo conectar la app con el backend por **USB**,
por **Wi-Fi** y desde el **emulador**.

---

## 1. Antes de empezar

### Requisitos

- Android Studio o IntelliJ IDEA con soporte para Android.
- Android SDK Platform-Tools (`adb`).
- Backend Spring Boot + PostgreSQL.
- Teléfono con Depuración USB o un emulador.

`local.properties` contiene la ruta local del SDK (`sdk.dir`), no se versiona y
cada PC la genera al abrir el proyecto.

### 1.1. Levantar el backend

En la carpeta del backend, la primera vez:

```powershell
# Windows
copy .env.example .env
```

```bash
# Linux/macOS
cp .env.example .env
```

Luego:

```bash
docker compose up -d --build
curl http://localhost:8080/actuator/health
```

Debe responder `200`.

Si el backend usa otro puerto, cambia `APP_PORT` en `.env` y usa ese mismo
puerto en la URL de la app y en `adb reverse`.

### 1.2. Verificar `adb`

En Android Studio: **SDK Manager → SDK Tools → Android SDK Platform-Tools**.

En una terminal:

```bash
adb version
```

Si responde `adb: command not found`, `adb` existe pero no está en el PATH.
Puedes usar la ruta completa:

```text
Linux/macOS: ~/Android/Sdk/platform-tools/adb
Windows:     %LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
```

Ejemplo Linux/macOS:

```bash
~/Android/Sdk/platform-tools/adb version
```

O agregarlo al PATH:

```bash
# Linux/macOS, Bash/Zsh
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"

# permanente en Bash
echo 'export ANDROID_HOME="$HOME/Android/Sdk"' >> ~/.bashrc
echo 'export PATH="$ANDROID_HOME/platform-tools:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

En Windows PowerShell, para la sesión actual:

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:Path = "$env:ANDROID_HOME\platform-tools;$env:Path"
adb version
```

Alternativa sin modificar el PATH en Linux/macOS:

```bash
mkdir -p ~/.local/bin
ln -sf "$HOME/Android/Sdk/platform-tools/adb" ~/.local/bin/adb
```

---

## 2. Elegir el tipo de conexión

| Entorno | URL de la app | Configuración | ¿Necesita ADB reverse? |
|---|---|---|---|
| USB | `http://127.0.0.1:8080/` | `local.properties` local | Sí |
| Wi-Fi | `http://IP_DEL_PC:8080/` | `-PMPAZ_BASE_URL=...` o `local.properties` | No |
| Emulador | `http://10.0.2.2:8080/` | Fallback por defecto | Opcional |

`MPAZ_BASE_URL` **no se versiona en `gradle.properties`**. Se resuelve al compilar
con esta precedencia:

1. `-PMPAZ_BASE_URL=...`
2. Variable de entorno `MPAZ_BASE_URL`
3. Clave `MPAZ_BASE_URL` en `local.properties`
4. Fallback: `http://10.0.2.2:8080/`

Cada PC configura su archivo local, que está en `.gitignore`:

```properties
sdk.dir=/home/nico/Android/Sdk
MPAZ_BASE_URL=http://127.0.0.1:8080/
```

En Windows, `local.properties` va en la raíz del proyecto con la ruta del SDK de
Windows. Android Studio la genera al abrir el proyecto; la clave
`MPAZ_BASE_URL` se agrega manualmente.

Por eso el APK compilado en este PC está pensado para **USB con `adb reverse`**.
El mismo APK **no conecta por Wi-Fi**, porque en el teléfono `127.0.0.1` es el
propio teléfono, no el PC.

---

## 3. Conexión por USB

### Paso 1: preparar el teléfono

1. Activar **Opciones de desarrollador**.
2. Activar **Depuración USB**.
3. Conectar el teléfono por USB.
4. Aceptar el diálogo **“¿Permitir depuración USB?”** en el teléfono.

### Paso 2: verificar ADB

```bash
adb devices
```

Debe aparecer el teléfono como `device`:

```text
List of devices attached
XXXXXXXX    device
```

Si aparece `unauthorized`:

- Aceptar el diálogo RSA en el teléfono.
- O revocar las autorizaciones USB y volver a conectar.

Si tienes varios dispositivos, usa el serial:

```bash
adb devices
adb -s <serial> reverse tcp:8080 tcp:8080
```

### Paso 3: crear el túnel USB

```bash
adb reverse tcp:8080 tcp:8080
adb reverse --list
```

Debe aparecer:

```text
host-... tcp:8080 tcp:8080
```

Esto hace que `127.0.0.1:8080` en el teléfono apunte al `localhost:8080` del PC.
Por eso **no necesitas la IP LAN ni abrir el firewall** para USB.

> `adb reverse` se pierde si desconectas el cable, reinicias el teléfono o
> reinicias el servidor ADB. Vuelve a ejecutarlo antes de probar.

### Paso 4: compilar e instalar

#### Opción A: terminal

Linux/macOS:

```bash
./gradlew installDebug
```

Windows:

```powershell
.\gradlew.bat installDebug
```

#### Opción B: Android Studio

1. Abrir la carpeta raíz del proyecto.
2. Esperar **Gradle Sync**.
3. Conectar el teléfono y seleccionarlo en la barra superior.
4. Abrir **Terminal** dentro de Android Studio y ejecutar:

   ```bash
   adb reverse tcp:8080 tcp:8080
   adb reverse --list
   ```

5. Pulsar **Run ‘app’**.
6. Abrir **Logcat** si falla la conexión.

Android Studio **no crea el túnel automáticamente**. Debes ejecutar `adb reverse`
antes de probar.

#### Opción C: IntelliJ IDEA

1. Abrir la carpeta raíz del proyecto.
2. Verificar el plugin/soporte de Android y el Android SDK.
3. Sincronizar Gradle.
4. Crear o seleccionar una configuración **Android App**.
5. Abrir el terminal integrado y ejecutar:

   ```bash
   adb reverse tcp:8080 tcp:8080
   adb reverse --list
   ```

6. Seleccionar el dispositivo y ejecutar la configuración.

Para Compose, Android Studio suele ser más directo; IntelliJ necesita el plugin
de Android correctamente configurado.

### Paso 5: iniciar sesión

Usuarios de demo:

| Usuario | Contraseña | Rol |
|---|---|---|
| `utp` | `mpaz123` | UTP |
| `docente` | `mpaz123` | Docente |
| `alumno` | `mpaz123` | Estudiante |

### Verificación USB

- `adb devices` muestra el teléfono como `device`.
- `adb reverse --list` muestra `tcp:8080 tcp:8080`.
- `curl http://localhost:8080/actuator/health` responde `200` en el PC.
- La app inicia sesión y muestra el Home.

---

## 4. Conexión por Wi-Fi

La app actual apunta a `127.0.0.1`; para Wi-Fi hay que recompilar con la IP LAN
del PC. No uses `adb reverse` para este modo.

### Paso 1: misma red y backend

- PC y teléfono en la misma red Wi-Fi.
- Sin aislamiento de clientes en el router.
- Backend levantado:

```bash
docker compose up -d --build
curl http://localhost:8080/actuator/health
```

### Paso 2: obtener la IP LAN del PC

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

Ejemplo: `192.168.1.50`.

### Paso 3: abrir el firewall

Debe permitirse TCP `8080` desde la red local.

Windows: permitir la aplicación/Docker en redes privadas o crear una regla de
entrada TCP 8080.

Linux con UFW:

```bash
sudo ufw allow 8080/tcp
```

### Paso 3.1: permiso de red local en Android 17

Android 17 (API 37) bloquea el acceso a la LAN de las apps que apuntan a
`targetSdk 37` si no tienen el permiso `ACCESS_LOCAL_NETWORK`.

La app lo declara y lo solicita al abrirse cuando `MPAZ_BASE_URL` usa una IP LAN.
Acepta el permiso **Dispositivos cercanos / Red local** cuando aparezca.

Si lo rechazas, la app muestra un aviso y ofrece **Abrir ajustes**. El USB con
`adb reverse` y `127.0.0.1` no necesita este permiso.

### Paso 4: probar el backend desde el teléfono

Abrir el navegador del teléfono:

```text
http://192.168.1.50:8080/actuator/health
```

Debe responder `200`. Si no responde, no sigas con la app: revisa IP, firewall
y que ambos estén en la misma red.

### Paso 5: recompilar con la IP LAN

#### Opción A: terminal

Linux/macOS:

```bash
./gradlew installDebug -PMPAZ_BASE_URL=http://192.168.1.50:8080/
```

Windows:

```powershell
.\gradlew.bat installDebug -PMPAZ_BASE_URL=http://192.168.1.50:8080/
```

Cambia `192.168.1.50` por la IP real.

#### Opción B: Android Studio o IntelliJ

1. Editar `local.properties` en la raíz del proyecto:

   ```properties
   MPAZ_BASE_URL=http://192.168.1.50:8080/
   ```

2. Ejecutar **Gradle Sync**.
3. Pulsar **Run ‘app’** o instalar desde el terminal integrado.
4. Para volver a USB, cambiar la clave a `http://127.0.0.1:8080/`.

También puedes ejecutar en el terminal integrado:

```bash
./gradlew installDebug -PMPAZ_BASE_URL=http://192.168.1.50:8080/
```

`-P` tiene prioridad sobre `local.properties` y evita editar el archivo, pero
sigue siendo una compilación distinta.

### Paso 6: iniciar sesión

No necesitas `adb reverse` en Wi-Fi. Si la IP del PC cambia, hay que recompilar
con la nueva IP.

---

## 5. Emulador

La dirección del PC host vista desde el emulador es `10.0.2.2`:

```bash
./gradlew installDebug -PMPAZ_BASE_URL=http://10.0.2.2:8080/
```

Alternativa: mantener `127.0.0.1` y ejecutar también:

```bash
adb reverse tcp:8080 tcp:8080
```

En ese caso el emulador usa el mismo túnel que un teléfono físico.

---

## 6. ¿Por qué se usa `adb reverse`?

En el teléfono, `127.0.0.1` es el propio teléfono. El backend está en el PC.
`adb reverse` crea un túnel para que el teléfono llegue al PC sin usar la IP LAN
ni abrir el firewall.

Es una herramienta **de desarrollo**, no de producción. En una app normal el
backend suele estar en una URL pública, o se usa la IP LAN. `adb reverse` también
lo usan herramientas como React Native/Expo para su servidor de desarrollo.

Alternativas:

- Wi-Fi con IP LAN.
- Backend desplegado en un servidor o túnel (ngrok/Cloudflare Tunnel).
- Emulador con `10.0.2.2`.

No se usa `adb reverse` en producción.

---

## 7. Cambiar de puerto o de backend

Si el backend usa `APP_PORT=9090`:

```bash
adb reverse tcp:9090 tcp:9090
```

Y compila con:

```bash
./gradlew installDebug -PMPAZ_BASE_URL=http://127.0.0.1:9090/
```

Si cambias de PC o de base de datos:

1. Cerrar sesión, o
2. Usar **Resetear datos locales** en el login.

Si el nuevo backend usa otro `MPAZ_JWT_SECRET`, el token anterior será inválido.

---

## 8. Modo local sin backend

Para una demo completamente offline:

```bash
./gradlew installDebug -PMPAZ_LOCAL_ONLY=true
```

En este modo la app usa el seed de Room y login local; no necesita backend.

---

## 9. Problemas frecuentes

| Problema | Causa / solución |
|---|---|
| `adb: command not found` | Platform-Tools no está en PATH. Usa la ruta completa o expórtalo como se explica en §1.2. |
| `device unauthorized` | Acepta el diálogo RSA o revoca autorizaciones USB y reconecta. |
| `adb devices` vacío | Revisa cable, modo USB y Depuración USB. |
| `adb reverse --list` vacío | Ejecuta de nuevo `adb reverse tcp:8080 tcp:8080`. |
| `Connection refused` | Backend apagado, puerto incorrecto o túnel no activo. |
| `401 Unauthorized` | Token vencido o backend distinto: cierra sesión/resetea datos. |
| La app conecta por USB pero no por Wi-Fi | El APK sigue apuntando a `127.0.0.1`; recompila con la IP LAN. |
| Wi-Fi funciona en el navegador pero no en la app (Android 17) | Falta `ACCESS_LOCAL_NETWORK` o fue denegado; revisa el aviso de la app y los ajustes de la app. |
| Timeout de Wi-Fi en Android 17 | Permiso de red local denegado; el shell/navegador pueden conectar sin ese permiso, la app no. |
| Wi-Fi no responde desde el teléfono | IP incorrecta, firewall, redes distintas o aislamiento de clientes. |
| Varios dispositivos conectados | Usa `adb -s <serial> reverse tcp:8080 tcp:8080`. |

---

## 10. Estructura rápida

```text
app/src/main/java/com/ccstudio/mpaz_pro_6/
├── data/remote/       # Retrofit, JWT y sincronización
├── data/local/        # Room, DAOs y DataStore
├── ui/                # Compose: login, UTP, docente y estudiante
└── MpazApplication.kt
```

La URL base se usa en `data/remote/ApiClient.kt` y se obtiene de
`BuildConfig.MPAZ_BASE_URL`.
