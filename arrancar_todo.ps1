
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/talleres_ph_db"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "Monoloko.12"

# 2. Llave secreta compartida para firmar y leer los Tokens JWT
$env:JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd service-auth-usuarios; .\mvnw.cmd spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd service-catalogo-sucursales; .\gradlew.bat bootRun"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd service-inscripciones-gestion; .\gradlew.bat bootRun"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd service-biometria; .\mvnw.cmd spring-boot:run"