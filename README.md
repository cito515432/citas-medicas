# citas-medicas
Sistema web de gestión de citas médicas ✔ Public ✔ Add README ✔ Add .gitignore → selecciona: Python

# 🏥 SaludPro – Sistema de Gestión de Citas Médicas

## 📌 Descripción General

**SaludPro** es una aplicación web desarrollada para la gestión de citas médicas. Permite a los usuarios registrarse, iniciar sesión y agendar citas con diferentes especialidades y doctores de manera rápida y sencilla.

El sistema está compuesto por un **frontend en HTML, CSS y JavaScript** y un **backend en Python (Flask)** que gestiona la lógica y los datos.

---

## 🎯 Objetivo del Proyecto

Desarrollar una plataforma básica que permita:

* Registro de usuarios
* Inicio de sesión
* Agendamiento de citas médicas
* Visualización y eliminación de citas

---

## 🧱 Arquitectura del Sistema

El proyecto sigue una arquitectura cliente-servidor:

* **Frontend:** Interfaces visuales (HTML, CSS, JS)
* **Backend:** API REST (Flask)
* **Comunicación:** Fetch API (HTTP - JSON)

---

## 🖥️ Tecnologías Utilizadas

### Frontend

* HTML5
* CSS3
* JavaScript (Vanilla)

### Backend

* Python
* Flask
* Flask-CORS

### Herramientas

* Visual Studio Code
* Git y GitHub

---

## 📂 Estructura del Proyecto

```
citas-medicas/
│
├── backend/
│   ├── app.py
│   └── (lógica del servidor)
│
├── frontend/
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── dashboard.html
│   └── css/
│       └── styles.css
```

---

## 🔐 Funcionalidades Implementadas

### 👤 Registro de Usuario

* Permite crear una cuenta con correo y contraseña
* Validación básica de campos
* Conexión con API `/api/register`

---

### 🔑 Inicio de Sesión

* Autenticación de usuario
* Almacenamiento en `localStorage`
* Redirección automática según rol

---

### 📅 Gestión de Citas

* Selección de especialidad
* Selección de doctor
* Selección de fecha y hora
* Creación de cita (POST)
* Eliminación de cita (DELETE)

---

### 📋 Visualización de Citas

* Listado dinámico desde el backend
* Actualización automática después de crear/eliminar

---

## 🎨 Mejoras de Frontend Realizadas

* Diseño moderno y responsivo
* Uso de gradientes y tarjetas (cards)
* Unificación visual en:

  * Index
  * Login
  * Registro
  * Dashboard
* Mejora en inputs, botones y distribución
* Integración de identidad visual **SaludPro**

---

## ⚠️ Problemas Identificados

1. Las citas no están filtradas por usuario

   * Actualmente se muestran todas las citas del sistema

2. Manejo de respuestas del backend

   * En algunos casos devuelve errores aunque la operación fue exitosa

3. Validaciones limitadas

   * No se validan fechas pasadas
   * No hay validación avanzada de correo

---

## 🔧 Mejoras Pendientes

### Backend

* Filtrar citas por usuario autenticado
* Mejorar respuestas HTTP (status 200/201 correctos)
* Implementar base de datos (SQLite o MySQL)

---

### Frontend

* Agregar mensajes visuales (no solo alert)
* Implementar botón “Cerrar sesión”
* Validaciones más robustas
* Mejorar experiencia de usuario (UX)

---

### Seguridad

* Encriptar contraseñas
* Implementar autenticación con tokens (JWT)

---

## ▶️ Ejecución del Proyecto

### Backend

```
cd backend
python -m venv venv
venv\Scripts\activate
pip install flask flask-cors
python app.py
```

---

### Frontend

Abrir `index.html` desde:

* Live Server (recomendado)
* o navegador directamente

---

## 👩‍💻 Autor

Proyecto desarrollado como parte de aprendizaje en desarrollo web.

---

## 📌 Conclusión

El sistema cumple con las funcionalidades básicas de gestión de citas médicas, integrando frontend y backend. A pesar de ser una versión inicial, establece una base sólida para futuras mejoras en seguridad, escalabilidad y experiencia de usuario.
