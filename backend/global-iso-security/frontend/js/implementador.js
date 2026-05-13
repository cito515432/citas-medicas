const API_URL = "/api";

// Estado global
let empresaActual = null;        // { id, nombre, servicioId }
let estadosChecklist = {};       // { controlId: 'CUMPLE' | 'NO_CUMPLE' | 'PENDIENTE' }
let justificaciones = {};        // { controlId: 'texto...' }

// ─────────────────────────────────────────────────────────────────────────────
// CONTROLES ISO 27001 — Anexo A (misma estructura que el auditor)
// ─────────────────────────────────────────────────────────────────────────────
const ANEXOS_ISO = [
  {
    codigo: 'A.5', titulo: 'Políticas de Seguridad de la Información', icono: 'bi-shield-lock-fill',
    controles: [
      { id: 'a51', titulo: 'A.5.1 — Políticas para la seguridad de la información', pregunta: '¿Existe una política de seguridad aprobada por la dirección?' },
      { id: 'a52', titulo: 'A.5.2 — Revisión de las políticas',                     pregunta: '¿Se revisan las políticas de seguridad a intervalos planificados?' }
    ]
  },
  {
    codigo: 'A.6', titulo: 'Organización de la Seguridad', icono: 'bi-people-fill',
    controles: [
      { id: 'a61', titulo: 'A.6.1 — Roles y responsabilidades de seguridad', pregunta: '¿Están definidos los roles y responsabilidades de seguridad?' },
      { id: 'a62', titulo: 'A.6.2 — Segregación de funciones',               pregunta: '¿Se aplica segregación de funciones para reducir riesgos?' }
    ]
  },
  {
    codigo: 'A.8', titulo: 'Gestión de Activos', icono: 'bi-hdd-stack-fill',
    controles: [
      { id: 'a81', titulo: 'A.8.1 — Inventario de activos',    pregunta: '¿Existe un inventario actualizado de los activos de información?' },
      { id: 'a82', titulo: 'A.8.2 — Clasificación de activos', pregunta: '¿Se clasifican los activos según su nivel de confidencialidad?' }
    ]
  },
  {
    codigo: 'A.9', titulo: 'Control de Acceso', icono: 'bi-key-fill',
    controles: [
      { id: 'a91', titulo: 'A.9.1 — Política de control de acceso', pregunta: '¿Existe una política formal de control de acceso?' },
      { id: 'a92', titulo: 'A.9.2 — Gestión de acceso de usuarios', pregunta: '¿Se aplican controles de acceso según roles y privilegios mínimos?' }
    ]
  },
  {
    codigo: 'A.12', titulo: 'Seguridad en las Operaciones', icono: 'bi-gear-wide-connected',
    controles: [
      { id: 'a121', titulo: 'A.12.3 — Copias de seguridad',         pregunta: '¿Se ejecutan copias de seguridad y pruebas de restauración?' },
      { id: 'a122', titulo: 'A.12.6 — Gestión de vulnerabilidades', pregunta: '¿Se gestiona la identificación y corrección de vulnerabilidades técnicas?' }
    ]
  },
  {
    codigo: 'A.16', titulo: 'Gestión de Incidentes de Seguridad', icono: 'bi-exclamation-triangle-fill',
    controles: [
      { id: 'a161', titulo: 'A.16.1 — Gestión de incidentes y mejoras', pregunta: '¿Se realiza gestión de incidentes de seguridad de la información?' }
    ]
  },
  {
    codigo: 'A.11', titulo: 'Seguridad Física y del Entorno', icono: 'bi-building-lock',
    controles: [
      { id: 'a111', titulo: 'A.11.1 — Áreas seguras',            pregunta: '¿Se controlan los accesos físicos y ambientales a los recursos críticos?' },
      { id: 'a112', titulo: 'A.11.2 — Seguridad de los equipos', pregunta: '¿Los equipos están protegidos de amenazas físicas y ambientales?' }
    ]
  },
  {
    codigo: 'A.7', titulo: 'Seguridad de los Recursos Humanos', icono: 'bi-person-check-fill',
    controles: [
      { id: 'a71', titulo: 'A.7.2 — Concienciación y formación', pregunta: '¿Se capacita al personal en seguridad de la información y buenas prácticas?' }
    ]
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// INIT
// ─────────────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  cargarDatosUsuario();
  cargarEmpresaAsignada();

  document.getElementById('btnLogout').addEventListener('click', () => {
    localStorage.clear();
    window.location.href = 'login.html';
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// DATOS DE USUARIO
// ─────────────────────────────────────────────────────────────────────────────
function cargarDatosUsuario() {
  const nombre = localStorage.getItem('nombre') || 'Implementador';
  let email = localStorage.getItem('email') || '';

  if (!email) {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        email = payload.sub || '';
        localStorage.setItem('email', email);
      } catch (e) { console.error('Error leyendo token:', e); }
    }
  }

  setEl('nombreImplementadorSidebar', nombre);
  setEl('perfilNombreTitulo', nombre);
  setVal('perfilNombreInput', nombre);
  setVal('perfilEmailInput', email);
}

// ─────────────────────────────────────────────────────────────────────────────
// EMPRESA ASIGNADA AL IMPLEMENTADOR
// ─────────────────────────────────────────────────────────────────────────────
async function cargarEmpresaAsignada() {
  const token = localStorage.getItem('token');

  try {
    // 1) Datos del usuario autenticado
    const resME = await fetch(`${API_URL}/usuarios/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!resME.ok) throw new Error('Sin datos de usuario');
    const usuario = await resME.json();

    const empresaId   = usuario.empresa?.id     || usuario.empresaId     || null;
    const empresaNombre = usuario.empresa?.nombre || usuario.empresaNombre || null;

    if (!empresaId || !empresaNombre) {
      mostrarSinEmpresa();
      return;
    }

    // 2) Obtener servicioId de la empresa
    const resSvc = await fetch(`${API_URL}/servicios`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const servicios = await resSvc.json();
    const servicio = servicios.find(s => (s.empresa?.id || s.empresaId) === empresaId);
    const servicioId = servicio?.id || null;

    empresaActual = { id: empresaId, nombre: empresaNombre, servicioId };

    // 3) Pintar en todos los lugares
    mostrarEmpresaDashboard(empresaNombre, servicioId);
    setEl('empresaChecklistLabel', empresaNombre);
    setEl('procesoEmpresaNombre', empresaNombre);
    setVal('perfilEmpresaInput', empresaNombre);

    // Mostrar sección proceso
    const alertaEl = document.getElementById('alertaProceso');
    const contenidoEl = document.getElementById('contenidoProceso');
    if (alertaEl) alertaEl.style.display = 'none';
    if (contenidoEl) contenidoEl.style.display = 'block';

    // 4) Cargar controles reales desde backend y actualizar contadores
    if (servicioId) {
      await cargarChecklistDesdeBackend(servicioId);
    }
    renderizarChecklist();
    actualizarTodo();

  } catch (err) {
    console.error('Error cargando empresa:', err);
    mostrarSinEmpresa();
  }
}

function mostrarSinEmpresa() {
  const el = document.getElementById('empresaInfoDashboard');
  if (el) el.innerHTML = `
    <div class="alerta-info">
      <i class="bi bi-info-circle me-2" style="color:#3b82f6"></i>
      No tienes una empresa asignada. Contacta al administrador.
    </div>`;
}

function mostrarEmpresaDashboard(nombre, servicioId) {
  const el = document.getElementById('empresaInfoDashboard');
  if (!el) return;
  el.innerHTML = `
    <div class="d-flex align-items-center gap-3 flex-wrap">
      <i class="bi bi-building-check" style="font-size:2rem; color:#10b981"></i>
      <div>
        <p class="text-white fw-bold mb-0">${nombre}</p>
        <small class="text-muted">Empresa asignada para implementación${servicioId ? ' · Servicio #' + servicioId : ''}</small>
      </div>
      <button class="btn-accion ms-auto" onclick="mostrarSeccion('checklist', null)">
        <i class="bi bi-clipboard-check me-1"></i>Ir al Checklist
      </button>
    </div>`;
}

// ─────────────────────────────────────────────────────────────────────────────
// NAVEGACIÓN
// ─────────────────────────────────────────────────────────────────────────────
window.mostrarSeccion = function(seccion, btn) {
  document.querySelectorAll('.seccion').forEach(s => s.classList.remove('activa'));
  const target = document.getElementById('seccion-' + seccion);
  if (target) target.classList.add('activa');
  document.querySelectorAll('.btn-menu').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  window.scrollTo({ top: 0, behavior: 'smooth' });
};


// ─────────────────────────────────────────────────────────────────────────────
// CARGAR CHECKLIST REAL DESDE BACKEND
// ─────────────────────────────────────────────────────────────────────────────
async function cargarChecklistDesdeBackend(servicioId) {
  const token = localStorage.getItem('token');

  try {
    const res = await fetch(`${API_URL}/checklists/servicio/${servicioId}/completo`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
      console.warn('No se pudo cargar checklist desde backend. Se usa fallback local.');
      return;
    }

    const data = await res.json();
    const items = Array.isArray(data.items) ? data.items : [];
    if (!items.length) return;

    ANEXOS_ISO.length = 0;
    estadosChecklist = {};
    if (typeof justificaciones !== 'undefined') justificaciones = {};

    const grupos = new Map();

    items.forEach((item) => {
      const parsed = parsearControlBackend(item);
      if (!grupos.has(parsed.codigo)) {
        grupos.set(parsed.codigo, {
          codigo: parsed.codigo,
          titulo: parsed.grupo,
          icono: parsed.icono,
          controles: []
        });
      }

      grupos.get(parsed.codigo).controles.push({
        id: String(item.id),
        titulo: parsed.titulo,
        pregunta: parsed.pregunta
      });

      estadosChecklist[String(item.id)] = normalizarEstadoFrontend(item.estado);
      if (item.observacion && typeof justificaciones !== 'undefined') {
        justificaciones[String(item.id)] = item.observacion;
      }
    });

    ANEXOS_ISO.push(...Array.from(grupos.values()));
  } catch (error) {
    console.error('Error cargando checklist desde backend:', error);
  }
}

function parsearControlBackend(item) {
  const texto = String(item.pregunta || '').trim();
  const matchIso = texto.match(/^(A\.\d+(?:\.\d+)?)\s*[—-]\s*([^:]+):\s*(.+)$/i);
  const matchSector = texto.match(/^([A-ZÁÉÍÓÚÑ]{3,10}-\d+)\s*[—-]\s*([^:]+):\s*(.+)$/i);

  if (matchIso) {
    const codigoControl = matchIso[1].toUpperCase();
    const anexo = codigoControl.split('.').slice(0, 2).join('.');
    return {
      codigo: anexo,
      grupo: nombreGrupoIso(anexo),
      icono: iconoPorGrupo(anexo),
      titulo: `${codigoControl} — ${matchIso[2].trim()}`,
      pregunta: matchIso[3].trim()
    };
  }

  if (matchSector) {
    const prefijo = matchSector[1].split('-')[0].toUpperCase();
    return {
      codigo: prefijo,
      grupo: nombreGrupoSector(prefijo),
      icono: iconoPorGrupo(prefijo),
      titulo: `${matchSector[1].toUpperCase()} — ${matchSector[2].trim()}`,
      pregunta: matchSector[3].trim()
    };
  }

  return {
    codigo: 'GENERAL',
    grupo: 'Controles generales',
    icono: 'bi-shield-check',
    titulo: `Control #${item.id}`,
    pregunta: texto || 'Control sin descripción'
  };
}

function normalizarEstadoFrontend(estado) {
  const valor = String(estado || 'PENDIENTE').trim().toUpperCase();
  return ['CUMPLE', 'NO_CUMPLE', 'EN_PROCESO', 'PENDIENTE'].includes(valor) ? valor : 'PENDIENTE';
}

function nombreGrupoIso(codigo) {
  const nombres = {
    'A.5': 'Controles organizacionales',
    'A.6': 'Controles de personas',
    'A.7': 'Controles físicos',
    'A.8': 'Controles tecnológicos'
  };
  return nombres[codigo] || 'Controles ISO 27001';
}

function nombreGrupoSector(prefijo) {
  const nombres = {
    SALUD: 'Controles específicos del sector salud',
    EDU: 'Controles específicos del sector educación',
    FIN: 'Controles específicos del sector financiero',
    TEC: 'Controles específicos del sector tecnología',
    MAN: 'Controles específicos del sector manufactura'
  };
  return nombres[prefijo] || 'Controles específicos del sector';
}

function iconoPorGrupo(codigo) {
  if (codigo === 'A.5') return 'bi-diagram-3-fill';
  if (codigo === 'A.6') return 'bi-people-fill';
  if (codigo === 'A.7') return 'bi-building-lock';
  if (codigo === 'A.8') return 'bi-cpu-fill';
  if (codigo === 'SALUD') return 'bi-hospital-fill';
  if (codigo === 'EDU') return 'bi-mortarboard-fill';
  if (codigo === 'FIN') return 'bi-bank2';
  if (codigo === 'TEC') return 'bi-cloud-lock-fill';
  if (codigo === 'MAN') return 'bi-gear-wide-connected';
  return 'bi-shield-check';
}

async function guardarEstadoBackend(controlId, estado, observacion) {
  const token = localStorage.getItem('token');
  const body = { estado };
  if (observacion && observacion.trim()) body.observacion = observacion.trim();

  const res = await fetch(`${API_URL}/items-checklist/${controlId}/evaluar`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    body: JSON.stringify(body)
  });

  if (!res.ok) {
    const texto = await res.text();
    throw new Error(texto || 'No se pudo guardar el estado del control');
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// RENDERIZAR CHECKLIST ISO 27001
// ─────────────────────────────────────────────────────────────────────────────
function renderizarChecklist() {
  const box = document.getElementById('checklistContenido');
  if (!box) return;

  let html = '';
  ANEXOS_ISO.forEach(anexo => {
    html += `
      <div class="anexo-header">
        <i class="bi ${anexo.icono} me-2"></i>Anexo ${anexo.codigo} — ${anexo.titulo}
      </div>`;

    anexo.controles.forEach(ctrl => {
      const estado = estadosChecklist[ctrl.id] || 'PENDIENTE';
      const justificacion = justificaciones[ctrl.id] || '';
      const badgeClass = estado === 'CUMPLE' ? 'cumple' : estado === 'NO_CUMPLE' ? 'no-cumple' : 'pendiente';
      const badgeLabel = estado === 'CUMPLE' ? 'Cumple' : estado === 'NO_CUMPLE' ? 'No Cumple' : 'Pendiente';

      const activeCumple    = estado === 'CUMPLE'    ? 'active' : '';
      const activeNoCumple  = estado === 'NO_CUMPLE' ? 'active' : '';
      const activePendiente = estado === 'PENDIENTE' ? 'active' : '';

      // La justificación solo aparece cuando el estado es NO_CUMPLE
      const justDisplay = estado === 'NO_CUMPLE' ? '' : 'display:none;';

      html += `
        <div class="checklist-item" data-id="${ctrl.id}" data-estado="${estado}">
          <div class="row g-3 align-items-start">

            <!-- Título y pregunta -->
            <div class="col-md-6">
              <p class="control-title">${ctrl.titulo}</p>
              <p class="control-desc">${ctrl.pregunta}</p>
            </div>

            <!-- Badge de estado -->
            <div class="col-md-2 d-flex align-items-center">
              <span class="badge-estado ${badgeClass}" id="badge-${ctrl.id}">${badgeLabel}</span>
            </div>

            <!-- Botones -->
            <div class="col-md-4 d-flex gap-2 flex-wrap align-items-start">
              <button class="btn-estado cumple ${activeCumple}"
                onclick="setEstado(this, '${ctrl.id}', 'CUMPLE')">
                <i class="bi bi-check-lg me-1"></i>Cumple
              </button>
              <button class="btn-estado no-cumple ${activeNoCumple}"
                onclick="setEstado(this, '${ctrl.id}', 'NO_CUMPLE')">
                <i class="bi bi-x-lg me-1"></i>No Cumple
              </button>
              <button class="btn-estado pendiente ${activePendiente}"
                onclick="setEstado(this, '${ctrl.id}', 'PENDIENTE')">
                <i class="bi bi-clock me-1"></i>Pendiente
              </button>
            </div>
          </div>

          <!-- Justificación — solo visible cuando No Cumple -->
          <div class="justificacion-box" id="just-box-${ctrl.id}" style="${justDisplay}">
            <label class="justificacion-label">
              <i class="bi bi-exclamation-circle me-1" style="color:#ef4444"></i>
              Justificación / Plan de acción (obligatorio cuando No Cumple)
            </label>
            <textarea class="justificacion-input" rows="3"
              id="just-texto-${ctrl.id}"
              placeholder="Describe por qué no cumple y qué acciones se tomarán para corregirlo..."
              onchange="guardarJustificacion('${ctrl.id}', this.value)"
            >${justificacion}</textarea>
          </div>
        </div>`;
    });
  });

  box.innerHTML = html;
}

// ─────────────────────────────────────────────────────────────────────────────
// SETEAR ESTADO
// ─────────────────────────────────────────────────────────────────────────────
window.setEstado = async function(btn, controlId, estado) {
  const item = btn.closest('.checklist-item');
  item.dataset.estado = estado;
  estadosChecklist[controlId] = estado;

  // Actualizar badge
  const badge = document.getElementById('badge-' + controlId);
  const badgeClass = estado === 'CUMPLE' ? 'cumple' : estado === 'NO_CUMPLE' ? 'no-cumple' : 'pendiente';
  const badgeLabel = estado === 'CUMPLE' ? 'Cumple' : estado === 'NO_CUMPLE' ? 'No Cumple' : 'Pendiente';
  badge.className = 'badge-estado ' + badgeClass;
  badge.textContent = badgeLabel;

  // Activar botón seleccionado
  item.querySelectorAll('.btn-estado').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  // Mostrar/ocultar justificación
  const justBox = document.getElementById('just-box-' + controlId);
  if (justBox) {
    justBox.style.display = estado === 'NO_CUMPLE' ? '' : 'none';
    if (estado !== 'NO_CUMPLE') {
      justificaciones[controlId] = '';
      const textarea = document.getElementById('just-texto-' + controlId);
      if (textarea) textarea.value = '';
    }
  }

  actualizarTodo();

  // Guardado real en backend. Si es NO_CUMPLE, se guarda cuando escriba justificación.
  if (estado !== 'NO_CUMPLE') {
    try {
      await guardarEstadoBackend(controlId, estado, null);
    } catch (error) {
      console.error(error);
      alert(error.message || 'No se pudo guardar el estado del control.');
    }
  }
};

// ─────────────────────────────────────────────────────────────────────────────
// GUARDAR JUSTIFICACIÓN
// ─────────────────────────────────────────────────────────────────────────────
window.guardarJustificacion = async function(controlId, valor) {
  justificaciones[controlId] = valor;
  if ((estadosChecklist[controlId] || 'PENDIENTE') !== 'NO_CUMPLE') return;

  if (!valor || !valor.trim()) return;

  try {
    await guardarEstadoBackend(controlId, 'NO_CUMPLE', valor);
  } catch (error) {
    console.error(error);
    alert(error.message || 'No se pudo guardar la justificación del control.');
  }
};

// ─────────────────────────────────────────────────────────────────────────────
// FILTRAR ITEMS
// ─────────────────────────────────────────────────────────────────────────────
window.filtrarItems = function(filtro, btn) {
  document.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');

  document.querySelectorAll('.checklist-item').forEach(item => {
    const estado = item.dataset.estado || 'PENDIENTE';
    const visible = filtro === 'todos'
      || (filtro === 'cumple'    && estado === 'CUMPLE')
      || (filtro === 'no-cumple' && estado === 'NO_CUMPLE')
      || (filtro === 'pendiente' && estado === 'PENDIENTE');
    item.style.display = visible ? '' : 'none';
  });
};

// ─────────────────────────────────────────────────────────────────────────────
// CONTADORES Y PORCENTAJE
// ─────────────────────────────────────────────────────────────────────────────
function actualizarTodo() {
  let cumple = 0, noCumple = 0, pendiente = 0;
  const total = ANEXOS_ISO.reduce((s, a) => s + a.controles.length, 0);

  ANEXOS_ISO.forEach(anexo => {
    anexo.controles.forEach(ctrl => {
      const est = estadosChecklist[ctrl.id] || 'PENDIENTE';
      if (est === 'CUMPLE') cumple++;
      else if (est === 'NO_CUMPLE') noCumple++;
      else pendiente++;
    });
  });

  const respondidos = cumple + noCumple;
  const porcentaje  = total > 0 ? Math.round((respondidos / total) * 100) : 0;

  // Dashboard
  setEl('totalCumple',    cumple);
  setEl('totalNoCumple',  noCumple);
  setEl('totalPendiente', pendiente);
  setEl('totalPorcentaje', porcentaje + '%');
  setEl('barraLabel',     porcentaje + '%');
  const barra = document.getElementById('barraProgreso');
  if (barra) barra.style.width = porcentaje + '%';

  // Proceso
  setEl('procesoCumple',        cumple);
  setEl('procesoNoCumple',      noCumple);
  setEl('procesoPendiente',     pendiente);
  setEl('procesoPorcentaje',    porcentaje + '%');
  setEl('procesoPorcentajeBarra', porcentaje + '%');
  const barraProceso = document.getElementById('barraProceso');
  if (barraProceso) barraProceso.style.width = porcentaje + '%';
}

// ─────────────────────────────────────────────────────────────────────────────
// FIRMAR
// ─────────────────────────────────────────────────────────────────────────────
window.firmarChecklist = async function() {
  if (!empresaActual) {
    alert('No tienes una empresa asignada.');
    return;
  }

  // Validar que los No Cumple tengan justificación
  const sinJustificacion = Object.entries(estadosChecklist)
    .filter(([id, est]) => est === 'NO_CUMPLE' && !justificaciones[id]?.trim())
    .length;

  if (sinJustificacion > 0) {
    alert(`Hay ${sinJustificacion} control(es) marcados como "No Cumple" sin justificación. Por favor complétalos antes de firmar.`);
    return;
  }

  const totalPendiente = Object.values(estadosChecklist).filter(e => e === 'PENDIENTE').length;
  const totalControles = ANEXOS_ISO.reduce((s, a) => s + a.controles.length, 0);
  const sinEvaluar = totalControles - Object.keys(estadosChecklist).length;
  const pendientesTotal = totalPendiente + sinEvaluar;

  if (pendientesTotal > 0) {
    const ok = confirm(`Aún tienes ${pendientesTotal} control(es) sin evaluar. ¿Deseas firmar de todas formas?`);
    if (!ok) return;
  }

  if (!empresaActual.servicioId) {
    alert('No se encontró un servicio activo para esta empresa. Contacta al administrador.');
    return;
  }

  const token  = localStorage.getItem('token');
  const nombre = localStorage.getItem('nombre') || 'Implementador';

  try {
    const res = await fetch(`${API_URL}/firmas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({
        nombreFirmante: nombre,
        cargo: 'Implementador',
        estado: 'FIRMADA',
        servicioId: empresaActual.servicioId
      })
    });

    if (res.ok) {
      alert(`✅ Implementación firmada correctamente para ${empresaActual.nombre}`);
    } else {
      alert('Error al firmar. Revisa la consola para más detalles.');
      console.error(await res.text());
    }
  } catch (e) {
    console.error(e);
    alert('Error de conexión con el servidor.');
  }
};

// ─────────────────────────────────────────────────────────────────────────────
// UTILIDADES
// ─────────────────────────────────────────────────────────────────────────────
function setEl(id, valor) {
  const el = document.getElementById(id);
  if (el) el.textContent = valor;
}

function setVal(id, valor) {
  const el = document.getElementById(id);
  if (el) el.value = valor;
}