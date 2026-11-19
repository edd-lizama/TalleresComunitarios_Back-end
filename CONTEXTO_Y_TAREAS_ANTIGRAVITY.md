# Contexto Maestro del Proyecto "Talleres PH" y Plan de Ejecución Backend

**Dirigido a:** Agente IA Antigravity (Workspace: `TalleresComunitarios_Back-end`)  
**Sistema Operativo del Entorno:** Windows (usar `.\mvnw.cmd` para Maven y `.\gradlew.bat` para Gradle)  
**Proyecto:** Plataforma Móvil de Gestión de Talleres Comunitarios – Municipalidad de Padre Hurtado  

---

## 1. Resumen Ejecutivo del Estado Actual

1. **Frontend Móvil (Ionic + Angular):** Está **100% finalizado** a nivel visual, de navegación y de persistencia local (`IonicStorage` en modo *Offline-First*). Actualmente opera con datos simulados en `DatabaseService` y está listo para conectarse a las APIs REST del backend.
2. **Backend (Microservicios Spring Boot + PostgreSQL):** El equipo de desarrollo completó 4 microservicios que conviven en este mismo workspace (`TalleresComunitarios_Back-end`), pero **fueron desarrollados por dos personas distintas (Dev 1 y Dev 2) con diferencias estructurales críticas** que impedirán el arranque de Spring Boot (`ddl-auto: validate`) y romperán la integración con el frontend si no se unifican antes.
3. **Compilación Pendiente:** Los dos servicios de Dev 1 (`service-auth-usuarios` y `service-biometria`) fueron escritos sin compilarse aún en un entorno con JDK/Maven. Deben compilarse y validarse en este workspace.

---

## 2. Mapa del Workspace Actual (`TalleresComunitarios_Back-end`)

En este directorio raíz existen **4 microservicios** que comparten una única base de datos PostgreSQL llamada `talleres_ph_db`:

| Carpeta en Workspace | Build Tool | Puerto | Stack / Versión | Paquete Raíz | Responsabilidad |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `service-auth-usuarios` | Maven (`mvnw.cmd`) | `8083` | Java 21, Spring Boot `4.1.2`, Spring Security 7, JJWT `0.12.5` | `com.talleresph.service_auth_usuarios` | Login, registro, roles, perfiles, actualización de usuario y foto de perfil. |
| `service-biometria` | Maven (`mvnw.cmd`) | `8084` | Java 21, Spring Boot `4.1.2`, Spring Security 7, JJWT `0.12.5` | `com.talleresph.service_biometria` | Cálculo y registro de IMC (`registro_biometrico`). |
| `service-catalogo-sucursales` | Gradle (`gradlew.bat`) | `8081` | Java 21, Spring Boot `3.2.5`, Spring Security 6, JJWT `0.12.6` | `com.municipalidad.talleresph.service_catalogo_sucursales` | Catálogo público de talleres, detalles, ubicación y cupos. |
| `service-inscripciones-gestion` | Gradle (`gradlew.bat`) | `8082` | Java 21, Spring Boot `3.2.5`, Spring Security 6, JJWT `0.12.6` | `com.municipalidad.talleresph.service_inscripciones_gestion` | Inscripciones con bloqueo pesimista, toma de asistencia, foto evidencia y sincronización offline (`/api/sync/lote`). |

---

## 3. Especificación Completa del Frontend (Contrato Exacto que Espera la App)

El frontend en Ionic/Angular (componentes clásicos con `NgModule`/imports de `@ionic/angular` y `ChangeDetectorRef`) implementa dos roles y una arquitectura **Offline-First**:

### 3.1 Roles de Usuario
* **`VECINO` (Ciudadano):** Explora talleres en lista y mapa (`lat`, `lng`), revisa detalle, se inscribe o anula inscripción, y gestiona su perfil.
* **`MONITOR` (Profesor / Profesional de Salud):** Accede a una interfaz independiente (`/profesor/tabs`) con:
  * **Mis Talleres (`/profesor/mis-talleres`):** Lista los talleres asignados a ese monitor, mostrando nombre, categoría, horario largo (`09:00 – 10:30`), lugar (`Sede Social Los Aromos`), badge dinámico de cupos disponibles (alerta roja si `cuposDisponibles <= 1`) y botón *"Tomar Asistencia"*.
  * **Toma de Asistencia (`/profesor/asistencia/:id`):**
    * Muestra contadores en vivo: **Asisten**, **No Asisten**, **Pendientes**.
    * Lista los vecinos inscritos con iniciales, edad (`67 años`), género (`Femenino`/`Masculino`), botones `asiste` / `no_asiste` / `pendiente`, y modal para registrar `justificacion` en texto.
    * Permite capturar una **fotografía de evidencia de la clase** (`capture="environment"`) en formato DataURL Base64 (`data:image/jpeg;base64,...`).
    * Botón flotante *"Guardar Asistencia"* que guarda en backend o encola offline.
  * **Mi Perfil (`/profesor/perfil`):** Muestra nombre completo, `cargo` (`Enfermera Comunitaria`), `centroSalud` (`CESFAM Padre Hurtado`), métricas (`estadisticas: { talleres, registros, esteMes }`), permite cambiar `fotoPerfil` tocando el avatar (Base64) y cerrar sesión.

### 3.2 Arquitectura Offline-First y Cola de Sincronización
* El frontend escucha `window.addEventListener('online' | 'offline')` y muestra un indicador circular verde (`online`) o rojo (`offline`) en la cabecera de todas las pantallas del monitor.
* Cuando `estaOnline === false`, las acciones se guardan en `IonicStorage` bajo la llave `cola_sincronizacion` con la estructura:
  ```json
  {
    "id": 1790172000000,
    "tipo": "GUARDAR_ASISTENCIA | ACTUALIZAR_PERFIL | INSCRIPCION | ANULAR_INSCRIPCION",
    "fecha": "2026-09-23T10:25:00.000Z",
    "payload": { ... }
  }
  ```
* Al volver la conexión (`online`), el frontend envía el lote a `POST /api/sync/lote`.

### 3.3 Contratos JSON (`camelCase`) que Consume el Frontend

1. **Login / Registro (`POST /api/auth/login` y `POST /api/auth/registro`):**
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "usuario": {
       "id": 2,
       "rut": "15.432.198-7",
       "nombre": "Claudia",
       "apellido": "Peñaloza Vera",
       "correo": "claudia.penaloza@saludmph.cl",
       "rol": "MONITOR",
       "edad": 38,
       "genero": "Femenino",
       "telefono": "+56912345678",
       "direccion": "Padre Hurtado",
       "cargo": "Enfermera Comunitaria",
       "centroSalud": "CESFAM Padre Hurtado",
       "fotoPerfil": null,
       "estadisticas": { "talleres": 14, "registros": 312, "esteMes": 38 }
     }
   }
   ```
2. **Objeto Taller (`GET /api/talleres` y `GET /api/monitor/mis-talleres`):**
   ```json
   {
     "id": 1,
     "nombre": "Yoga Terapéutico",
     "categoria": "Salud Mental y Bienestar",
     "descripcion": "Clases orientadas a mejorar la flexibilidad...",
     "imagen": "https://...",
     "lat": -33.5725,
     "lng": -70.8150,
     "lugar": "Sede Social Los Aromos",
     "dias": "Lunes, Miércoles y Viernes",
     "horario": "Lun/Mié/Vie 09:00",
     "horarioLargo": "09:00 – 10:30",
     "cuposDisponibles": 3,
     "profesor": "Claudia Peñaloza Vera",
     "monitorId": 2
   }
   ```
3. **Nómina de Asistencia del Día (`GET /api/monitor/talleres/{tallerId}/asistencia?fecha=YYYY-MM-DD`):**
   ```json
   {
     "tallerId": 1,
     "fecha": "2026-09-23",
     "fotoEvidencia": null,
     "alumnos": [
       {
         "id": 101,
         "nombre": "María González Rojas",
         "edad": 67,
         "genero": "Femenino",
         "estado": "asiste",
         "justificacion": ""
       }
     ]
   }
   ```
4. **Guardar Asistencia (`POST /api/monitor/talleres/asistencia`):**
   ```json
   {
     "tallerId": 1,
     "fecha": "2026-09-23",
     "fotoEvidencia": "data:image/jpeg;base64,...",
     "alumnos": [
       { "id": 101, "estado": "asiste", "justificacion": "" },
       { "id": 104, "estado": "no_asiste", "justificacion": "Compromiso familiar" }
     ]
   }
   ```

---

## 4. Auditoría de Conflictos Detectados entre Dev 1 y Dev 2

Al cruzar los informes técnicos de ambos desarrolladores, se detectaron **6 incompatibilidades graves** que deben corregirse en este workspace:

### Conflicto 1: Colisión en la Tabla `usuario` (Crash en `ddl-auto: validate`)
* **Dev 1 (`service-auth-usuarios`):** Mapea la tabla `usuario` con PK `id_usuario` (`BIGINT`), columna `correo`, y campos `id_rol`, `rut`, `nombre`, `apellido`, `edad`, `genero`, `cargo`, `centro_salud`, `activo`, `es_de_la_comuna`, `telefono`, `direccion`, `password`, `foto_perfil` (`TEXT`).
* **Dev 2 (`service-catalogo-sucursales` y `service-inscripciones-gestion`):** Mapea la misma tabla `usuario` pero con PK `id`, columna `email`, y **sin** `direccion`, `foto_perfil`, `edad`, `genero`, `cargo`, `centro_salud`, `activo`.
* **Impacto:** Ambos proyectos tienen `spring.jpa.hibernate.ddl-auto: validate` apuntando a `talleres_ph_db`. Uno de los dos fallará al arrancar. Además, `SyncService` en `service-inscripciones-gestion` intenta ejecutar `ACTUALIZAR_PERFIL`, pero su entidad `Usuario` no tiene `foto_perfil` ni `direccion`.

### Conflicto 2: Columnas Faltantes en la Tabla `taller` (Dev 2)
* Dev 2 redujo la entidad `Taller` a: `id`, `nombre`, `descripcion`, `cupos_totales`, `cupos_disponibles`, `estado`, `monitor_id`.
* **Faltan obligatoriamente:** `categoria`, `imagen` (`TEXT`), `lat` (`DOUBLE PRECISION`), `lng` (`DOUBLE PRECISION`), `lugar`, `dias`, `horario`, `horario_largo`, `activo` (`BOOLEAN`). Sin estas columnas, ni las tarjetas de `MisTalleresPage` ni el mapa del vecino pueden renderizarse.

### Conflicto 3: Discrepancia de Nombres en `detalle_asistencia` (`observacion` vs `justificacion`)
* En PostgreSQL, Dev 2 nombró la columna `observacion` en `detalle_asistencia`. Debe asegurarse que en los DTOs JSON (`AlumnoAsistenciaItemDTO` y `AlumnoAsistenciaResponseDTO`) la propiedad se serialice como `justificacion` (o usar `@JsonAlias({"observacion", "justificacion"})` y `@JsonProperty("justificacion")`) para que el frontend la lea y envíe sin cambios.

### Conflicto 4: CORS Incompleto en Servicios de Dev 2 (`8081` y `8082`)
* Dev 1 autorizó `http://localhost:8100`, `capacitor://localhost` y `http://localhost`.
* Dev 2 autorizó `capacitor://localhost` y `http://localhost`, pero **omitió `http://localhost:8100`** (el puerto por defecto de `ionic serve`). Esto bloqueará todas las peticiones HTTP en desarrollo web.

### Conflicto 5: Secreto JWT Expuesto en Texto Plano y Validación de Claims
* Dev 2 dejó el secreto JWT hardcodeado en `application.yml` (`404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971`), mientras Dev 1 usa `${JWT_SECRET}`.
* Dev 1 firma el token con `subject = correo` y los claims: `idUsuario` (`Long`), `rut` (`String`), `rol` (`String`). Los filtros `JwtAuthenticationFilter` de Dev 2 deben leer exactamente esos claims (`idUsuario`, `rol`, `subject`) usando la misma variable de entorno `JWT_SECRET`.

### Conflicto 6: Endpoints Faltantes para el Monitor en Dev 2
* Falta `GET /api/monitor/mis-talleres` (o `GET /api/monitor/{monitorId}/talleres`) para listar solo los talleres donde `monitor_id` corresponde al profesor autenticado.
* Falta `GET /api/monitor/estadisticas` para devolver `{ "talleres": int, "registros": int, "esteMes": int }` en la pantalla de perfil del monitor (actualmente Dev 1 devuelve `estadisticas: null` en el login porque esos datos viven en las tablas de Dev 2).

---

## 5. Instrucciones Paso a Paso para Antigravity (Tareas a Ejecutar)

Ejecuta las siguientes acciones de manera ordenada sobre los archivos de este workspace:

### Tarea 1: Unificar Entidades `Usuario` y `Taller` en `service-catalogo-sucursales` y `service-inscripciones-gestion`
1. **En `service-catalogo-sucursales` y `service-inscripciones-gestion`**, actualiza la entidad `Usuario.java` para que coincida exactamente con la tabla de `service-auth-usuarios`:
   * `@Table(name = "usuario")`
   * `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_usuario") private Long id;`
   * `@Column(name = "correo", unique = true, nullable = false) private String correo;`
   * Mapear también: `rut`, `nombre`, `apellido`, `telefono`, `direccion`, `edad` (`Integer`), `genero`, `cargo`, `centroSalud` (`@Column(name = "centro_salud")`), `fotoPerfil` (`@Column(name = "foto_perfil", columnDefinition = "TEXT")`), `activo` (`Boolean`).
   * Actualizar `UsuarioRepository` en ambos servicios para buscar por `findByCorreo(String correo)` en lugar de `findByEmail`.
2. **En `service-catalogo-sucursales` y `service-inscripciones-gestion`**, actualiza la entidad `Taller.java` y `TallerResponseDTO.java`:
   * Agregar columnas: `categoria`, `imagen` (`TEXT`), `lat` (`Double`), `lng` (`Double`), `lugar`, `dias`, `horario`, `horarioLargo` (`@Column(name = "horario_largo")`), `activo` (`Boolean`).
   * En `TallerResponseDTO`, incluir todos estos campos en `camelCase` más `profesor` (concatenando `monitor.getNombre() + " " + monitor.getApellido()`) y `monitorId`.

### Tarea 2: Revisar `AsistenciaService` y `SyncService` en `service-inscripciones-gestion`
1. Verifica que `AlumnoAsistenciaItemDTO` y `AlumnoAsistenciaResponseDTO` expongan el campo `justificacion` (mapeado hacia/desde `detalle_asistencia.observacion`).
2. Verifica que `SyncService` al recibir la operación `ACTUALIZAR_PERFIL` actualice correctamente `nombre`, `apellido`, `telefono`, `direccion` y `fotoPerfil` en la entidad unificada `Usuario`.
3. Verifica que `SyncService` al recibir `GUARDAR_ASISTENCIA` acepte tanto la llave `alumnos` como `alumnosActualizados` dentro del `payload` (ya que la cola offline del frontend envía `alumnosActualizados`), extraiga la fecha `YYYY-MM-DD` desde `operacion.fecha`, y persista tanto la asistencia como `fotoEvidencia`.

### Tarea 3: Agregar Endpoints de Monitor Faltantes (`service-catalogo-sucursales` / `service-inscripciones-gestion`)
1. En `service-catalogo-sucursales` (o `service-inscripciones-gestion`), agregar `GET /api/monitor/mis-talleres` protegido para rol `MONITOR` / `ADMIN`, que extraiga el `idUsuario` del token JWT y retorne `List<TallerResponseDTO>` filtrados por `monitor_id = idUsuario` y `activo = true`.
2. En `service-inscripciones-gestion`, agregar `GET /api/monitor/estadisticas` protegido para rol `MONITOR` / `ADMIN`, que extraiga `idUsuario` del token JWT y calcule con consultas reales:
   * `talleres`: cantidad de talleres activos asignados a ese `monitor_id`.
   * `registros`: cantidad total de registros en `detalle_asistencia` con estado `'asiste'` o `'no_asiste'` en sesiones de ese monitor.
   * `esteMes`: misma cuenta filtrada por el mes y año actual.

### Tarea 4: Homologar Seguridad, JWT y CORS en los 4 Microservicios
1. En `service-catalogo-sucursales` y `service-inscripciones-gestion`, actualiza `CorsConfig.java` para incluir `"http://localhost:8100"` en `allowedOrigins` junto a `"http://localhost:4200"`, `"capacitor://localhost"` y `"http://localhost"`.
2. En `service-catalogo-sucursales` y `service-inscripciones-gestion`, actualiza `application.yml` para que `jwt.secret` lea `${JWT_SECRET}` (eliminando cualquier secreto hardcodeado en el archivo) y verifica que `JwtUtil` / `JwtAuthenticationFilter` extraigan `idUsuario` (`Long`), `rol` (`String`) y `subject` (`correo`) igual que `service-auth-usuarios`.
3. Asegúrate de que todos los servicios soporten payloads JSON grandes (hasta `10MB`) para recibir `fotoPerfil` y `fotoEvidencia` en Base64 sin errores de límite de tamaño en Tomcat/Jackson.

### Tarea 5: Generar Script SQL Maestro Unificado (`db/schema_unificado.sql`)
Crea un archivo `db/schema_unificado.sql` en la raíz del workspace (`TalleresComunitarios_Back-end`) que contenga:
1. Las tablas completas de Dev 1 (`rol`, `permiso`, `rol_permiso`, `usuario`, `perfil`, `registro_biometrico`) y las tablas completas y corregidas de Dev 2 (`taller`, `inscripcion`, `sesion_asistencia`, `detalle_asistencia`) con todos los tipos `BIGINT`, `TEXT`, `BOOLEAN`, `DOUBLE PRECISION` y restricciones `UNIQUE` exactas para que `ddl-auto: validate` pase en los **4 microservicios** simultáneamente.
2. Datos semilla (`INSERT ... ON CONFLICT DO NOTHING`) con:
   * Roles: `VECINO`, `MONITOR`, `ADMIN`.
   * Usuario Vecino demo: `vecino@padrehurtado.cl` (password `123` en BCrypt, RUT `12345678-9`, Cristian López).
   * Usuario Monitor demo: `claudia.penaloza@saludmph.cl` (password `123` en BCrypt, RUT `15.432.198-7`, Claudia Peñaloza Vera, cargo `Enfermera Comunitaria`, centro_salud `CESFAM Padre Hurtado`).
   * Los 4 talleres base asignados a Claudia (`Yoga Terapéutico`, `Zumba para Adultos Mayores`, `Control de Hipertensión`, `Psicomotricidad Infantil`) con sus horarios, coordenadas, lugares y cupos.
   * Alumnos inscritos de prueba en el Taller 1 (`Yoga Terapéutico`).

### Tarea 6: Compilación y Verificación de los 4 Proyectos
Ejecuta la compilación en cada carpeta desde la terminal de Windows para garantizar que **cero errores de sintaxis, imports o tipos** queden pendientes:
1. En `service-auth-usuarios`: `.\mvnw.cmd clean compile`
2. En `service-biometria`: `.\mvnw.cmd clean compile`
3. En `service-catalogo-sucursales`: `.\gradlew.bat compileJava`
4. En `service-inscripciones-gestion`: `.\gradlew.bat compileJava`

Al finalizar, entrega un resumen claro con los archivos modificados, los endpoints finales disponibles por puerto (`8081`, `8082`, `8083`, `8084`) y las variables de entorno necesarias para levantar los 4 servicios.