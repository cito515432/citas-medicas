const API_URL = "/api";

// Estado global
let empresaActual = null;          // { id, nombre, servicioId }
let estadosChecklist = {};         // { itemId: 'CUMPLE' | 'NO_CUMPLE' | 'PENDIENTE' }

// ─────────────────────────────────────────────────────────────────────────────
// CONTROLES ISO 27001 — Anexo A (estructura fija del checklist)
// ─────────────────────────────────────────────────────────────────────────────
const ANEXOS_ISO = [
  {
    codigo: 'A.5',
    titulo: 'Políticas de Seguridad de la Información',
    icono: 'bi-shield-lock-fill',
    controles: [
      { id: 'a51', titulo: 'A.5.1 — Políticas para la seguridad de la información', pregunta: '¿Existe una política de seguridad aprobada por la dirección?' },
      { id: 'a52', titulo: 'A.5.2 — Revisión de las políticas',                     pregunta: '¿Se revisan las políticas de seguridad a intervalos planificados?' }
    ]
  },
  {
    codigo: 'A.6',
    titulo: 'Organización de la Seguridad',
    icono: 'bi-people-fill',
    controles: [
      { id: 'a61', titulo: 'A.6.1 — Roles y responsabilidades de seguridad', pregunta: '¿Están definidos los roles y responsabilidades de seguridad?' },
      { id: 'a62', titulo: 'A.6.2 — Segregación de funciones',               pregunta: '¿Se aplica segregación de funciones para reducir riesgos?' }
    ]
  },
  {
    codigo: 'A.8',
    titulo: 'Gestión de Activos',
    icono: 'bi-hdd-stack-fill',
    controles: [
      { id: 'a81', titulo: 'A.8.1 — Inventario de activos',     pregunta: '¿Existe un inventario actualizado de los activos de información?' },
      { id: 'a82', titulo: 'A.8.2 — Clasificación de activos',  pregunta: '¿Se clasifican los activos según su nivel de confidencialidad?' }
    ]
  },
  {
    codigo: 'A.9',
    titulo: 'Control de Acceso',
    icono: 'bi-key-fill',
    controles: [
      { id: 'a91', titulo: 'A.9.1 — Política de control de acceso', pregunta: '¿Existe una política formal de control de acceso?' },
      { id: 'a92', titulo: 'A.9.2 — Gestión de acceso de usuarios', pregunta: '¿Se aplican controles de acceso según roles y privilegios mínimos?' }
    ]
  },
  {
    codigo: 'A.12',
    titulo: 'Seguridad en las Operaciones',
    icono: 'bi-gear-wide-connected',
    controles: [
      { id: 'a121', titulo: 'A.12.3 — Copias de seguridad',           pregunta: '¿Se ejecutan copias de seguridad y pruebas de restauración?' },
      { id: 'a122', titulo: 'A.12.6 — Gestión de vulnerabilidades',   pregunta: '¿Se gestiona la identificación y corrección de vulnerabilidades técnicas?' }
    ]
  },
  {
    codigo: 'A.16',
    titulo: 'Gestión de Incidentes de Seguridad',
    icono: 'bi-exclamation-triangle-fill',
    controles: [
      { id: 'a161', titulo: 'A.16.1 — Gestión de incidentes y mejoras', pregunta: '¿Se realiza gestión de incidentes de seguridad de la información?' }
    ]
  },
  {
    codigo: 'A.11',
    titulo: 'Seguridad Física y del Entorno',
    icono: 'bi-building-lock',
    controles: [
      { id: 'a111', titulo: 'A.11.1 — Áreas seguras',           pregunta: '¿Se controlan los accesos físicos y ambientales a los recursos críticos?' },
      { id: 'a112', titulo: 'A.11.2 — Seguridad de los equipos', pregunta: '¿Los equipos están protegidos de amenazas físicas y ambientales?' }
    ]
  },
  {
    codigo: 'A.7',
    titulo: 'Seguridad de los Recursos Humanos',
    icono: 'bi-person-check-fill',
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
  cargarEmpresaAsignada();   // carga la empresa del auditor desde el token/API

  document.getElementById('btnLogout').addEventListener('click', () => {
    localStorage.clear();
    window.location.href = 'login.html';
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// DATOS DE USUARIO
// ─────────────────────────────────────────────────────────────────────────────
function cargarDatosUsuario() {
  const nombre = localStorage.getItem('nombre') || 'Auditor';
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

  document.getElementById('nombreAuditor').textContent = nombre;
  document.getElementById('perfilNombre').textContent = nombre;
  document.getElementById('perfilNombreInput').value = nombre;
  document.getElementById('perfilEmail').value = email;
}

// ─────────────────────────────────────────────────────────────────────────────
// EMPRESA ASIGNADA AL AUDITOR
// Llama a /api/usuarios/me para obtener empresa_id, luego busca el servicio
// activo de esa empresa para obtener servicioId (necesario para firmar).
// ─────────────────────────────────────────────────────────────────────────────
async function cargarEmpresaAsignada() {
  const token = localStorage.getItem('token');

  try {
    // 1) Obtener datos del usuario autenticado
    const resME = await fetch(`${API_URL}/usuarios/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!resME.ok) throw new Error('No se pudo obtener el usuario');
    const usuario = await resME.json();

    const empresaId = usuario.empresa?.id || usuario.empresaId || null;
    const empresaNombre = usuario.empresa?.nombre || usuario.empresaNombre || null;

    if (!empresaId || !empresaNombre) {
      mostrarSinEmpresa();
      return;
    }

    // 2) Obtener servicio asociado a la empresa
    const resSvc = await fetch(`${API_URL}/servicios`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const servicios = await resSvc.json();
    const servicio = servicios.find(s => (s.empresa?.id || s.empresaId) === empresaId);
    const servicioId = servicio?.id || null;

    empresaActual = { id: empresaId, nombre: empresaNombre, servicioId };
    localStorage.setItem('empresaActual', JSON.stringify(empresaActual));

    // 3) Pintar en Dashboard
    mostrarEmpresaEnDashboard(empresaNombre, servicioId);

    // 4) Actualizar sección Empresas (card marcada)
    renderizarEmpresaCard(empresaId, empresaNombre, servicioId);

    // 5) Pintar nombre empresa en Procesos
    document.getElementById('procesoEmpresaNombre').textContent = empresaNombre;
    document.getElementById('alertaProcesos').style.display = 'none';
    document.getElementById('contenidoProcesos').style.display = 'block';

    // 6) Cargar checklist real del servicio y estados guardados
    if (servicioId) {
      await cargarChecklistDesdeBackend(servicioId);
    }
    await cargarEstadosDesdeAPI(servicioId);

    // 7) Actualizar contadores/porcentaje
    actualizarTodo();

  } catch (err) {
    console.error('Error cargando empresa asignada:', err);
    mostrarSinEmpresa();
  }
}

function mostrarSinEmpresa() {
  document.getElementById('empresaSeleccionadaInfo').innerHTML = `
    <div class="alerta-info">
      <i class="bi bi-info-circle me-2" style="color:#8b5cf6"></i>
      No tienes una empresa asignada. Contacta al administrador.
    </div>`;
}

function mostrarEmpresaEnDashboard(nombre, servicioId) {
  document.getElementById('empresaSeleccionadaInfo').innerHTML = `
    <div class="d-flex align-items-center gap-3 flex-wrap">
      <i class="bi bi-building-check" style="font-size:2rem; color:#10b981"></i>
      <div>
        <p class="text-white fw-bold mb-0">${nombre}</p>
        <small class="text-muted">Empresa asignada para auditoría${servicioId ? ' · Servicio #' + servicioId : ''}</small>
      </div>
      <button class="btn-crear ms-auto" onclick="mostrarSeccion('empresas', null); setTimeout(abrirChecklistEmpresa, 200)">
        <i class="bi bi-clipboard-check me-1"></i>Ir al Checklist
      </button>
    </div>`;
}

function renderizarEmpresaCard(empresaId, nombre, servicioId) {
  const lista = document.getElementById('listaEmpresas');
  lista.innerHTML = `
    <div class="col-md-4">
      <div class="empresa-card seleccionada" id="card-empresa-${empresaId}">
        <div class="d-flex align-items-center gap-3">
          <i class="bi bi-building" style="font-size:1.8rem; color:#8b5cf6"></i>
          <div>
            <p class="text-white fw-bold mb-0">${nombre}</p>
            <small class="text-muted">ID: ${empresaId}${servicioId ? ' · Servicio #' + servicioId : ''}</small>
          </div>
        </div>
        <div class="mt-2 d-flex gap-2 align-items-center">
          <span style="font-size:0.75rem; color:#10b981;">
            <i class="bi bi-check-circle me-1"></i>Empresa asignada
          </span>
          <button class="btn-crear ms-auto" onclick="abrirChecklistEmpresa()">
            <i class="bi bi-clipboard-check me-1"></i>Auditar
          </button>
        </div>
      </div>
    </div>`;

  document.getElementById('empresasAuditadas').textContent = '1';
}

// ─────────────────────────────────────────────────────────────────────────────
// CARGAR ESTADOS PREVIOS DESDE LA API (evaluaciones guardadas)
// ─────────────────────────────────────────────────────────────────────────────
async function cargarEstadosDesdeAPI(servicioId) {
  if (!servicioId) return;
  const token = localStorage.getItem('token');
  try {
    const res = await fetch(`${API_URL}/evaluaciones?servicioId=${servicioId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) return;
    const evaluaciones = await res.json();
    // Mapear respuesta a estadosChecklist por itemId
    evaluaciones.forEach(ev => {
      const key = ev.itemChecklistId || ev.item_checklist_id;
      if (key) estadosChecklist[key] = ev.estado; // 'CUMPLE' | 'NO_CUMPLE' | 'PENDIENTE'
    });
  } catch (e) {
    // Si el endpoint no existe todavía, simplemente ignoramos
    console.warn('No se pudieron cargar evaluaciones previas:', e);
  }
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
// CHECKLIST — abrir y renderizar
// ─────────────────────────────────────────────────────────────────────────────
window.abrirChecklistEmpresa = function() {
  if (!empresaActual) return;
  document.getElementById('vistaListaEmpresas').style.display = 'none';
  document.getElementById('vistaChecklist').style.display = 'block';
  document.getElementById('empresaChecklist').textContent = empresaActual.nombre;
  renderizarChecklist();
};

window.volverAEmpresas = function() {
  document.getElementById('vistaChecklist').style.display = 'none';
  document.getElementById('vistaListaEmpresas').style.display = 'block';
};

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
      const badgeClass = estado === 'CUMPLE' ? 'cumple' : estado === 'NO_CUMPLE' ? 'no-cumple' : 'pendiente';
      const badgeLabel = estado === 'CUMPLE' ? 'Cumple' : estado === 'NO_CUMPLE' ? 'No Cumple' : 'Pendiente';
      const activeCumple = estado === 'CUMPLE' ? 'active' : '';
      const activeNoCumple = estado === 'NO_CUMPLE' ? 'active' : '';

      html += `
        <div class="checklist-item" data-id="${ctrl.id}" data-estado="${estado}">
          <div class="row g-3 align-items-center">
            <div class="col-md-7">
              <p class="control-title">${ctrl.titulo}</p>
              <p class="control-desc">${ctrl.pregunta}</p>
            </div>
            <div class="col-md-3">
              <span class="badge-estado ${badgeClass}" id="estado-${ctrl.id}">${badgeLabel}</span>
            </div>
            <div class="col-md-2 d-flex gap-2">
              <button class="btn-estado cumple ${activeCumple}"
                onclick="setEstado(this, '${ctrl.id}', 'CUMPLE')">Cumple</button>
              <button class="btn-estado no-cumple ${activeNoCumple}"
                onclick="setEstado(this, '${ctrl.id}', 'NO_CUMPLE')">No Cumple</button>
            </div>
          </div>
        </div>`;
    });
  });

  box.innerHTML = html;
  actualizarTodo();
}

// ─────────────────────────────────────────────────────────────────────────────
// SETEAR ESTADO DE UN CONTROL
// ─────────────────────────────────────────────────────────────────────────────
window.setEstado = async function(btn, controlId, estado) {
  const item = btn.closest('.checklist-item');
  item.dataset.estado = estado;
  estadosChecklist[controlId] = estado;

  const badge = document.getElementById('estado-' + controlId);
  const label = estado === 'CUMPLE' ? 'Cumple' : 'No Cumple';
  badge.className = 'badge-estado ' + (estado === 'CUMPLE' ? 'cumple' : 'no-cumple');
  badge.textContent = label;

  item.querySelectorAll('.btn-estado').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  actualizarTodo();

  try {
    const observacion = estado === 'NO_CUMPLE' ? 'Marcado como No Cumple por auditor.' : null;
    await guardarEstadoBackend(controlId, estado, observacion);
  } catch (error) {
    console.error(error);
    alert(error.message || 'No se pudo guardar el estado del control.');
  }
};

// ─────────────────────────────────────────────────────────────────────────────
// FILTROS
// ─────────────────────────────────────────────────────────────────────────────
window.filtrarItems = function(filtro, btn) {
  document.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');

  document.querySelectorAll('.checklist-item').forEach(item => {
    const estado = item.dataset.estado || 'PENDIENTE';
    const visible = filtro === 'todos'
      || (filtro === 'cumple'     && estado === 'CUMPLE')
      || (filtro === 'no-cumple'  && estado === 'NO_CUMPLE')
      || (filtro === 'pendiente'  && estado === 'PENDIENTE');
    item.style.display = visible ? '' : 'none';
  });
};

// ─────────────────────────────────────────────────────────────────────────────
// CONTADORES Y PORCENTAJE — se actualiza en Dashboard y Procesos
// ─────────────────────────────────────────────────────────────────────────────
function actualizarTodo() {
  // Contar todos los controles definidos en ANEXOS_ISO
  let cumple = 0, noCumple = 0, pendiente = 0;
  const total = ANEXOS_ISO.reduce((sum, a) => sum + a.controles.length, 0);

  ANEXOS_ISO.forEach(anexo => {
    anexo.controles.forEach(ctrl => {
      const est = estadosChecklist[ctrl.id] || 'PENDIENTE';
      if (est === 'CUMPLE') cumple++;
      else if (est === 'NO_CUMPLE') noCumple++;
      else pendiente++;
    });
  });

  const respondidos = cumple + noCumple;
  const porcentaje = total > 0 ? Math.round((respondidos / total) * 100) : 0;
  const porcentajeCumple = total > 0 ? Math.round((cumple / total) * 100) : 0;

  // Dashboard
  setTexto('totalCumple', cumple);
  setTexto('totalNoCumple', noCumple);
  setTexto('totalPendiente', pendiente);

  // Procesos
  setTexto('procesoCumple', cumple);
  setTexto('procesoNoCumple', noCumple);
  setTexto('procesoPendiente', pendiente);
  setTexto('procesoPorcentaje', porcentaje + '%');
  setTexto('procesoPorcentajeBarra', porcentaje + '%');
  const barra = document.getElementById('barraProceso');
  if (barra) barra.style.width = porcentaje + '%';
}

function setTexto(id, valor) {
  const el = document.getElementById(id);
  if (el) el.textContent = valor;
}

// ─────────────────────────────────────────────────────────────────────────────
// FIRMAR AUDITORÍA — usa servicioId real de la empresa asignada
// ─────────────────────────────────────────────────────────────────────────────
window.firmarAuditoria = async function() {
  if (!empresaActual) {
    alert('No tienes una empresa asignada para firmar.');
    return;
  }

  const pendientes = Object.values(estadosChecklist).filter(e => e === 'PENDIENTE').length;
  const totalControles = ANEXOS_ISO.reduce((s, a) => s + a.controles.length, 0);
  const sinEvaluar = totalControles - Object.keys(estadosChecklist).length;
  const totalPendientes = pendientes + sinEvaluar;

  if (totalPendientes > 0) {
    const confirmar = confirm(
      `Aún tienes ${totalPendientes} control(es) sin evaluar. ¿Deseas firmar de todas formas?`
    );
    if (!confirmar) return;
  }

  const token = localStorage.getItem('token');
  const nombre = localStorage.getItem('nombre') || 'Auditor';

  if (!empresaActual.servicioId) {
    alert('No se encontró un servicio activo para esta empresa. Contacta al administrador.');
    return;
  }

  try {
    const res = await fetch(`${API_URL}/firmas`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        nombreFirmante: nombre,
        cargo: 'Auditor',
        estado: 'FIRMADA',
        servicioId: empresaActual.servicioId
      })
    });

    if (res.ok) {
      alert(`✅ Auditoría firmada correctamente para ${empresaActual.nombre}`);
    } else {
      const errBody = await res.text();
      console.error('Error respuesta firma:', errBody);
      alert('Error al firmar la auditoría. Revisa la consola.');
    }
  } catch (e) {
    console.error(e);
    alert('Error de conexión con el servidor.');
  }
};