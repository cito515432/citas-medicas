# Global ISO Security - Despliegue en Render sin método de pago

Esta versión evita crear MySQL dentro de Render, porque MySQL con disco persistente requiere servicio pago o método de pago.

## Arquitectura usada

- Render Free: backend Spring Boot
- Render Free: frontend Nginx
- Base de datos externa: MySQL de Railway u otra MySQL remota

## Paso 1: subir a GitHub

```bash
git add .
git commit -m "chore: ajusta despliegue gratis en Render"
git push origin main
```

## Paso 2: tener los datos de MySQL externa

Si usas Railway MySQL, copia desde Railway las credenciales públicas:

- Host
- Port
- Database
- User
- Password

La URL JDBC queda así:

```text
jdbc:mysql://HOST:PORT/NOMBRE_BASE?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Ejemplo:

```text
jdbc:mysql://mysql.railway.internal:3306/globalisosecurity?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Ojo: desde Render normalmente debes usar el host público de Railway, no el host interno de Railway.

## Paso 3: crear Blueprint en Render

En Render:

New > Blueprint > seleccionar repositorio > Apply

El archivo render.yaml crea dos servicios gratis:

- globaliso-backend-cito515432
- globaliso-frontend-cito515432

## Paso 4: llenar variables del backend

Cuando Render pida variables, coloca:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://HOST_PUBLICO:PUERTO/globalisosecurity?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=TU_USUARIO_MYSQL
SPRING_DATASOURCE_PASSWORD=TU_PASSWORD_MYSQL
```

Render genera automáticamente JWT_SECRET.

## Paso 5: abrir frontend

Cuando termine el deploy, abre:

```text
https://globaliso-frontend-cito515432.onrender.com
```

Si Render cambia el nombre de la URL del backend, actualiza en el servicio frontend la variable:

```text
BACKEND_URL=https://URL_REAL_DEL_BACKEND.onrender.com
```

y redeploya el frontend.

## Nota importante sobre Render Free

Los servicios Free pueden dormir después de estar inactivos. La primera carga puede tardar cerca de un minuto.
