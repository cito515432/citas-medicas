/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Empresa;
import com.globalisosecurity.backend.models.Sector;
import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.repositories.EmpresaRepository;
import com.globalisosecurity.backend.repositories.SectorRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.globalisosecurity.backend.dto.ServicioResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.globalisosecurity.backend.models.Checklist;
import com.globalisosecurity.backend.models.Usuario;
import com.globalisosecurity.backend.repositories.ChecklistRepository;
import com.globalisosecurity.backend.repositories.UsuarioRepository;
import com.globalisosecurity.backend.utils.SecurityUtils;
import com.globalisosecurity.backend.models.ItemChecklist;
import com.globalisosecurity.backend.repositories.ItemChecklistRepository;
import java.util.HashMap;
import java.util.Map;
import com.globalisosecurity.backend.models.Firma;
import com.globalisosecurity.backend.repositories.FirmaRepository;


@Service
public class ServicioService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "BORRADOR",
            "EN_PROCESO",
            "FINALIZADO",
            "FIRMADO",
            "CERRADO"
    );
@Autowired
private UsuarioRepository usuarioRepository;
@Autowired
private ItemChecklistRepository itemChecklistRepository;
@Autowired
private ChecklistRepository checklistRepository;
@Autowired
private FirmaRepository firmaRepository;
    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private LogAuditoriaService logAuditoriaService;

    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    public Optional<Servicio> obtenerPorId(Long id) {
        return servicioRepository.findById(id);
    }

    public List<Servicio> obtenerPorEstado(String estado) {
        return servicioRepository.findByEstado(normalizarEstado(estado));
    }
public List<ServicioResponseDTO> listarServiciosDTO(Long empresaId) {
    List<Servicio> servicios;

    if (empresaId != null) {
        servicios = servicioRepository.findByEmpresaId(empresaId);
    } else {
        servicios = servicioRepository.findAll();
    }

    return servicios.stream()
            .map(this::mapServicio)
            .toList();
}
    public List<Servicio> obtenerPorEmpresa(Long empresaId) {
        return servicioRepository.findByEmpresaId(empresaId);
    }

    @Transactional
    public Servicio crearServicio(Servicio servicio) {
        validarEmpresaYSector(servicio);

        if (servicio.getFechaCreacion() == null) {
            servicio.setFechaCreacion(LocalDateTime.now());
        }

        String estado = servicio.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = "BORRADOR";
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);
        servicio.setEstado(estado);

        Empresa empresa = empresaRepository.findById(servicio.getEmpresa().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        Sector sector = sectorRepository.findById(servicio.getSector().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado"));

        servicio.setEmpresa(empresa);
        servicio.setSector(sector);

        Servicio nuevo = servicioRepository.save(servicio);

        crearChecklistInicialPorSector(nuevo);

        logAuditoriaService.registrarLog(
                "CREAR",
                "SERVICIOS",
                "Se creó el servicio con ID: " + nuevo.getId() + " en estado " + nuevo.getEstado()
        );

        return nuevo;
    }

    public Servicio actualizarServicio(Long id, Servicio servicioActualizado) {
        Servicio servicioExistente = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        if (estaBloqueado(servicioExistente.getEstado())) {
            throw new BadRequestException("No se puede modificar un servicio en estado " + servicioExistente.getEstado());
        }

        validarEmpresaYSector(servicioActualizado);

        String estado = servicioActualizado.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            throw new BadRequestException("El estado del servicio es obligatorio");
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Empresa empresa = empresaRepository.findById(servicioActualizado.getEmpresa().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        Sector sector = sectorRepository.findById(servicioActualizado.getSector().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sector no encontrado"));

        servicioExistente.setEmpresa(empresa);
        servicioExistente.setSector(sector);
        servicioExistente.setEstado(estado);

        Servicio actualizado = servicioRepository.save(servicioExistente);

        logAuditoriaService.registrarLog(
                "ACTUALIZAR",
                "SERVICIOS",
                "Se actualizó el servicio con ID: " + actualizado.getId() + " al estado " + actualizado.getEstado()
        );

        return actualizado;
    }

    public void eliminarServicio(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        if (estaBloqueado(servicio.getEstado())) {
            throw new BadRequestException("No se puede eliminar un servicio en estado " + servicio.getEstado());
        }

        servicioRepository.delete(servicio);

        logAuditoriaService.registrarLog(
                "ELIMINAR",
                "SERVICIOS",
                "Se eliminó el servicio con ID: " + servicio.getId() + " en estado " + servicio.getEstado()
        );
    }
private ServicioResponseDTO mapServicio(Servicio servicio) {
    ServicioResponseDTO dto = new ServicioResponseDTO();
    dto.setId(servicio.getId());
    dto.setEstado(servicio.getEstado());
    dto.setFechaCreacion(servicio.getFechaCreacion());

    if (servicio.getEmpresa() != null) {
        dto.setEmpresa(new ServicioResponseDTO.SimpleRef(
                servicio.getEmpresa().getId(),
                servicio.getEmpresa().getNombre()
        ));
    } else {
        dto.setEmpresa(null);
    }

    if (servicio.getSector() != null) {
        dto.setSector(new ServicioResponseDTO.SimpleRef(
                servicio.getSector().getId(),
                servicio.getSector().getNombre()
        ));
    } else {
        dto.setSector(null);
    }

    return dto;
}
public Map<String, Object> obtenerMiServicio() {
    String email = SecurityUtils.getUsuarioActual();

    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

    if (usuario.getEmpresa() == null) {
        throw new BadRequestException("El usuario no tiene empresa asignada");
    }

    List<Servicio> servicios = servicioRepository.findByEmpresaId(usuario.getEmpresa().getId());
    if (servicios.isEmpty()) {
        throw new ResourceNotFoundException("No se encontró servicio para la empresa del usuario");
    }

    Servicio servicio = servicios.get(0);

    List<Checklist> checklists = checklistRepository.findByServicioId(servicio.getId());
    if (checklists.isEmpty()) {
        throw new ResourceNotFoundException("No se encontró checklist para el servicio");
    }

    Checklist checklist = checklists.get(0);

    Map<String, Object> response = new HashMap<>();
    response.put("servicioId", servicio.getId());
    response.put("estado", servicio.getEstado());
    response.put("empresaNombre", servicio.getEmpresa() != null ? servicio.getEmpresa().getNombre() : null);
    response.put("sectorNombre", servicio.getSector() != null ? servicio.getSector().getNombre() : null);
    response.put("checklistId", checklist.getId());

    return response;
}
public Map<String, Object> obtenerResumen(Long servicioId) {
    Servicio servicio = servicioRepository.findById(servicioId)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    List<Checklist> checklists = checklistRepository.findByServicioId(servicio.getId());
    if (checklists.isEmpty()) {
        throw new ResourceNotFoundException("No se encontró checklist para el servicio");
    }

    Checklist checklist = checklists.get(0);

    List<ItemChecklist> items = itemChecklistRepository.findByChecklistId(checklist.getId());

    int total = items.size();
    int cumple = 0;
    int noCumple = 0;
    int enProceso = 0;
    int pendiente = 0;

    for (ItemChecklist item : items) {
        String estado = item.getEstado() != null ? item.getEstado().trim().toUpperCase() : "PENDIENTE";

        switch (estado) {
            case "CUMPLE" -> cumple++;
            case "NO_CUMPLE" -> noCumple++;
            case "EN_PROCESO" -> enProceso++;
            default -> pendiente++;
        }
    }

    int porcentajeCumplimiento = total == 0 ? 0 : Math.round((cumple * 100.0f) / total);

    Map<String, Object> response = new HashMap<>();
    response.put("total", total);
    response.put("cumple", cumple);
    response.put("noCumple", noCumple);
    response.put("enProceso", enProceso);
    response.put("pendiente", pendiente);
    response.put("porcentajeCumplimiento", porcentajeCumplimiento);

    return response;
}

public Map<String, Object> obtenerEstadoCompleto(Long servicioId) {
    Servicio servicio = servicioRepository.findById(servicioId)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    List<Firma> firmas = firmaRepository.findByServicioId(servicioId);

    List<Map<String, Object>> firmasResponse = firmas.stream().map(firma -> {
        Map<String, Object> firmaMap = new HashMap<>();
        firmaMap.put("cargo", firma.getCargo());
        firmaMap.put("estado", firma.getEstado());
        firmaMap.put("fechaFirma", firma.getFechaFirma());
        return firmaMap;
    }).toList();

    Map<String, Object> response = new HashMap<>();
    response.put("servicioId", servicio.getId());
    response.put("estado", servicio.getEstado());
    response.put("firmas", firmasResponse);

    return response;
}

    private void crearChecklistInicialPorSector(Servicio servicio) {
        if (servicio == null || servicio.getId() == null) return;
        if (!checklistRepository.findByServicioId(servicio.getId()).isEmpty()) return;

        Checklist checklist = new Checklist();
        checklist.setNombre("Checklist ISO 27001 - " + servicio.getEmpresa().getNombre());
        checklist.setDescripcion("Checklist dinámico para sector " + servicio.getSector().getNombre());
        checklist.setEstado("PENDIENTE");
        checklist.setServicio(servicio);

        Checklist guardado = checklistRepository.save(checklist);

        for (String pregunta : controlesPorSector(servicio.getSector().getNombre())) {
            ItemChecklist item = new ItemChecklist();
            item.setPregunta(pregunta);
            item.setEstado("PENDIENTE");
            item.setChecklist(guardado);
            itemChecklistRepository.save(item);
        }
    }

    private List<String> controlesPorSector(String sectorNombre) {
        String sector = sectorNombre == null ? "" : sectorNombre.trim().toUpperCase();

        List<String> base = List.of(
                "A.5.1 — Políticas de seguridad de la información: ¿Existe una política de seguridad aprobada, comunicada y revisada?",
                "A.5.2 — Roles y responsabilidades: ¿Están definidos los responsables de seguridad de la información?",
                "A.5.7 — Inteligencia de amenazas: ¿La organización identifica amenazas relevantes para su operación?",
                "A.5.15 — Control de acceso: ¿Existe una política formal de control de acceso basada en roles?",
                "A.5.23 — Seguridad en servicios en la nube: ¿Se evalúan proveedores cloud y sus responsabilidades de seguridad?",
                "A.6.3 — Concienciación y formación: ¿El personal recibe capacitación periódica en seguridad de la información?",
                "A.6.7 — Trabajo remoto: ¿Se protegen dispositivos y accesos usados fuera de las instalaciones?",
                "A.7.1 — Perímetros físicos: ¿Existen controles físicos para proteger áreas críticas?",
                "A.8.2 — Derechos de acceso privilegiado: ¿Los accesos administrativos están controlados y revisados?",
                "A.8.5 — Autenticación segura: ¿Se usa autenticación fuerte para sistemas críticos?",
                "A.8.9 — Gestión de configuración: ¿Los sistemas tienen configuraciones seguras documentadas?",
                "A.8.13 — Copias de seguridad: ¿Se realizan backups y pruebas de restauración?",
                "A.8.15 — Registro de eventos: ¿Se conservan logs de seguridad y actividad relevante?",
                "A.8.16 — Monitoreo: ¿Se monitorean eventos de seguridad para detectar incidentes?",
                "A.8.24 — Uso de criptografía: ¿La información sensible se protege con cifrado adecuado?"
        );

        if (sector.contains("SALUD")) {
            return unir(base, List.of(
                    "SALUD-01 — Historia clínica: ¿La información clínica tiene acceso restringido por rol asistencial o administrativo?",
                    "SALUD-02 — Datos sensibles de pacientes: ¿Se protegen diagnósticos, resultados y datos personales de pacientes?",
                    "SALUD-03 — Consentimiento y privacidad: ¿Se informa al paciente sobre el tratamiento de sus datos?",
                    "SALUD-04 — Sistemas clínicos: ¿Se controla el acceso a software médico, agenda y admisiones?",
                    "SALUD-05 — Trazabilidad clínica: ¿Se registra quién consulta, modifica o elimina información del paciente?",
                    "SALUD-06 — Interoperabilidad: ¿Se valida la seguridad al intercambiar información con laboratorios, EPS o terceros?",
                    "SALUD-07 — Disponibilidad asistencial: ¿Existen planes para mantener sistemas críticos durante contingencias?",
                    "SALUD-08 — Dispositivos médicos: ¿Los equipos conectados están inventariados y protegidos?"
            ));
        }

        if (sector.contains("EDUC")) {
            return unir(base, List.of(
                    "EDU-01 — Datos de estudiantes: ¿Se protegen calificaciones, matrículas, acudientes y expedientes académicos?",
                    "EDU-02 — Plataformas virtuales: ¿Se controlan accesos a LMS, aulas virtuales y repositorios académicos?",
                    "EDU-03 — Menores de edad: ¿Se aplican controles adicionales para datos de menores?",
                    "EDU-04 — Evaluaciones: ¿Se protege la integridad de exámenes, rúbricas y resultados?",
                    "EDU-05 — Investigación: ¿Se clasifican datos de proyectos, semilleros o investigaciones sensibles?",
                    "EDU-06 — Correo institucional: ¿Se aplican controles contra phishing y suplantación?",
                    "EDU-07 — Laboratorios y equipos compartidos: ¿Existen políticas para sesiones, usuarios y limpieza de datos?",
                    "EDU-08 — Publicación de información: ¿Se revisa que portales y documentos no expongan datos personales?"
            ));
        }

        if (sector.contains("FINAN")) {
            return unir(base, List.of(
                    "FIN-01 — Datos financieros: ¿Se clasifican y protegen cuentas, transacciones y soportes financieros?",
                    "FIN-02 — Prevención de fraude: ¿Existen controles para detectar operaciones inusuales o no autorizadas?",
                    "FIN-03 — Segregación de funciones: ¿Quien aprueba pagos es distinto de quien los registra o ejecuta?",
                    "FIN-04 — Conciliaciones: ¿Se revisan transacciones críticas y conciliaciones con trazabilidad?",
                    "FIN-05 — Accesos a banca y ERP: ¿Los sistemas financieros usan MFA y privilegios mínimos?",
                    "FIN-06 — Retención de registros: ¿Se conservan soportes y logs de operaciones por el tiempo requerido?",
                    "FIN-07 — Terceros financieros: ¿Se evalúa la seguridad de pasarelas de pago, bancos y proveedores?",
                    "FIN-08 — Continuidad financiera: ¿Existe plan para operar pagos y reportes críticos ante fallas?"
            ));
        }

        if (sector.contains("TECNO")) {
            return unir(base, List.of(
                    "TEC-01 — Desarrollo seguro: ¿Se aplican prácticas de codificación segura y revisión de código?",
                    "TEC-02 — Gestión de vulnerabilidades: ¿Se identifican, priorizan y corrigen vulnerabilidades técnicas?",
                    "TEC-03 — Ambientes separados: ¿Producción, pruebas y desarrollo están separados?",
                    "TEC-04 — Secretos y credenciales: ¿Las claves API, tokens y secretos se gestionan fuera del código fuente?",
                    "TEC-05 — CI/CD: ¿Los pipelines de despliegue tienen controles de aprobación y trazabilidad?",
                    "TEC-06 — Infraestructura cloud: ¿Se revisan configuraciones de red, permisos y almacenamiento cloud?",
                    "TEC-07 — Logs de aplicación: ¿Se registran eventos de autenticación, errores y cambios críticos?",
                    "TEC-08 — Respuesta a incidentes técnicos: ¿Existe procedimiento para caídas, fugas de datos y vulnerabilidades críticas?"
            ));
        }

        if (sector.contains("MANUFACT")) {
            return unir(base, List.of(
                    "MAN-01 — Sistemas de producción: ¿Se protegen sistemas usados en planta, inventario y operación?",
                    "MAN-02 — Continuidad operativa: ¿Existen procedimientos ante fallas de sistemas que afecten producción?",
                    "MAN-03 — Acceso a planta: ¿Se controla el acceso físico a áreas de producción y equipos críticos?",
                    "MAN-04 — Proveedores industriales: ¿Se evalúan terceros que acceden a información o sistemas productivos?",
                    "MAN-05 — Propiedad intelectual: ¿Se protegen diseños, fórmulas, planos o procesos de fabricación?",
                    "MAN-06 — Dispositivos OT/IoT: ¿Los dispositivos conectados en planta están inventariados y segmentados?",
                    "MAN-07 — Mantenimiento: ¿El mantenimiento de equipos y sistemas queda registrado y autorizado?",
                    "MAN-08 — Trazabilidad logística: ¿Se protege la información de pedidos, inventario, despachos y clientes?"
            ));
        }

        return base;
    }

    private List<String> unir(List<String> base, List<String> especificos) {
        return java.util.stream.Stream.concat(base.stream(), especificos.stream()).toList();
    }

    private void validarEmpresaYSector(Servicio servicio) {
        if (servicio == null) {
            throw new BadRequestException("El body del servicio es obligatorio");
        }

        if (servicio.getEmpresa() == null || servicio.getEmpresa().getId() == null) {
            throw new BadRequestException("La empresa es obligatoria");
        }

        if (servicio.getSector() == null || servicio.getSector().getId() == null) {
            throw new BadRequestException("El sector es obligatorio");
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException(
                    "Estado no válido. Use: BORRADOR, EN_PROCESO, FINALIZADO, FIRMADO o CERRADO"
            );
        }
    }

    private String normalizarEstado(String estado) {
        return estado.trim().toUpperCase();
    }

    private boolean estaBloqueado(String estado) {
        String estadoNormalizado = normalizarEstado(estado);
        return estadoNormalizado.equals("FIRMADO") || estadoNormalizado.equals("CERRADO");
    }
}