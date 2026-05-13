const API_BASE_URL = "/api";

let cacheUsuarios = [];
let cacheEmpresas = [];
let cacheRoles = [];

let modalEditarUsuario = null;
let modalCrearUsuario = null;
let modalCrearEmpresa = null;
let modalEditarEmpresa = null;

const CONFIG_KEY = "globaliso_admin_config_v1";

document.addEventListener("DOMContentLoaded", async () => {
    validarSesion();
    cargarDatosUsuario();
    configurarLogout();

    prepararVistaInicial();

    configurarModalUsuario();
    configurarModalCrearUsuario();
    configurarModalCrearEmpresa();
    configurarModalEditarEmpresa();

    configurarReportes();
    configurarConfiguracion();

    cargarResumenDashboard();
    await cargarUsuarios();
    await cargarRoles();
    await cargarEmpresas();
});

function validarSesion() {
    const token = localStorage.getItem("token");
    const rol = (localStorage.getItem("rol") || "").toUpperCase();

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    if (!rol.includes("ADMIN")) {
        alert("No tienes permisos para acceder a este panel");
        window.location.href = "login.html";
    }
}

function cargarDatosUsuario() {
    const nombre = localStorage.getItem("nombre") || "Administrador";
    setText("nombreAdmin", nombre);
}

function prepararVistaInicial() {
    setText("totalUsuarios", "Cargando...");
    setText("totalEmpresas", "Cargando...");
    setText("totalEvaluaciones", "Cargando...");

    setText("usuariosTotalResumen", "Cargando...");
    setText("empresasResumen", "Cargando...");

    setText("ultimaAuditoria", "Cargando...");
    setText("ultimoReporte", "Cargando...");
    setText("estadoSistema", "Operativo");

    setText("usuariosTotalSeccion", "Cargando...");
    setText("eventosRecientes", "Pendiente");

    setText("empresasRegistradas", "Cargando...");
    setText("serviciosTotalesEmpresas", "Cargando...");
    setText("totalImplementadores", "Cargando...");
    setText("totalCapacitadores", "Cargando...");
    setText("totalAuditores", "Cargando...");

    const tablaUsuariosBody = document.getElementById("tablaUsuariosBody");
    if (tablaUsuariosBody) {
        tablaUsuariosBody.innerHTML = `<tr><td colspan="6" class="text-center">Cargando usuarios...</td></tr>`;
    }

    const tablaEmpresasBody = document.getElementById("tablaEmpresasBody");
    if (tablaEmpresasBody) {
        tablaEmpresasBody.innerHTML = `<tr><td colspan="9" class="text-center">Cargando empresas...</td></tr>`;
    }
}

/* =========================
   MODALES
========================= */

function configurarModalUsuario() {
    const modalElement = document.getElementById("modalEditarUsuario");
    if (modalElement) modalEditarUsuario = new bootstrap.Modal(modalElement);

    const btnGuardar = document.getElementById("btnGuardarUsuario");
    if (btnGuardar) btnGuardar.addEventListener("click", guardarEdicionUsuario);
}

function configurarModalCrearUsuario() {
    const modalElement = document.getElementById("modalCrearUsuario");
    if (modalElement) modalCrearUsuario = new bootstrap.Modal(modalElement);

    const btnAbrir = document.getElementById("btnAbrirModalCrearUsuario");
    if (btnAbrir) {
        btnAbrir.addEventListener("click", async () => {
            await prepararFormularioCrearUsuario();
            ocultarErrorCrearUsuario();
            if (modalCrearUsuario) modalCrearUsuario.show();
        });
    }

    const btnGuardar = document.getElementById("btnGuardarNuevoUsuario");
    if (btnGuardar) btnGuardar.addEventListener("click", guardarNuevoUsuario);
}

function configurarModalCrearEmpresa() {
    const modalElement = document.getElementById("modalCrearEmpresa");
    if (modalElement) modalCrearEmpresa = new bootstrap.Modal(modalElement);

    const btnAbrir = document.getElementById("btnAbrirModalCrearEmpresa");
    if (btnAbrir) {
        btnAbrir.addEventListener("click", () => {
            setValue("crearEmpresaNombre", "");
            setValue("crearEmpresaSector", "");
            ocultarErrorCrearEmpresa();
            if (modalCrearEmpresa) modalCrearEmpresa.show();
        });
    }

    const btnGuardar = document.getElementById("btnGuardarNuevaEmpresa");
    if (btnGuardar) btnGuardar.addEventListener("click", guardarNuevaEmpresa);
}

/* =========================
   DASHBOARD
========================= */

async function cargarResumenDashboard() {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(`${API_BASE_URL}/dashboard/resumen`, {
            method: "GET",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {
            mostrarErrorDashboard();
            return;
        }

        if (!response.ok) {
            mostrarErrorDashboard();
            return;
        }

        const totalUsuarios = toNumber(data.totalUsuarios);
        const totalEmpresas = toNumber(data.totalEmpresas);
        const totalEvaluaciones = toNumber(data.totalEvaluaciones);

        setText("totalUsuarios", totalUsuarios);
        setText("totalEmpresas", totalEmpresas);
        setText("totalEvaluaciones", totalEvaluaciones);

        setText("usuariosTotalResumen", totalUsuarios);
        setText("empresasResumen", totalEmpresas);

        setText("ultimaAuditoria", valorODefecto(data.ultimaAuditoria, "No disponible"));
        setText("ultimoReporte", valorODefecto(data.ultimoReporte, "No disponible"));
        setText("estadoSistema", valorODefecto(data.estadoSistema, "Operativo"));

        setText("usuariosTotalSeccion", totalUsuarios);
        setText("eventosRecientes", valorODefecto(data.eventosRecientes, "Pendiente"));
    } catch (error) {
        console.error("Error cargando resumen del dashboard:", error);
        mostrarErrorDashboard();
    }
}

function mostrarErrorDashboard() {
    setText("totalUsuarios", "Error");
    setText("totalEmpresas", "Error");
    setText("totalEvaluaciones", "Error");
    setText("usuariosTotalResumen", "Error");
    setText("empresasResumen", "Error");
    setText("ultimaAuditoria", "Error");
    setText("ultimoReporte", "Error");
}

/* =========================
   USUARIOS
========================= */

async function cargarUsuarios() {
    const token = localStorage.getItem("token");
    const tablaBody = document.getElementById("tablaUsuariosBody");
    if (!tablaBody) return;

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios`, {
            method: "GET",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
        });

        const texto = await response.text();
        let usuarios = [];
        try {
            usuarios = texto ? JSON.parse(texto) : [];
        } catch {
            tablaBody.innerHTML = `<tr><td colspan="6" class="text-center">Error al leer usuarios</td></tr>`;
            return;
        }

        if (!response.ok) {
            tablaBody.innerHTML = `<tr><td colspan="6" class="text-center">No se pudieron cargar los usuarios</td></tr>`;
            return;
        }

        cacheUsuarios = Array.isArray(usuarios) ? usuarios : [];
        renderizarUsuarios(cacheUsuarios);
        setText("usuariosTotalSeccion", cacheUsuarios.length);
    } catch (error) {
        console.error("Error cargando usuarios:", error);
        tablaBody.innerHTML = `<tr><td colspan="6" class="text-center">Error de conexión al cargar usuarios</td></tr>`;
    }
}

function renderizarUsuarios(usuarios) {
    const tablaBody = document.getElementById("tablaUsuariosBody");
    if (!tablaBody) return;

    if (!usuarios || usuarios.length === 0) {
        tablaBody.innerHTML = `<tr><td colspan="6" class="text-center">No hay usuarios registrados</td></tr>`;
        return;
    }

    tablaBody.innerHTML = usuarios
        .map((usuario, index) => {
            const id = Number(usuario.id);
            const nombre = usuario.nombre || "Sin nombre";
            const email = usuario.email || "Sin email";
            const rol = usuario.rol?.nombre || usuario.rol || "Sin rol";

            let empresa = "Sin empresa";
            if (Array.isArray(usuario.empresas)) {
                empresa =
                    usuario.empresas
                        .map((e) => e?.nombre || e)
                        .filter(Boolean)
                        .join(", ") || "Sin empresa";
            } else {
                empresa = usuario.empresa?.nombre || usuario.empresa || "Sin empresa";
            }

            return `
                <tr>
                    <td>${index + 1}</td>
                    <td>${escapeHtml(nombre)}</td>
                    <td>${escapeHtml(email)}</td>
                    <td><span class="badge-rol ${claseRol(rol)}">${escapeHtml(rol)}</span></td>
                    <td>${escapeHtml(empresa)}</td>
                    <td>
                        <button class="btn btn-sm btn-accion-editar" title="Editar" onclick="abrirEditarUsuario(${id})">
                            <i class="bi bi-pencil-fill"></i>
                        </button>
                        <button class="btn btn-sm btn-accion-eliminar" title="Eliminar" onclick="eliminarUsuario(${id}, '${escapeJs(nombre)}')">
                            <i class="bi bi-trash-fill"></i>
                        </button>
                    </td>
                </tr>
            `;
        })
        .join("");
}

/** Lista para el select: cache + empresa del usuario si no viene en GET /empresas. */
function empresasOpcionesParaEditarUsuario(usuario) {
    const lista = cacheEmpresas.map((e) => ({ id: e.id, nombre: e.nombre || `Empresa #${e.id}` }));
    const rawId = usuario?.empresa_id ?? usuario?.empresaId ?? usuario?.empresa?.id;
    if (rawId === undefined || rawId === null || rawId === "") return lista;

    const idN = Number(rawId);
    if (!Number.isFinite(idN)) return lista;

    if (!lista.some((e) => Number(e.id) === idN)) {
        const nombre =
            (usuario.empresa && usuario.empresa.nombre) ||
            `Empresa #${idN}`;
        lista.push({ id: idN, nombre });
    }
    return lista;
}

async function abrirEditarUsuario(id) {
    let usuario = cacheUsuarios.find((u) => Number(u.id) === Number(id));
    if (!usuario) {
        alert("No se encontró el usuario");
        return;
    }

    await cargarRoles();
    await cargarEmpresas();

    const token = localStorage.getItem("token");
    try {
        const resDetalle = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: "GET",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
        });
        if (resDetalle.ok) {
            const texto = await resDetalle.text();
            try {
                const detalle = texto ? JSON.parse(texto) : {};
                if (detalle && typeof detalle === "object" && detalle.id != null) {
                    usuario = { ...usuario, ...detalle };
                }
            } catch {
                /* respuesta no JSON */
            }
        }
    } catch (e) {
        console.warn("No se pudo cargar detalle del usuario, se usa la lista en caché.", e);
    }

    setValue("editUsuarioId", usuario.id || "");
    setValue("editNombre", usuario.nombre || "");
    setValue("editEmail", usuario.email || "");
    setValue("editPassword", "");

    const selectRol = document.getElementById("editRol");
    const rolActualId = usuario.rol_id ?? usuario.rol?.id ?? "";

    if (selectRol) {
        selectRol.innerHTML =
            `<option value="">Selecciona un rol</option>` +
            cacheRoles
                .map(
                    (rol) => `
                <option value="${rol.id}" ${String(rol.id) === String(rolActualId) ? "selected" : ""}>
                    ${escapeHtml(rol.nombre)}
                </option>
            `
                )
                .join("");
    }

    const selectEmpresa = document.getElementById("editUsuarioEmpresa");
    const ayudaEmpresa = document.getElementById("editUsuarioEmpresaAyuda");
    const opcionesEmpresa = empresasOpcionesParaEditarUsuario(usuario);

    let empresaActualId =
        usuario.empresa_id ?? usuario.empresaId ?? usuario.empresa?.id ?? "";
    if (empresaActualId !== "" && empresaActualId != null) {
        empresaActualId = String(empresaActualId);
    }

    if (selectEmpresa) {
        // ✅ Primera opción es "Sin empresa" con value vacío — válido y explícito
        selectEmpresa.innerHTML =
            `<option value="">Sin empresa</option>` +
            opcionesEmpresa
                .map(
                    (empresa) => `
                <option value="${empresa.id}" ${
                    String(empresa.id) === String(empresaActualId) ? "selected" : ""
                }>
                    ${escapeHtml(empresa.nombre)}
                </option>
            `
                )
                .join("");
    }

    // ✅ Texto de ayuda actualizado: empresa es opcional
    if (ayudaEmpresa) {
        ayudaEmpresa.textContent =
            "Opcional. Puedes dejar este campo vacío si el usuario no pertenece a ninguna empresa.";
    }

    ocultarErrorEditarUsuario();
    // ✅ Ya no se bloquea el modal si no hay empresas cargadas
    if (modalEditarUsuario) modalEditarUsuario.show();
}

async function guardarEdicionUsuario() {
    const token = localStorage.getItem("token");

    const id = getValue("editUsuarioId");
    const nombre = getValue("editNombre").trim();
    const email = getValue("editEmail").trim();
    const password = getValue("editPassword").trim();
    const rolId = getValue("editRol");
    const empresaId = getValue("editUsuarioEmpresa"); // ✅ puede quedar vacío

    if (!nombre || !email) {
        mostrarErrorEditarUsuario("Nombre y correo son obligatorios.");
        return;
    }

    if (!rolId) {
        mostrarErrorEditarUsuario("Selecciona un rol.");
        return;
    }

    // ✅ Empresa ya NO es obligatoria — se envía null si no se selecciona
    const payload = {
        nombre,
        email,
        rolId: Number(rolId),
        empresaId: empresaId ? Number(empresaId) : null
    };

    if (password) payload.rawPassword = password;

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
            body: JSON.stringify(payload)
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {}

        if (!response.ok) {
            console.error("PUT usuarios", response.status, texto);
            mostrarErrorEditarUsuario(data.message || data.error || texto || "No se pudo actualizar el usuario.");
            return;
        }

        if (modalEditarUsuario) modalEditarUsuario.hide();

        alert("Usuario actualizado correctamente");
        await cargarUsuarios();
        refrescarTablaEmpresasConUsuarios();
        await cargarResumenDashboard();
    } catch (error) {
        console.error("Error guardando usuario:", error);
        mostrarErrorEditarUsuario("Error de conexión al actualizar el usuario.");
    }
}

async function eliminarUsuario(id, nombre) {
    const token = localStorage.getItem("token");

    const confirmado = confirm(
        `Vas a eliminar al usuario "${nombre}".\n\nEsta acción no se puede deshacer.\n\n¿Deseas continuar?`
    );
    if (!confirmado) return;

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {}

        if (!response.ok) {
            alert(data.message || texto || "No se pudo eliminar el usuario");
            return;
        }

        alert("Usuario eliminado correctamente");
        await cargarUsuarios();
        refrescarTablaEmpresasConUsuarios();
        await cargarResumenDashboard();
    } catch (error) {
        console.error("Error eliminando usuario:", error);
        alert("Error de conexión al eliminar el usuario");
    }
}

async function prepararFormularioCrearUsuario() {
    if (cacheRoles.length === 0) await cargarRoles();
    if (cacheEmpresas.length === 0) await cargarEmpresas();

    setValue("crearNombre", "");
    setValue("crearEmail", "");
    setValue("crearPassword", "");

    const selectRol = document.getElementById("crearRol");
    if (selectRol) {
        selectRol.innerHTML =
            `<option value="">Selecciona un rol</option>` +
            cacheRoles.map((rol) => `<option value="${rol.id}">${escapeHtml(rol.nombre)}</option>`).join("");
    }

    const selectEmpresa = document.getElementById("crearEmpresa");
    if (selectEmpresa) {
        // ✅ Primera opción "Sin empresa" también en creación
        selectEmpresa.innerHTML =
            `<option value="">Sin empresa</option>` +
            cacheEmpresas.map((empresa) => `<option value="${empresa.id}">${escapeHtml(empresa.nombre)}</option>`).join("");
    }
}

async function guardarNuevoUsuario() {
    const token = localStorage.getItem("token");

    const nombre = getValue("crearNombre").trim();
    const email = getValue("crearEmail").trim();
    const password = getValue("crearPassword").trim();
    const rolId = getValue("crearRol");
    const empresaId = getValue("crearEmpresa"); // ✅ puede quedar vacío

    if (!nombre || !email || !password || !rolId) {
        mostrarErrorCrearUsuario("Nombre, correo, contraseña y rol son obligatorios.");
        return;
    }

    // ✅ Empresa opcional también en creación
    const payload = {
        nombre,
        email,
        rolId: Number(rolId),
        empresaId: empresaId ? Number(empresaId) : null,
        rawPassword: password
    };

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
            body: JSON.stringify(payload)
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {}

        if (!response.ok) {
            console.error("POST usuarios", response.status, texto);
            mostrarErrorCrearUsuario(data.message || data.error || texto || "No se pudo crear el usuario.");
            return;
        }

        if (modalCrearUsuario) modalCrearUsuario.hide();

        alert("Usuario creado correctamente");
        await cargarUsuarios();
        refrescarTablaEmpresasConUsuarios();
        await cargarResumenDashboard();
    } catch (error) {
        console.error("Error creando usuario:", error);
        mostrarErrorCrearUsuario("Error de conexión al crear el usuario.");
    }
}

/* =========================
   EMPRESAS
========================= */

function empresaIdDeUsuario(u) {
    if (!u) return NaN;
    const raw = u.empresa_id ?? u.empresaId ?? u.empresa?.id;
    if (raw === undefined || raw === null || raw === "") return NaN;
    const n = Number(raw);
    return Number.isFinite(n) ? n : NaN;
}

function rolNombreStaffDeUsuario(u) {
    if (!u) return "";
    let nombre = "";
    if (u.rol != null && typeof u.rol === "object" && u.rol.nombre != null) {
        nombre = String(u.rol.nombre);
    } else if (typeof u.rol === "string") {
        nombre = u.rol;
    } else {
        const rid = u.rol_id ?? u.rolId ?? (typeof u.rol === "number" ? u.rol : null);
        if (rid != null && cacheRoles.length) {
            const r = cacheRoles.find((x) => Number(x.id) === Number(rid));
            if (r && r.nombre) nombre = String(r.nombre);
        }
    }
    return nombre.toUpperCase();
}

function staffPorEmpresaDesdeUsuarios(empresaId) {
    const eid = Number(empresaId);
    const porRol = { IMPLEMENTADOR: [], CAPACITADOR: [], AUDITOR: [] };

    if (!Number.isFinite(eid) || !cacheUsuarios.length) {
        return { implementador: "", capacitador: "", auditor: "" };
    }

    for (const u of cacheUsuarios) {
        const uidEmpresa = empresaIdDeUsuario(u);
        if (uidEmpresa !== eid) continue;

        const rolNombre = rolNombreStaffDeUsuario(u);
        const etiqueta = (u.nombre || u.email || "").trim();
        if (!etiqueta) continue;

        if (rolNombre.includes("IMPLEMENTADOR")) porRol.IMPLEMENTADOR.push(etiqueta);
        else if (rolNombre.includes("CAPACITADOR")) porRol.CAPACITADOR.push(etiqueta);
        else if (rolNombre.includes("AUDITOR")) porRol.AUDITOR.push(etiqueta);
    }

    const join = (arr) => (arr.length ? [...new Set(arr)].join(", ") : "");

    return {
        implementador: join(porRol.IMPLEMENTADOR),
        capacitador: join(porRol.CAPACITADOR),
        auditor: join(porRol.AUDITOR)
    };
}

function conteoServiciosPorStaff(empresaId) {
    const eid = Number(empresaId);
    if (!Number.isFinite(eid) || !cacheUsuarios.length) return 0;

    let n = 0;
    for (const u of cacheUsuarios) {
        if (empresaIdDeUsuario(u) !== eid) continue;
        const rol = rolNombreStaffDeUsuario(u);
        if (rol.includes("IMPLEMENTADOR") || rol.includes("AUDITOR") || rol.includes("CAPACITADOR")) {
            n += 1;
        }
    }
    return n;
}

function refrescarTablaEmpresasConUsuarios() {
    if (cacheEmpresas.length > 0) renderizarEmpresas(cacheEmpresas);
}

async function cargarEmpresas() {
    const token = localStorage.getItem("token");
    const tablaEmpresasBody = document.getElementById("tablaEmpresasBody");

    const endpoints = [
        `${API_BASE_URL}/empresas`,
        `${API_BASE_URL}/admin/empresas`,
        `${API_BASE_URL}/dashboard/empresas`
    ];

    for (const endpoint of endpoints) {
        try {
            const response = await fetch(endpoint, {
                method: "GET",
                headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
            });

            const texto = await response.text();
            let empresas = [];

            try {
                empresas = texto ? JSON.parse(texto) : [];
            } catch {
                continue;
            }

            if (!response.ok || !Array.isArray(empresas)) continue;

            cacheEmpresas = empresas;
            poblarSelectEmpresasReportes();
            if (tablaEmpresasBody) {
                renderizarEmpresas(empresas);
            }
            return;
        } catch (error) {
            console.error(`Error cargando empresas desde ${endpoint}:`, error);
        }
    }

    cacheEmpresas = [];
    poblarSelectEmpresasReportes();
    if (tablaEmpresasBody) {
        tablaEmpresasBody.innerHTML = `<tr><td colspan="9" class="text-center">No se pudieron cargar las empresas desde la API.</td></tr>`;
    }
}

function renderizarEmpresas(empresas) {
    const tablaEmpresasBody = document.getElementById("tablaEmpresasBody");
    if (!tablaEmpresasBody) return;

    if (!empresas || empresas.length === 0) {
        tablaEmpresasBody.innerHTML = `<tr><td colspan="9" class="text-center">No hay empresas registradas</td></tr>`;
        setText("empresasRegistradas", 0);
        setText("serviciosTotalesEmpresas", 0);
        setText("totalImplementadores", 0);
        setText("totalCapacitadores", 0);
        setText("totalAuditores", 0);
        return;
    }

    const implementadores = new Set();
    const capacitadores = new Set();
    const auditores = new Set();

    tablaEmpresasBody.innerHTML = empresas
        .map((empresa, index) => {
            const id = Number(empresa.id);
            const nombre = empresa.nombre || "Sin nombre";
            const sector = empresa.sector?.nombre || empresa.sector_nombre || empresa.sector || "Sin sector";
            const apiServicios = toNumber(empresa.totalServicios ?? empresa.servicios ?? 0);
            const staffServicios = conteoServiciosPorStaff(id);
            const totalServicios = Math.max(apiServicios, staffServicios);

            const desdeApi = {
                implementador: empresa.implementador?.nombre || empresa.implementador || "",
                capacitador: empresa.capacitador?.nombre || empresa.capacitador || "",
                auditor: empresa.auditor?.nombre || empresa.auditor || ""
            };
            const desdeUsuarios = staffPorEmpresaDesdeUsuarios(id);

            const implementador =
                (desdeApi.implementador && String(desdeApi.implementador)) ||
                desdeUsuarios.implementador ||
                "No asignado";
            const capacitador =
                (desdeApi.capacitador && String(desdeApi.capacitador)) ||
                desdeUsuarios.capacitador ||
                "No asignado";
            const auditor =
                (desdeApi.auditor && String(desdeApi.auditor)) ||
                desdeUsuarios.auditor ||
                "No asignado";

            const estadoGeneral = empresa.estadoGeneral || empresa.estado || "No definido";

            if (implementador !== "No asignado") implementadores.add(implementador);
            if (capacitador !== "No asignado") capacitadores.add(capacitador);
            if (auditor !== "No asignado") auditores.add(auditor);

            return `
                <tr>
                    <td>${index + 1}</td>
                    <td>${escapeHtml(nombre)}</td>
                    <td>${escapeHtml(sector)}</td>
                    <td>${escapeHtml(String(totalServicios))}</td>
                    <td><span class="badge-rol implementador">${escapeHtml(implementador)}</span></td>
                    <td><span class="badge-rol capacitador">${escapeHtml(capacitador)}</span></td>
                    <td><span class="badge-rol auditor">${escapeHtml(auditor)}</span></td>
                    <td>${escapeHtml(estadoGeneral)}</td>
                    <td>
                        <button class="btn btn-sm btn-accion-editar" title="Editar" onclick="editarEmpresa(${id})">
                            <i class="bi bi-pencil-fill"></i>
                        </button>
                        <button class="btn btn-sm btn-accion-eliminar" title="Eliminar" onclick="eliminarEmpresa(${id}, '${escapeJs(nombre)}')">
                            <i class="bi bi-trash-fill"></i>
                        </button>
                    </td>
                </tr>
            `;
        })
        .join("");

    setText("empresasRegistradas", empresas.length);
    setText(
        "serviciosTotalesEmpresas",
        empresas.reduce((acc, e) => {
            const eid = Number(e.id);
            const apiS = toNumber(e.totalServicios ?? e.servicios ?? 0);
            const staffS = conteoServiciosPorStaff(eid);
            return acc + Math.max(apiS, staffS);
        }, 0)
    );
    setText("totalImplementadores", implementadores.size);
    setText("totalCapacitadores", capacitadores.size);
    setText("totalAuditores", auditores.size);
}

async function guardarNuevaEmpresa() {
    const token = localStorage.getItem("token");

    const nombre = getValue("crearEmpresaNombre").trim();
    const sector = getValue("crearEmpresaSector").trim();

    if (!nombre) {
        mostrarErrorCrearEmpresa("El nombre de la empresa es obligatorio.");
        return;
    }

    const payload = { nombre, sector };

    try {
        const response = await fetch(`${API_BASE_URL}/empresas`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
            body: JSON.stringify(payload)
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {}

        if (!response.ok) {
            console.error("POST empresas", response.status, texto);
            mostrarErrorCrearEmpresa(data.message || data.error || texto || "No se pudo crear la empresa.");
            return;
        }

        if (modalCrearEmpresa) modalCrearEmpresa.hide();

        alert("Empresa creada correctamente");
        await cargarEmpresas();
        await cargarResumenDashboard();
    } catch (error) {
        console.error("Error creando empresa:", error);
        mostrarErrorCrearEmpresa("Error de conexión al crear la empresa.");
    }
}

function configurarModalEditarEmpresa() {
    const modalElement = document.getElementById("modalEditarEmpresa");
    if (modalElement) modalEditarEmpresa = new bootstrap.Modal(modalElement);

    const btnGuardar = document.getElementById("btnGuardarEdicionEmpresa");
    if (btnGuardar) btnGuardar.addEventListener("click", guardarEdicionEmpresa);
}

async function editarEmpresa(id) {
    const empresa = cacheEmpresas.find((e) => Number(e.id) === Number(id));
    if (!empresa) {
        alert("No se encontró la empresa");
        return;
    }

    setValue("editEmpresaId", empresa.id ?? "");
    setValue("editEmpresaNombre", (empresa.nombre || "").trim());

    ocultarErrorEditarEmpresa();
    if (modalEditarEmpresa) modalEditarEmpresa.show();
}

async function guardarEdicionEmpresa() {
    const token = localStorage.getItem("token");
    const id = getValue("editEmpresaId");
    const nombre = getValue("editEmpresaNombre").trim();

    if (!id) {
        mostrarErrorEditarEmpresa("Identificador de empresa no válido.");
        return;
    }

    if (!nombre) {
        mostrarErrorEditarEmpresa("El nombre de la empresa es obligatorio.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/empresas/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
            body: JSON.stringify({ nombre })
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {}

        if (!response.ok) {
            console.error("PUT empresas", response.status, texto);
            mostrarErrorEditarEmpresa(
                data.message || data.error || (typeof data === "string" ? data : "") || texto || "No se pudo actualizar la empresa."
            );
            return;
        }

        if (modalEditarEmpresa) modalEditarEmpresa.hide();

        alert("Empresa actualizada correctamente");
        await cargarEmpresas();
        await cargarResumenDashboard();
    } catch (error) {
        console.error("Error guardando empresa:", error);
        mostrarErrorEditarEmpresa("Error de conexión al actualizar la empresa.");
    }
}

async function eliminarEmpresa(id, nombre) {
    const token = localStorage.getItem("token");

    const confirmado = confirm(
        `Vas a eliminar la empresa "${nombre}".\n\nEsta acción no se puede deshacer.\n\n¿Deseas continuar?`
    );
    if (!confirmado) return;

    try {
        const response = await fetch(`${API_BASE_URL}/empresas/${id}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
        });

        const texto = await response.text();
        let data = {};
        try {
            data = texto ? JSON.parse(texto) : {};
        } catch {}

        if (!response.ok) {
            alert(data.message || texto || "No se pudo eliminar la empresa");
            return;
        }

        alert("Empresa eliminada correctamente");
        await cargarEmpresas();
        await cargarResumenDashboard();
    } catch (error) {
        console.error("Error eliminando empresa:", error);
        alert("Error de conexión al eliminar la empresa");
    }
}

/* =========================
   ROLES
========================= */

async function cargarRoles() {
    if (cacheRoles.length > 0) return cacheRoles;

    const token = localStorage.getItem("token");
    const endpoints = [`${API_BASE_URL}/roles`, `${API_BASE_URL}/admin/roles`];

    for (const endpoint of endpoints) {
        try {
            const response = await fetch(endpoint, {
                method: "GET",
                headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }
            });

            const texto = await response.text();
            let roles = [];

            try {
                roles = texto ? JSON.parse(texto) : [];
            } catch {
                continue;
            }

            if (response.ok && Array.isArray(roles) && roles.length > 0) {
                cacheRoles = roles;
                return roles;
            }
        } catch (error) {
            console.error(`Error cargando roles desde ${endpoint}:`, error);
        }
    }

    cacheRoles = [
        { id: 1, nombre: "ADMINISTRADOR" },
        { id: 2, nombre: "IMPLEMENTADOR" },
        { id: 3, nombre: "AUDITOR" },
        { id: 4, nombre: "CAPACITADOR" },
        { id: 5, nombre: "USUARIO" }
    ];
    return cacheRoles;
}

/* =========================
   REPORTES REALES PDF / EXCEL
========================= */

function configurarReportes() {
    const btnPdf = document.getElementById("btnReportePdf");
    const btnExcel = document.getElementById("btnReporteExcel");

    if (btnPdf) btnPdf.addEventListener("click", () => descargarReporteReal("pdf"));
    if (btnExcel) btnExcel.addEventListener("click", () => descargarReporteReal("excel"));

    poblarSelectEmpresasReportes();
}

function poblarSelectEmpresasReportes() {
    const select = document.getElementById("reporteEmpresaSelect");
    if (!select) return;

    if (!cacheEmpresas || cacheEmpresas.length === 0) {
        select.innerHTML = `<option value="">No hay empresas cargadas</option>`;
        return;
    }

    select.innerHTML =
        `<option value="">Selecciona una empresa</option>` +
        cacheEmpresas
            .map((empresa) => `
                <option value="${empresa.id}">${escapeHtml(empresa.nombre || `Empresa #${empresa.id}`)}</option>
            `)
            .join("");
}

async function descargarReporteReal(formato) {
    const token = localStorage.getItem("token");
    const empresaId = getValue("reporteEmpresaSelect");
    const tipo = getValue("reporteTipoSelect") || "general";

    if (!empresaId) {
        alert("Selecciona una empresa para generar el reporte.");
        return;
    }

    const extension = formato === "excel" ? "xlsx" : "pdf";
    const endpoint = `${API_BASE_URL}/reportes/empresa/${empresaId}/${formato}`;
    const btnPdf = document.getElementById("btnReportePdf");
    const btnExcel = document.getElementById("btnReporteExcel");

    try {
        setText("reporteEstado", `Generando reporte ${extension.toUpperCase()}...`);
        if (btnPdf) btnPdf.disabled = true;
        if (btnExcel) btnExcel.disabled = true;

        const response = await fetch(endpoint, {
            method: "GET",
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!response.ok) {
            const texto = await response.text();
            throw new Error(texto || `No se pudo generar el reporte ${extension.toUpperCase()}`);
        }

        const blob = await response.blob();
        const nombreDesdeHeader = obtenerNombreArchivo(response.headers.get("Content-Disposition"));
        const fecha = new Date().toISOString().slice(0, 10);
        const nombreArchivo = nombreDesdeHeader || `reporte_${tipo}_empresa_${empresaId}_${fecha}.${extension}`;

        descargarBlob(blob, nombreArchivo);
        setText("reporteEstado", `Último reporte generado: ${nombreArchivo}`);
    } catch (error) {
        console.error("Error descargando reporte:", error);
        setText("reporteEstado", `Error: ${error.message || "No se pudo descargar el reporte"}`);
        alert(error.message || "No se pudo descargar el reporte");
    } finally {
        if (btnPdf) btnPdf.disabled = false;
        if (btnExcel) btnExcel.disabled = false;
    }
}

function obtenerNombreArchivo(contentDisposition) {
    if (!contentDisposition) return null;
    const match = /filename\*?=(?:UTF-8''|")?([^";]+)/i.exec(contentDisposition);
    return match ? decodeURIComponent(match[1].replaceAll('"', '').trim()) : null;
}

function descargarBlob(blob, filename) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
}

/* =========================
   CONFIGURACIÓN (localStorage)
========================= */

function configurarConfiguracion() {
    aplicarConfiguracionDesdeStorage();

    const btnGuardar = document.getElementById("btnGuardarConfiguracion");
    if (btnGuardar) btnGuardar.addEventListener("click", guardarConfiguracionEnStorage);
}

function leerConfiguracionUI() {
    return {
        nombreSistema: getValue("cfgNombreSistema").trim(),
        version: getValue("cfgVersion").trim(),
        soporteEmail: getValue("cfgSoporteEmail").trim(),
        estadoSistema: getValue("cfgEstadoSistema"),

        sesionDuracionMin: toNumber(getValue("cfgSesionDuracion")),
        intentosMax: toNumber(getValue("cfgIntentosMax")),
        bloqueoMin: toNumber(getValue("cfgBloqueoMin")),
        sesionAuto: isChecked("sesionAuto"),

        flujo: {
            editarBorrador: isChecked("editarBorrador"),
            editarProceso: isChecked("editarProceso"),
            bloquearFirmado: isChecked("bloquearFirmado"),
            bloquearCerrado: isChecked("bloquearCerrado")
        },

        reportes: {
            pdfActivo: isChecked("pdfActivo"),
            excelActivo: isChecked("excelActivo"),
            logoReporte: isChecked("logoReporte"),
            soloCerrado: isChecked("soloCerrado")
        }
    };
}

function escribirConfiguracionUI(cfg) {
    if (!cfg) return;

    setValue("cfgNombreSistema", cfg.nombreSistema ?? "Global ISO Security");
    setValue("cfgVersion", cfg.version ?? "1.0.0");
    setValue("cfgSoporteEmail", cfg.soporteEmail ?? "soporte@globaliso.com");
    setValue("cfgEstadoSistema", cfg.estadoSistema ?? "Operativo");

    setValue("cfgSesionDuracion", String(cfg.sesionDuracionMin ?? 30));
    setValue("cfgIntentosMax", String(cfg.intentosMax ?? 5));
    setValue("cfgBloqueoMin", String(cfg.bloqueoMin ?? 10));

    setChecked("sesionAuto", cfg.sesionAuto ?? true);

    setChecked("editarBorrador", cfg.flujo?.editarBorrador ?? true);
    setChecked("editarProceso", cfg.flujo?.editarProceso ?? true);
    setChecked("bloquearFirmado", cfg.flujo?.bloquearFirmado ?? true);
    setChecked("bloquearCerrado", cfg.flujo?.bloquearCerrado ?? true);

    setChecked("pdfActivo", cfg.reportes?.pdfActivo ?? true);
    setChecked("excelActivo", cfg.reportes?.excelActivo ?? true);
    setChecked("logoReporte", cfg.reportes?.logoReporte ?? true);
    setChecked("soloCerrado", cfg.reportes?.soloCerrado ?? true);
}

function guardarConfiguracionEnStorage() {
    const cfg = leerConfiguracionUI();
    localStorage.setItem(CONFIG_KEY, JSON.stringify(cfg));
    setText("configEstado", `Configuración guardada (${new Date().toLocaleString()}).`);
    setText("estadoSistema", cfg.estadoSistema || "Operativo");
}

function aplicarConfiguracionDesdeStorage() {
    const raw = localStorage.getItem(CONFIG_KEY);
    if (!raw) {
        setText("configEstado", "No hay configuración guardada (usando valores por defecto).");
        return;
    }

    try {
        const cfg = JSON.parse(raw);
        escribirConfiguracionUI(cfg);
        setText("configEstado", `Configuración cargada (${new Date().toLocaleString()}).`);
        setText("estadoSistema", cfg.estadoSistema || "Operativo");
    } catch {
        setText("configEstado", "No se pudo leer la configuración guardada.");
    }
}

/* =========================
   LOGOUT
========================= */

function configurarLogout() {
    const btnLogout = document.getElementById("btnLogout");
    if (!btnLogout) return;

    btnLogout.addEventListener("click", () => {
        localStorage.removeItem("token");
        localStorage.removeItem("nombre");
        localStorage.removeItem("email");
        localStorage.removeItem("rol");
        window.location.href = "login.html";
    });
}

/* =========================
   ERRORES UI
========================= */

function mostrarErrorEditarUsuario(mensaje) {
    const error = document.getElementById("editUsuarioError");
    if (error) {
        error.textContent = mensaje;
        error.style.display = "block";
    }
}

function ocultarErrorEditarUsuario() {
    const error = document.getElementById("editUsuarioError");
    if (error) {
        error.textContent = "";
        error.style.display = "none";
    }
}

function mostrarErrorCrearUsuario(mensaje) {
    const error = document.getElementById("crearUsuarioError");
    if (error) {
        error.textContent = mensaje;
        error.style.display = "block";
    }
}

function ocultarErrorCrearUsuario() {
    const error = document.getElementById("crearUsuarioError");
    if (error) {
        error.textContent = "";
        error.style.display = "none";
    }
}

function mostrarErrorCrearEmpresa(mensaje) {
    const error = document.getElementById("crearEmpresaError");
    if (error) {
        error.textContent = mensaje;
        error.style.display = "block";
    }
}

function ocultarErrorCrearEmpresa() {
    const error = document.getElementById("crearEmpresaError");
    if (error) {
        error.textContent = "";
        error.style.display = "none";
    }
}

function mostrarErrorEditarEmpresa(mensaje) {
    const error = document.getElementById("editEmpresaError");
    if (error) {
        error.textContent = mensaje;
        error.style.display = "block";
    }
}

function ocultarErrorEditarEmpresa() {
    const error = document.getElementById("editEmpresaError");
    if (error) {
        error.textContent = "";
        error.style.display = "none";
    }
}

/* =========================
   HELPERS
========================= */

function setText(id, valor) {
    const elemento = document.getElementById(id);
    if (elemento) elemento.textContent = valor ?? "0";
}

function setValue(id, valor) {
    const elemento = document.getElementById(id);
    if (elemento) elemento.value = valor ?? "";
}

function getValue(id) {
    const elemento = document.getElementById(id);
    return elemento ? elemento.value : "";
}

function isChecked(id) {
    const el = document.getElementById(id);
    return !!(el && el.checked);
}

function setChecked(id, checked) {
    const el = document.getElementById(id);
    if (el) el.checked = !!checked;
}

function toNumber(valor) {
    const numero = Number(valor);
    return Number.isFinite(numero) ? numero : 0;
}

function valorODefecto(valor, defecto) {
    return valor === null || valor === undefined || valor === "" ? defecto : valor;
}

function claseRol(rol) {
    const valor = String(rol || "").toLowerCase();
    if (valor.includes("implementador")) return "implementador";
    if (valor.includes("capacitador")) return "capacitador";
    if (valor.includes("auditor")) return "auditor";
    if (valor.includes("administrador")) return "administrador";
    if (valor.includes("usuario")) return "usuario";
    return "";
}

function escapeHtml(texto) {
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function escapeJs(texto) {
    return String(texto).replace(/\\/g, "\\\\").replace(/'/g, "\\'");
}

function downloadBlob(content, filename, mimeType) {
    const blob = new Blob([content], { type: mimeType || "application/octet-stream" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
}

window.refrescarDatosSeccionEmpresas = async function () {
    await cargarUsuarios();
    await cargarRoles();
    if (cacheEmpresas.length > 0) {
        renderizarEmpresas(cacheEmpresas);
    } else {
        await cargarEmpresas();
    }
};