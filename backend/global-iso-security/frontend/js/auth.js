const API_URL = "/api";

async function login() {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMsg = document.getElementById('error-msg');

    if (!email || !password) {
        errorMsg.textContent = 'Por favor ingresa tu correo y contrasena.';
        errorMsg.style.display = 'block';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.status === 401) {
            errorMsg.textContent = 'Credenciales incorrectas. Verifica tu email y contrasena.';
            errorMsg.style.display = 'block';
            return;
        }

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('rol', data.rol);
            localStorage.setItem('nombre', data.nombre);

            const rol = data.rol.toLowerCase();
            if (rol === 'administrador') {
                window.location.href = 'admin.html';
            } else if (rol === 'implementador') {
                window.location.href = 'implementador.html';
            } else if (rol === 'auditor') {
                window.location.href = 'auditor.html';
            } else if (rol === 'capacitador') {
                window.location.href = 'capacitador.html';
            } else {
                window.location.href = 'usuario-empresa.html';
            }
        } else {
            errorMsg.textContent = data.message || 'Error al iniciar sesion.';
            errorMsg.style.display = 'block';
        }
    } catch (error) {
        errorMsg.textContent = 'No se pudo conectar con el servidor.';
        errorMsg.style.display = 'block';
    }
}