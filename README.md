# MascotasVeteBack

Backend de Mascotas Córdoba (Java 21, Spring Boot, SQL Server). Implementa el Registro Único de Mascotas, el carnet sanitario digital y las demás funcionalidades del programa municipal listadas en `CLAUDE.md`.

## Qué hay adentro

```
src/main/java/ubp/das/backndvt/
 ├── config/       → Seguridad, CORS
 ├── controller/   → Endpoints REST
 ├── service/      → Lógica de negocio
 ├── repository/   → Acceso a la base de datos
 ├── entity/       → Mapeo de las tablas de la base
 ├── dto/          → Datos que entran y salen por la API
 ├── security/     → Autenticación con JWT
 └── exception/    → Manejo de errores
```

## Cómo ejecutar desde el IDE (sin Docker)

Se necesita tener SQL Server corriendo en la PC, con la base `MascotasCordoba` ya creada (script `mascotas-cordoba-completo.sql`).

1. Definir la variable de entorno `DB_PASSWORD` con la contraseña del usuario de SQL Server (`sa` por defecto).
2. Si el usuario, el puerto o el nombre de la base son distintos a los valores por defecto, definir también `DB_USER`, `DB_PORT` y/o `DB_NAME`.
3. Definir la variable de entorno `JWT_SECRET` (una cadena de al menos 32 caracteres).
4. Ejecutar `BackendVeterinariasApplication` desde el IDE, o `mvnw spring-boot:run` desde la terminal.

## Cómo ejecutar con Docker

Esta sección está pensada para poder seguirse paso a paso, sin necesitar saber programar.

### Idea general

El backend corre **dentro** de Docker (empaquetado como archivo WAR, sobre un servidor Tomcat). El SQL Server **no** corre dentro de Docker: sigue instalado en la PC como hasta ahora, y el backend en Docker se conecta a él desde afuera.

### Paso 0: preparar SQL Server (una sola vez)

Docker necesita poder conectarse al SQL Server de la PC desde "afuera" (como si fuera otra computadora), así que hay que habilitar unas opciones que por defecto están apagadas.

1. **Habilitar el protocolo de red TCP/IP:**
   - Abrir **SQL Server Configuration Manager**.
   - Ir a **Configuración de red de SQL Server** → **Protocolos de \<nombre de instancia\>**.
   - Hacer clic derecho sobre **TCP/IP** → **Habilitar**.
   - Doble clic en **TCP/IP** → pestaña **Direcciones IP** → bajar hasta **IPAll** → confirmar que **Puerto TCP** sea `1433`.
   - Reiniciar el servicio de SQL Server (desde la misma herramienta, o desde **Servicios** de Windows).

2. **Habilitar el modo de autenticación mixto:**
   - Abrir **SQL Server Management Studio (SSMS)** y conectarse al servidor.
   - Clic derecho sobre el servidor (arriba de todo, en el Object Explorer) → **Propiedades**.
   - Ir a la página **Seguridad** → elegir **Modo de autenticación de SQL Server y Windows**.
   - Aceptar, y reiniciar el servicio de SQL Server.

3. **Confirmar el usuario y la contraseña:**
   - En SSMS, ir a **Seguridad** → **Inicios de sesión**.
   - Si se va a usar `sa`, hacer doble clic sobre él, confirmar que esté **habilitado** (pestaña **Estado**) y definir/recordar su contraseña (pestaña **General**).
   - Confirmar que ese usuario tenga permisos sobre la base `MascotasCordoba` (pestaña **Asignación de usuarios**).

4. **Permitir el puerto en el Firewall de Windows** (si la conexión falla por esto):
   - Abrir **Firewall de Windows Defender con seguridad avanzada**.
   - **Reglas de entrada** → **Nueva regla** → **Puerto** → **TCP** → puerto específico `1433` → **Permitir la conexión**.

### Paso 1: configurar las variables de entorno

1. Copiar el archivo `.env.example` y renombrar la copia a `.env` (en la raíz de este proyecto).
2. Abrir `.env` y completar `DB_PASSWORD` con la contraseña real del usuario de SQL Server. Revisar también que `DB_USER`, `DB_PORT` y `DB_NAME` sean correctos.

El archivo `.env` nunca se sube al repositorio (está en `.gitignore`), porque tiene la contraseña real.

### Paso 2: generar el WAR

Desde la raíz de este proyecto:

```
mvnw clean package
```

Esto genera el archivo `.war` dentro de la carpeta `target/`, que es lo que la imagen de Docker va a usar.

### Paso 3: levantar el contenedor

```
docker compose up --build
```

La primera vez puede tardar unos minutos porque descarga la imagen de Tomcat. Cuando termine, el backend va a estar escuchando en `http://localhost:8085`.

### Paso 4: probar que funciona

Abrir en el navegador (o probar con Postman):

```
http://localhost:8085/api/v1/atenciones-sanitarias/tipos/cantidad
```

Si devuelve un número, la conexión a la base de datos está funcionando correctamente.

### Problemas frecuentes

| Mensaje / síntoma | Qué significa | Cómo resolverlo |
|---|---|---|
| `Login failed for user 'sa'` | El usuario o la contraseña están mal, o el usuario no tiene permisos sobre la base. | Revisar `DB_USER`/`DB_PASSWORD` en `.env`, y confirmar en SSMS que el usuario esté habilitado y tenga acceso a `MascotasCordoba`. |
| `Connection refused` | SQL Server no está escuchando en el puerto esperado, o el servicio no está corriendo. | Confirmar que el protocolo TCP/IP esté habilitado (Paso 0.1) y que el servicio de SQL Server esté iniciado. |
| El contenedor se queda esperando y después falla por `timeout` | Docker no puede llegar hasta la PC, generalmente por el Firewall de Windows. | Revisar el Paso 0.4 (regla de Firewall para el puerto 1433). |
| `The TCP/IP connection to the host host.docker.internal ... has failed` | Mismo problema que `Connection refused`/`timeout`: Docker no logra alcanzar el SQL Server de la PC. | Repetir el Paso 0 completo y confirmar que el puerto 1433 esté realmente abierto (se puede probar con `Test-NetConnection -ComputerName localhost -Port 1433` en PowerShell). |
| El navegador muestra `404` en `/api/v1/...` | El WAR no se copió con el nombre de contexto esperado, o no terminó de desplegar. | Revisar los logs con `docker compose logs -f`, y confirmar que `target/` tenga un único archivo `.war` antes de `docker compose up --build`. |

### Comandos útiles

```
docker compose up --build      # construir la imagen y levantar el contenedor
docker compose down            # apagar y eliminar el contenedor
docker compose logs -f         # ver los logs del backend en tiempo real
```
