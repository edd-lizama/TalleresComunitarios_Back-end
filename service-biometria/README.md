# service-biometria (Maven)

Microservicio de registros biométricos (IMC). Tabla propia: `registro_biometrico`.
Estructura por capas igual al servicio de autenticación: `config`, `controller`, `dto`, `exception`, `model`, `repository`, `security`, `service`, `util`.

## Endpoints (requieren `Authorization: Bearer <token>` emitido por service-auth-usuarios)
| Método | Ruta | Quién | Descripción |
|---|---|---|---|
| POST | `/api/biometria` | cualquier usuario | Registra una medición propia. Body: `{ "fechaMedicion": "2026-11-18" (opcional), "altura": 168.5, "peso": 70.2 }`. Altura en cm, peso en kg. Responde `201` con el IMC calculado. |
| GET | `/api/biometria/mis-registros` | cualquier usuario | Historial propio (más reciente primero). |
| GET | `/api/biometria/perfil/{idPerfil}` | MONITOR, ADMIN | Historial de un perfil. |
| DELETE | `/api/biometria/{id}` | dueño o ADMIN | Elimina una medición (`204`). |

Respuesta de una medición (camelCase):
`{ "id", "idPerfil", "fechaMedicion", "altura", "peso", "indiceMasaCorporal" }`

Errores: `{ "mensaje": "..." }` con 400 (datos inválidos), 401 (sin token válido), 403, 404.

## Cómo ejecutarlo
1. La base debe tener `perfil` (servicio de autenticación). Ejecuta `db/schema.sql`.
2. Define `SPRING_DATASOURCE_PASSWORD` y `JWT_SECRET` (**el mismo secreto que el servicio de autenticación**). Ver `.env.example` y `.vscode/launch.json.example`.
3. `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). Puerto por defecto: 8084.
4. `./mvnw clean package` compila y corre las pruebas (no necesitan base de datos).

Requiere JDK 21.

## Decisiones asumidas (confirmar con el equipo)
- **Unidades:** altura en centímetros, peso en kilogramos (el MER no las definía).
- **IMC sin clasificación:** el servicio devuelve solo el número. Las categorías cambian para niños (percentiles) y adultos mayores, y el programa incluye ambos grupos.
- **Perfil del usuario:** el token trae `idUsuario` y el MER cuelga el registro de `PERFIL`. El servicio lee `perfil` (solo lectura) para resolverlo, lo que supone la misma base de datos. Al registrar usa el primer perfil del usuario.
- **Acceso de monitores:** hoy cualquier MONITOR puede ver el historial de cualquier perfil. Lo ideal es limitarlo a los alumnos de sus talleres, lo que requiere datos del servicio de talleres (Dev 2).
- El frontend aún no tiene pantalla/contrato para IMC: estos endpoints son una propuesta para acordar.
