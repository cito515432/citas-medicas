# Dockerización — Global ISO Security

Esta versión deja el proyecto listo para ejecutarse con Docker Compose usando tres servicios:

- `mysql`: base de datos MySQL 8.
- `backend`: API Java Spring Boot en el puerto interno 8081.
- `frontend`: Nginx sirviendo HTML/CSS/JS y redirigiendo `/api` al backend.

## 1. Archivos agregados

```text
docker-compose.yml
.env.example
backend/Dockerfile
backend/Dockerfile.jar
backend/.dockerignore
frontend/Dockerfile
frontend/nginx.conf
frontend/.dockerignore
database/init/README_IMPORTANTE.txt
database/init/01_globalisosecurity_backup.sql
README_DOCKER.md
```

Además, los JavaScript del frontend fueron ajustados para consumir la API con ruta relativa:

```js
const API_BASE_URL = "/api";
const API_URL = "/api";
```

Esto evita depender de Railway o `localhost:8081` cuando el sistema se ejecuta en Docker.

## 2. Ejecutar localmente

Desde la raíz del proyecto:

```bash
copy .env.example .env
```

En Linux/Mac:

```bash
cp .env.example .env
```

Luego:

```bash
docker compose up -d --build
```

Abrir en el navegador:

```text
http://localhost:8080
```

La raíz redirige automáticamente a:

```text
http://localhost:8080/pages/login.html
```

## 3. Ver logs

```bash
docker compose logs -f backend
```

```bash
docker compose logs -f frontend
```

```bash
docker compose logs -f mysql
```

## 4. Entrar a MySQL del contenedor

```bash
docker exec -it globaliso_mysql mysql -uroot -p globalisosecurity
```

La contraseña por defecto está en `.env`:

```text
123456789
```

## 5. Importar base de datos completa

Esta versión ya incluye el respaldo completo que enviaste:

```text
database/init/01_globalisosecurity_backup.sql
```

Ese archivo se importa automáticamente en MySQL la primera vez que se crea el volumen `mysql_data`.

Ejecuta normalmente:

```bash
docker compose up -d --build
```

Si ya habías levantado MySQL y quieres reiniciar desde cero:

```bash
docker compose down -v
docker compose up -d --build
```

Cuidado: `down -v` borra la base del contenedor.

## 6. Subir al servidor de la universidad

En el servidor deben estar instalados Docker, Docker Compose y Git.

```bash
git clone https://github.com/cito515432/global-iso-security.git
cd global-iso-security
cp .env.example .env
nano .env
```

Luego:

```bash
docker compose up -d --build
```

Si la universidad permite usar el puerto 80, cambia en `.env`:

```env
FRONTEND_PORT=80
```

Después abre:

```text
http://IP_DEL_SERVIDOR
```

## 7. Comandos útiles

Ver contenedores:

```bash
docker compose ps
```

Apagar sin borrar datos:

```bash
docker compose down
```

Apagar borrando base de datos:

```bash
docker compose down -v
```

Reconstruir:

```bash
docker compose up -d --build
```

## 8. Alternativa si falla la compilación Maven dentro de Docker

El archivo `backend/Dockerfile` compila desde código fuente. Si necesitas usar el `.jar` ya compilado, conserva `backend/target/backend-0.0.1-SNAPSHOT.jar` y cambia en `docker-compose.yml`:

```yaml
backend:
  build:
    context: ./backend
    dockerfile: Dockerfile.jar
```

La opción recomendada sigue siendo compilar desde código fuente con `backend/Dockerfile`.
