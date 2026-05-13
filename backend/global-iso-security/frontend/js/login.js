const API_BASE_URL = "/api";

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    const mensajeError = document.getElementById("mensajeError");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!email || !password) {
            mostrarError("Debes ingresar usuario y contraseña");
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            });

            const responseText = await response.text();
            let data;

            try {
                data = JSON.parse(responseText);
            } catch (e) {
                data = { message: responseText };
            }

            if (!response.ok) {
                mostrarError(data.message || "Credenciales inválidas");
                return;
            }

            localStorage.setItem("token", data.token);
            localStorage.setItem("nombre", data.nombre || "");
            localStorage.setItem("email", data.email || email);
            localStorage.setItem("rol", data.rol || "");

            redirigirSegunRol(data.rol);
        } catch (error) {
            console.error("Error en login:", error);
            mostrarError("No se pudo conectar con el servidor");
        }
    });

    function mostrarError(mensaje) {
        mensajeError.textContent = mensaje;
        mensajeError.style.display = "block";
    }

    function redirigirSegunRol(rol) {
        const rolNormalizado = (rol || "").toUpperCase();

        if (rolNormalizado.includes("ADMIN")) {
            window.location.href = "admin.html";
            return;
        }

        if (rolNormalizado.includes("IMPLEMENTADOR")) {
            window.location.href = "implementador.html";
            return;
        }

        if (rolNormalizado.includes("AUDITOR")) {
            window.location.href = "auditor.html";
            return;
        }

        if (rolNormalizado.includes("CAPACITADOR")) {
            window.location.href = "capacitador.html";
            return;
        }

        window.location.href = "admin.html";
    }
});
