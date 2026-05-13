Aquí ya quedó incluido el respaldo completo:

01_globalisosecurity_backup.sql

Importante:
- MySQL solo ejecuta estos scripts la primera vez que se crea el volumen mysql_data.
- Si ya levantaste Docker antes y quieres reimportar desde cero, ejecuta:

  docker compose down -v
  docker compose up -d --build

Cuidado: docker compose down -v borra la base de datos del contenedor.
