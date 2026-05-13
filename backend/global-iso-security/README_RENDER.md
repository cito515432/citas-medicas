# Global ISO Security — despliegue en Render

Este paquete está preparado para subirse a GitHub y desplegarse en Render usando Docker.

## Qué incluye

- `backend/Dockerfile.render`: backend Spring Boot listo para Render.
- `frontend/Dockerfile.render`: frontend Nginx listo para Render.
- `render/mysql/Dockerfile`: MySQL 8 como Private Service de Render.
- `render.yaml`: Blueprint para crear los tres servicios en Render.
- `database/init/01_globalisosecurity_backup.sql`: respaldo inicial de la base de datos.
- `docker-compose.yml`: alternativa para probar localmente.

## Antes de subir a GitHub

1. Descomprime el proyecto.
2. Verifica que NO exista un archivo `.env` con contraseñas reales.
3. Si el SQL tiene datos sensibles, usa un repositorio privado.
4. Sube la carpeta completa a GitHub.

Comandos sugeridos:

```bash
git init
git branch -M main
git add .
git commit -m "chore: prepara despliegue docker en render"
git remote add origin https://github.com/TU_USUARIO/global-iso-security.git
git push -u origin main
```

## Despliegue en Render con Blueprint

1. En Render, entra a **New > Blueprint**.
2. Conecta el repositorio de GitHub.
3. Render detectará el archivo `render.yaml`.
4. Confirma la creación de los servicios:
   - `globaliso-mysql` como Private Service.
   - `globaliso-backend` como Web Service.
   - `globaliso-frontend` como Web Service.
5. Espera a que los tres servicios terminen el deploy.
6. Abre la URL pública del frontend:

```text
https://globaliso-frontend.onrender.com
```

## Cómo funciona la conexión

El navegador entra al frontend. El frontend llama rutas relativas `/api/...`. Nginx recibe esas llamadas y las redirige internamente al backend usando la red privada de Render.

```text
Navegador -> globaliso-frontend -> /api -> globaliso-backend -> globaliso-mysql
```

## Importante sobre MySQL y el SQL

El respaldo `database/init/01_globalisosecurity_backup.sql` se importa automáticamente solo cuando el disco persistente de MySQL está vacío.

Si el servicio MySQL ya fue creado una vez, cambiar el SQL no lo vuelve a importar automáticamente. En ese caso debes importar manualmente o recrear el servicio/disco.

## Probar localmente antes de Render

```bash
copy .env.example .env
# en Linux/Mac sería: cp .env.example .env

docker compose up -d --build
```

Abrir:

```text
http://localhost:8080
```

## Rutas útiles

Backend health:

```text
/health
```

Login frontend:

```text
/pages/login.html
```

## Nota sobre costos

El Blueprint usa servicios Docker y un disco persistente para MySQL. Revisa el plan en Render antes de confirmar el despliegue, porque MySQL con disco persistente puede requerir un plan de pago.
