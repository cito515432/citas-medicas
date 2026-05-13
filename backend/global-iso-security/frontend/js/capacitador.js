const API_URL = "/api";

// LINK REAL DEL MODULO 2
const VIDEO_MODULO_2 = "https://youtube.com/shorts/wWU18tWttkM?si=nL3gcM3hyPv381pW";

// LINK REAL DEL MODULO 3
const DIAPOSITIVA_MODULO_3 = "https://docs.google.com/presentation/d/17VIf2HG_O48G-NDCztIkmgOztvfIuV8bWE8sbQ7s4Fk/embed?start=false&loop=false&delayms=3000";

// Estado real del usuario logueado
let usuarioActual = {
  id: null,
  nombre: "Capacitador",
  email: "capacitador@globaliso.com",
  empresa: { id: null, nombre: "Sin empresa asignada" }
};

document.addEventListener("DOMContentLoaded", async () => {
  await cargarPerfilRealDesdeBackend();
  verModulo(1, document.querySelector(".btn-modulo.active"));

  const btnLogout = document.getElementById("btnLogout");
  if (btnLogout) {
    btnLogout.addEventListener("click", () => {
      localStorage.removeItem("token");
      localStorage.removeItem("nombre");
      localStorage.removeItem("email");
      localStorage.removeItem("rol");
      localStorage.removeItem("empresaId");
      localStorage.removeItem("empresaNombre");
      window.location.href = "login.html";
    });
  }
});

// ================================
// PERFIL REAL DESDE BACKEND
// ================================
async function cargarPerfilRealDesdeBackend() {
  const token = localStorage.getItem("token");

  if (!token) {
    console.warn("No hay token en localStorage.");
    cargarPerfilEnPantalla();
    return;
  }

  try {
    const resp = await fetch(`${API_URL}/usuarios/me`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (!resp.ok) {
      throw new Error(`Error al consultar /usuarios/me: ${resp.status}`);
    }

    const data = await resp.json();

    usuarioActual = {
      id: data?.id ?? null,
      nombre: data?.nombre || localStorage.getItem("nombre") || "Capacitador",
      email: data?.email || localStorage.getItem("email") || "capacitador@globaliso.com",
      empresa: {
        id: data?.empresa?.id ?? null,
        nombre: data?.empresa?.nombre || localStorage.getItem("empresaNombre") || "Sin empresa asignada"
      }
    };

    // persistencia opcional para reutilizar en otras vistas
    localStorage.setItem("nombre", usuarioActual.nombre);
    localStorage.setItem("email", usuarioActual.email);

    if (usuarioActual.empresa.id !== null) {
      localStorage.setItem("empresaId", String(usuarioActual.empresa.id));
    }
    if (usuarioActual.empresa.nombre) {
      localStorage.setItem("empresaNombre", usuarioActual.empresa.nombre);
    }

    cargarPerfilEnPantalla();
  } catch (error) {
    console.error("No se pudo cargar el perfil real desde backend:", error);

    // Fallback: intenta con localStorage
    usuarioActual = {
      id: null,
      nombre: localStorage.getItem("nombre") || "Capacitador",
      email: localStorage.getItem("email") || "capacitador@globaliso.com",
      empresa: {
        id: localStorage.getItem("empresaId") || null,
        nombre: localStorage.getItem("empresaNombre") || "Sin empresa asignada"
      }
    };

    cargarPerfilEnPantalla();
  }
}

function cargarPerfilEnPantalla() {
  setEl("nombreCapacitadorSidebar", usuarioActual.nombre);
  setEl("perfilNombreTitulo", usuarioActual.nombre);
  setVal("perfilNombreInput", usuarioActual.nombre);
  setVal("perfilEmailInput", usuarioActual.email);
  setVal("perfilEmpresaInput", usuarioActual.empresa?.nombre || "Sin empresa asignada");
}

// ================================
// NAVEGACION
// ================================
window.mostrarSeccion = function(seccion, btn) {
  document.querySelectorAll(".seccion").forEach(s => s.classList.remove("activa"));

  const target = document.getElementById("seccion-" + seccion);
  if (target) target.classList.add("activa");

  document.querySelectorAll(".btn-menu").forEach(b => b.classList.remove("active"));
  if (btn) btn.classList.add("active");
};

// ================================
// MODULOS
// ================================
window.verModulo = function(numeroModulo, btn = null) {
  document.querySelectorAll(".btn-modulo").forEach(b => b.classList.remove("active"));

  if (btn) {
    btn.classList.add("active");
  } else {
    const botones = document.querySelectorAll(".btn-modulo");
    const boton = botones[numeroModulo - 1];
    if (boton) boton.classList.add("active");
  }

  const titulo = document.getElementById("tituloModuloActivo");
  const descripcion = document.getElementById("descripcionModuloActivo");
  const contenido = document.getElementById("contenidoModuloActivo");

  if (!titulo || !descripcion || !contenido) return;

  if (numeroModulo === 1) {
    titulo.textContent = "Módulo 1: Fundamentos de ISO 27001";
    descripcion.textContent = "Conceptos básicos para comprender el Sistema de Gestión de Seguridad de la Información.";
    contenido.innerHTML = `
      <div class="modulo-texto">
        <h5>¿Qué es ISO 27001?</h5>
        <p>
          ISO 27001 es una norma internacional que establece los requisitos para implementar,
          mantener y mejorar un Sistema de Gestión de Seguridad de la Información (SGSI).
        </p>

        <h5>¿Qué es un SGSI?</h5>
        <p>
          Es un conjunto de políticas, procesos, procedimientos y controles diseñados para
          proteger la información de una organización frente a riesgos internos y externos.
        </p>

        <h5>Objetivos del SGSI</h5>
        <ul>
          <li>Proteger la información sensible de la organización.</li>
          <li>Reducir riesgos relacionados con la seguridad de la información.</li>
          <li>Garantizar continuidad, confianza y cumplimiento.</li>
          <li>Establecer controles claros y responsabilidades definidas.</li>
        </ul>

        <h5>Principios clave</h5>
        <ul>
          <li><strong>Confidencialidad:</strong> la información solo debe ser accesible para personas autorizadas.</li>
          <li><strong>Integridad:</strong> la información debe mantenerse exacta, completa y sin alteraciones no autorizadas.</li>
          <li><strong>Disponibilidad:</strong> la información debe estar accesible cuando se necesite.</li>
        </ul>

        <h5>Contexto de la organización</h5>
        <p>
          Antes de implementar ISO 27001, la empresa debe identificar su contexto interno y externo,
          las necesidades de las partes interesadas y el alcance del sistema de gestión.
        </p>

        <h5>Roles y responsabilidades</h5>
        <p>
          Es importante que cada persona dentro de la organización tenga claras sus responsabilidades
          en materia de seguridad de la información, desde la alta dirección hasta los usuarios operativos.
        </p>

        <h5>Importancia de la concienciación</h5>
        <p>
          La seguridad de la información no depende solo de la tecnología, sino también del comportamiento
          del personal. Por eso, la capacitación y la sensibilización son fundamentales.
        </p>
      </div>
    `;
  } else if (numeroModulo === 2) {
    titulo.textContent = "Módulo 2: Riesgos y Controles";
    descripcion.textContent = "Video de apoyo sobre riesgos, amenazas, vulnerabilidades y controles de seguridad.";

    const embedUrl = convertirYoutubeAEmbed(VIDEO_MODULO_2);

    contenido.innerHTML = `
      <iframe
        class="embed-frame"
        src="${embedUrl}"
        title="Video del módulo 2"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen>
      </iframe>
    `;
  } else if (numeroModulo === 3) {
    titulo.textContent = "Módulo 3: Incidentes, Cumplimiento y Cierre";
    descripcion.textContent = "Presentación visual sobre gestión de incidentes, cumplimiento y cierre de capacitación.";

    contenido.innerHTML = `
      <iframe
        class="embed-frame"
        src="${DIAPOSITIVA_MODULO_3}"
        title="Diapositiva del módulo 3"
        allowfullscreen>
      </iframe>
    `;
  }
};

// ================================
// HELPERS
// ================================
function convertirYoutubeAEmbed(url) {
  try {
    const u = new URL(url);

    if (u.hostname.includes("youtube.com") && u.pathname === "/watch") {
      const videoId = u.searchParams.get("v");
      return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
    }

    if (u.hostname.includes("youtube.com") && u.pathname.startsWith("/shorts/")) {
      const videoId = u.pathname.split("/shorts/")[1];
      return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
    }

    if (u.hostname.includes("youtu.be")) {
      const videoId = u.pathname.replace("/", "");
      return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
    }

    return url;
  } catch {
    return url;
  }
}

function setEl(id, valor) {
  const el = document.getElementById(id);
  if (el) el.textContent = valor;
}

function setVal(id, valor) {
  const el = document.getElementById(id);
  if (el) el.value = valor;
}