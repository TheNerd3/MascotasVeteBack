# Datos de prueba — Mascotas Córdoba

Generados por `datos-prueba.sql`, corrido y verificado contra la base `MASCOTAS` real (SQL Server, `localhost`). Idempotente: se corrió dos veces y los IDs/NRM no cambiaron.

Requiere haber corrido antes `mascotas-cordoba-schema-completo.sql` (schema + semillas + SP + vista + triggers), con el fix de `UQ_mascotas_microchip` como índice único filtrado (`WHERE microchip IS NOT NULL`) en vez de `UNIQUE` simple — en SQL Server un `UNIQUE` normal solo permite **un** NULL en toda la tabla, no varios.

## Usuarios (ciudadanos)

Clave en texto plano para todos: **`Prueba123!`** (hash BCrypt real, generado con el mismo `BCryptPasswordEncoder` del backend).

| id_ciudadano | CUIL | Nombre | Perfil esperado en el login | Notas |
|---|---|---|---|---|
| 25 | 20123456786 | Ana Maria Gimenez | CIUDADANO | Dueña de Rocky, Luna, Toby, Coty |
| 26 | 27234567891 | Lucas Fernandez | CIUDADANO | Dueño de Michi, Simon |
| 27 | 20345678906 | Marcos Diaz | REFUGIO | Responsable de los refugios 10, 11, 12 |
| 28 | 27456789019 | Carla Lopez | CIUDADANO | Dueña de Nala |
| 29 | 20567890121 | Juan Pablo Molina | VETERINARIA | Profesional activo en veterinarias 13, 14, 15 |
| 30 | 23678901237 | Victoria Suarez | — | **Deshabilitada** (`habilitado=0`): usar para probar login rechazado |
| 31 | 20789012343 | Esteban Romero | — | Profesional **dado de baja** en veterinaria 13: usar para probar RF09 rechazado |
| 32 | 27890123452 | Florencia Acosta | CIUDADANO | Dueña de Thor, Mia |

## Veterinarias

| id_veterinaria | Razón social | Habilitada | URL | Tecnología | API Key |
|---|---|---|---|---|---|
| 13 | Veterinaria Nueva Cordoba | Sí (HM-1001) | https://api.vetnuevacordoba.com.ar/v1 | REST | `vnc-9f1a2b3c4d5e6f70` |
| 14 | Clinica Veterinaria Alta Cordoba | Sí (HM-1002) | https://api.vetaltacordoba.com.ar/v1 | REST | `vac-1a2b3c4d5e6f7081` |
| 15 | Veterinaria Cerro de las Rosas | Sí (HM-1003) | https://ws.vetcerro.com.ar/soap | SOAP | `vcr-2b3c4d5e6f708192` |
| 16 | Veterinaria General Paz | **No habilitada** | https://api.vetgeneralpaz.com.ar/v1 | REST | `vgp-3c4d5e6f70819203` |

La 16 no debe aparecer en `GET /veterinarias` (RF17).

## Profesionales en veterinarias

| id_veterinaria | id_profesional | Baja |
|---|---|---|
| 13 (Nueva Cordoba) | 29 (Juan Pablo) | No |
| 14 (Alta Cordoba) | 29 (Juan Pablo) | No |
| 15 (Cerro) | 29 (Juan Pablo) | No |
| 13 (Nueva Cordoba) | 31 (Esteban) | **Sí** — usar para probar RF09 rechazado (profesional dado de baja) |

## Refugios

| id_refugio | Razón social | Habilitado | Responsable |
|---|---|---|---|
| 10 | Refugio Huellitas Cordoba | Sí (HM-2001) | 27 (Marcos) |
| 11 | Refugio Patitas sin Hogar | Sí (HM-2002) | 27 (Marcos) |
| 12 | Refugio Esperanza Animal | **No habilitado** | 27 (Marcos) |

El 12 no debe aparecer en `GET /refugios` (RF18).

## Mascotas

| nro_reg_municipal | Nombre | Microchip | Responsable | Refugio | Vive |
|---|---|---|---|---|---|
| 28 | Rocky | 900000000000001 | 25 (Ana) | — | Sí |
| 29 | Luna | — | 25 (Ana) | — | Sí |
| 30 | Toby | — | 25 (Ana) | — | **No** |
| 31 | Michi | 900000000000002 | 26 (Lucas) | — | Sí |
| 32 | Simon | — | 26 (Lucas) | — | Sí |
| 33 | Nala | 900000000000003 | 28 (Carla) | — | Sí |
| 34 | Thor | — | 32 (Florencia) | — | Sí |
| 35 | Mia | 900000000000004 | 32 (Florencia) | — | Sí |
| 36 | Bruno | — | 27 (Marcos) | 10 (Huellitas) | Sí |
| 37 | Pelusa | — | 27 (Marcos) | 10 (Huellitas) | Sí |
| 38 | Max | — | 27 (Marcos) | 11 (Patitas) | Sí |
| 39 | Coty | — | 25 (Ana) | — | Sí |

**Casos para probar duplicados (RF06):**
- Reenviar `microchip=900000000000001` → debe devolver 200 con Rocky (nro_reg_municipal=28) existente.
- Reenviar `nombre=Luna, año_nacimiento=2020, idResponsable=25` sin microchip → debe devolver 200 con Luna existente.

## Características (Especie/Raza/Pelaje)

Catálogo real sembrado por el schema (no inventado por este script):
- **Especie**: 1=Gato, 2=Perro, 3=Conejo
- **Raza**: 1=Border Collie, 2=Labrador, 3=Siamés, 4=Mestizo
- **Pelaje**: 1=Corto, 2=Largo

Cada una de las 12 mascotas tiene 3 características cargadas (Especie+Raza+Pelaje), 36 en total.

## Información sanitaria (18 atenciones)

Tipos reales: 1=Vacunación, 2=Desparasitación, 3=Esterilización, 4=Castración.

Casos relevantes para el carnet (RF10/RF11):
- **Rocky (NRM 28)**: 2 atenciones, ambas vigentes (vencen 2026).
- **Bruno (NRM 36)**: vacunación vigente + una castración (sin vencimiento).
- **Luna, Thor, Max**: tienen una atención con `fecha_vencimiento` ya pasada (2025) y otra vigente — sirve para probar que el carnet muestra ambos casos.
- **Simon, Nala, Pelusa, Coty**: una sola atención, vigente.
- **Toby**: una atención con vencimiento ya pasado (2025-05-12), y además `vive=0`.
- **Michi**: 2 atenciones el mismo día (vacunación + desparasitación).

## Publicaciones de adopción

| nro_publicacion | Mascota | Refugio | Estado |
|---|---|---|---|
| 1 | Bruno (36) | 10 (Huellitas) | **Activa** |
| 2 | Pelusa (37) | 10 (Huellitas) | **Activa** |
| 3 | Max (38) | 11 (Patitas) | Pausada |
| 4 | Toby (30) | 10 (Huellitas) | Finalizada |

**Caso de error (RF13, 409):** intentar `PUT /publicaciones/{id}` para pasar la publicación 3 (Max) a `Activa` sin antes pausar la 1 o la 2, o crear una publicación nueva para Bruno (36) mientras la 1 sigue Activa → debe fallar con 409.

## Datos que NO se pudieron cargar tal como pedía el prompt original

- **El prompt original pedía "6 mascotas con microchip y 6 sin"**: la tabla `mascotas.microchip` tenía un `UNIQUE` constraint estándar que en SQL Server **solo permite un único NULL en toda la tabla** (no uno por fila como en otras bases). Tuve que reemplazar `UQ_mascotas_microchip` por un índice único filtrado (`CREATE UNIQUE INDEX ... WHERE microchip IS NOT NULL`) para poder tener varias mascotas sin microchip. Esto es una corrección real al script de schema, no solo a los datos de prueba — avisar si se vuelve a crear la base desde cero con el script original, hay que aplicar este mismo fix.
- Los 2 stored procedures (`sp_InsertarInformacionSanitaria`, `sp_InsertarCaracteristicaMascota`) se habían creado originalmente con `QUOTED_IDENTIFIER OFF` (porque el cliente SQL usado no tenía ese SET activo al crearlos), lo cual rompe cualquier `UPDATE`/`INSERT` sobre `mascotas` o `publicaciones_adopcion` por los índices filtrados que tienen esas tablas. Se recrearon con `SET QUOTED_IDENTIFIER ON` antes del `CREATE PROCEDURE`.
- Los textos con tilde de los catálogos semilla (`tipos_atencion_sanitaria`, `atributos_sistema`, `dominio_rasgos_mascotas.Siamés`) habían quedado corruptos (doble-codificados) en una corrida previa del script de schema sin el flag `-f 65001` de `sqlcmd`. Se corrigieron con `UPDATE` directo antes de cargar los datos de prueba.
