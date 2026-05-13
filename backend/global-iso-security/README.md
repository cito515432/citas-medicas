# 🔐 Global ISO Security

Herramienta web para **auditoría, implementación y capacitación de ISO 27001**, desarrollada como proyecto académico para la asignatura de **Ingeniería Web**.

---

## 📌 Descripción general

**Global ISO Security** es una aplicación web orientada a apoyar la gestión de procesos relacionados con la norma **ISO 27001**, permitiendo administrar múltiples empresas, usuarios por rol, servicios, checklists, evaluaciones, firmas, capacitaciones, trazabilidad y reportes.

El sistema fue diseñado para centralizar la información del proceso, mejorar la evidencia documental y facilitar el seguimiento del cumplimiento mediante una arquitectura web cliente-servidor con autenticación JWT y persistencia en MySQL.

---

## 👥 Equipo de desarrollo

| Integrante | Rol principal |
|---|---|
| **Andrés Felipe Obando Barriga** | Backend |
| **María Camila Sarmiento** | Frontend |
| **Juan Esteban Pardo Bedoya** | Base de datos |

**Universidad San Buenaventura – Sede Bogotá**  
**Programa:** Ingeniería de Sistemas  
**Asignatura:** Ingeniería Web  
**Docente:** Jairo Armando Salcedo Aranda

---

## 🛠️ Stack tecnológico

### Backend
- Java 20
- Spring Boot 3.2.0
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- BCrypt
- Maven

### Frontend
- HTML5
- CSS3
- Bootstrap 5
- JavaScript vanilla

### Base de datos
- MySQL 8
- XAMPP / phpMyAdmin

### Herramientas de apoyo
- NetBeans o IntelliJ IDEA
- VS Code
- Postman
- Git + GitHub
- Python (`python -m http.server 5500` para servir frontend local)

---

## 🧱 Arquitectura del proyecto

```text
global-iso-security/
│
├── backend/
│   └── src/main/java/com/globalisosecurity/backend/
│       ├── config/         # Seguridad, CORS y configuración general
│       ├── controllers/    # Endpoints REST
│       ├── dto/            # DTOs para contratos con frontend
│       ├── exceptions/     # Manejo global de errores
│       ├── models/         # Entidades JPA
│       ├── repositories/   # Acceso a datos
│       ├── services/       # Lógica de negocio
│       └── utils/          # JWT, seguridad y utilidades
│
├── frontend/
│   ├── pages/              # Vistas HTML por rol
│   ├── css/                # Estilos
│   ├── js/                 # Lógica JS de frontend
│   └── assets/             # Recursos estáticos
│
└── database/
    ├── schema.sql          # Scripts base
    └── backups/            # Respaldos SQL si se incluyen
🔐 Roles del sistema
Rol	Tipo	Funciones principales
ADMINISTRADOR	Interno	Gestionar usuarios, empresas, servicios y reportes
IMPLEMENTADOR	Interno	Diligenciar checklist, registrar evaluaciones y observaciones
AUDITOR	Interno	Validar resultados, registrar auditoría y firmas
CAPACITADOR	Interno	Gestionar capacitaciones, materiales y constancias
USUARIO EMPRESA	Externo	Consultar estado del servicio, firmas y reportes
✅ Funcionalidades implementadas
Autenticación y seguridad
Login con JWT
Protección de rutas con Bearer Token
Spring Security configurado
Passwords hasheadas con BCrypt
CORS global habilitado para integración frontend-backend
Gestión de usuarios
Listar usuarios
Crear usuario con DTO
Actualizar usuario
Eliminar usuario
Normalización de email
Validaciones de negocio
Gestión de roles
Consulta de roles para poblar selects del frontend
Gestión de empresas
CRUD de empresas
Consulta de empresas asignadas al usuario autenticado
Gestión de sectores
CRUD de sectores
Gestión de servicios
CRUD de servicios
Asociación con empresa y sector
Control de estados del servicio
Restricción de edición/eliminación en estados finales
Checklists
CRUD de checklist
CRUD de ítems del checklist
Endpoint combinado para obtener checklist + ítems por servicio
Evaluaciones
Persistencia de evaluaciones por ítem/control
Asociación a servicio, ítem y usuario
Validación de observación obligatoria para ciertos estados
Consultas por servicio y por empresa
Firmas
CRUD funcional
Consulta por servicio y por empresa
Capacitaciones
CRUD funcional
Asociación con servicio
Material, video, estado y fecha de finalización
Consulta por servicio y empresa
Dashboard
Resumen global del sistema
Resumen por empresa
Reportes
Reporte JSON por empresa
Exportación en PDF
Exportación en Excel
Trazabilidad
Logs de auditoría
Registro de usuario autenticado real
Registro de IP real del request
📡 Endpoints principales
Autenticación
POST /api/auth/login
Usuarios
GET /api/usuarios
POST /api/usuarios
PUT /api/usuarios/{id}
DELETE /api/usuarios/{id}
Roles
GET /api/roles
Empresas
GET /api/empresas
GET /api/empresas/asignadas
POST /api/empresas
PUT /api/empresas/{id}
DELETE /api/empresas/{id}
Sectores
GET /api/sectores
POST /api/sectores
PUT /api/sectores/{id}
DELETE /api/sectores/{id}
Servicios
GET /api/servicios
GET /api/servicios/empresa/{empresaId}
POST /api/servicios
PUT /api/servicios/{id}
DELETE /api/servicios/{id}
Checklists
GET /api/checklists
POST /api/checklists
PUT /api/checklists/{id}
DELETE /api/checklists/{id}
GET /api/checklists/servicio/{servicioId}/completo
Ítems del checklist
GET /api/items-checklist
POST /api/items-checklist
PUT /api/items-checklist/{id}
DELETE /api/items-checklist/{id}
Evaluaciones
POST /api/evaluaciones
GET /api/evaluaciones/empresa/{empresaId}
Firmas
POST /api/firmas
GET /api/firmas/empresa/{empresaId}
Capacitaciones
POST /api/capacitaciones
GET /api/capacitaciones/empresa/{empresaId}
Dashboard
GET /api/dashboard/resumen
GET /api/dashboard/resumen/empresa/{empresaId}
Reportes
GET /api/reportes/empresa/{empresaId}
GET /api/reportes/empresa/{empresaId}/pdf
GET /api/reportes/empresa/{empresaId}/excel
🚀 Ejecución local
1. Requisitos previos

Instala lo siguiente:

Java JDK 20
Maven
XAMPP con MySQL activo
NetBeans o IntelliJ IDEA
Python
2. Clonar el repositorio
git clone https://github.com/cito515432/global-iso-security.git
cd global-iso-security
3. Crear la base de datos

Abre XAMPP, enciende MySQL y crea una base de datos llamada exactamente:

globalisosecurity
4. Configurar application.properties

Ubicación:

backend/src/main/resources/application.properties

Configuración mínima sugerida:

spring.datasource.url=jdbc:mysql://localhost:3306/globalisosecurity
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
server.port=8081

# JWT
jwt.secret=TU_CLAVE_SECRETA
jwt.expiration=86400000
5. Ejecutar backend

Abre la carpeta backend en NetBeans o IntelliJ y ejecuta el proyecto.

El backend debe levantar en:

http://localhost:8081
6. Ejecutar frontend

Desde la raíz del proyecto:

python -m http.server 5500

Abrir en navegador:

http://localhost:5500/frontend/pages/login.html

No abrir el frontend con file:///, porque eso genera problemas de origen y conexión con el backend.

🗄️ Base de datos y datos de prueba

Spring Boot puede crear automáticamente las tablas si la base está vacía, pero no deja la base lista con datos útiles de prueba. Por eso, para trabajar con el mismo estado funcional del proyecto, se recomienda:

importar un respaldo .sql desde phpMyAdmin, o
poblar la base con datos mínimos de roles, empresas, sectores, servicios y checklist.
Recomendación de trabajo
Crear usuarios funcionales por API en lugar de insertarlos manualmente por SQL
Compartir un respaldo .sql actualizado entre integrantes del equipo
Mantener el nombre de la base exactamente como globalisosecurity
👤 Contrato actual para crear usuarios

Body esperado por el backend:

{
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "rawPassword": "123456",
  "rolId": 2,
  "empresaId": 1
}
🧪 Flujo recomendado de prueba

Se recomienda validar el sistema en este orden:

Login
Dashboard administrador
Consulta de roles
Consulta de empresas
Creación de usuarios
Servicios por empresa
Checklist completo por servicio
Evaluaciones
Firmas
Capacitaciones
Reportes JSON / PDF / Excel
📄 Documentación complementaria

La documentación del proyecto incluye:

análisis y contexto del sistema
requisitos funcionales y no funcionales
casos de uso
historias de usuario
informes técnicos de avance
guías de instalación local
handoff técnico entre backend y frontend
📌 Estado actual del proyecto
Componente	Estado
Backend	✅ Muy avanzado
Seguridad JWT	✅ Implementada
CRUD principales	✅ Implementados
Dashboard	✅ Implementado
Reportes PDF / Excel	✅ Implementados
Frontend visual	✅ Avanzado
Integración frontend-backend	🔄 En cierre
Pruebas end-to-end	🔄 En validación
Deploy	⏳ Pendiente
🚧 Pendientes de cierre
Validación funcional completa de extremo a extremo
Revisión final de integración frontend-backend
Ajustes menores de limpieza técnica
Respaldo SQL final compartido entre integrantes
Evidencia de pruebas y entrega académica
📬 Repositorio

Repositorio GitHub del proyecto:

https://github.com/cito515432/global-iso-security
📝 Licencia / uso

Proyecto académico desarrollado con fines formativos para la asignatura de Ingeniería Web.

Última actualización: Abril de 2026
