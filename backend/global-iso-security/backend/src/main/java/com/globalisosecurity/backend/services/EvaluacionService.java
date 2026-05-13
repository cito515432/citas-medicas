/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Evaluacion;
import com.globalisosecurity.backend.models.ItemChecklist;
import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.models.Usuario;
import com.globalisosecurity.backend.repositories.EvaluacionRepository;
import com.globalisosecurity.backend.repositories.ItemChecklistRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import com.globalisosecurity.backend.repositories.UsuarioRepository;
import com.globalisosecurity.backend.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EvaluacionService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "CUMPLE",
            "NO_CUMPLE",
            "EN_PROCESO"
    );

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ItemChecklistRepository itemChecklistRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Evaluacion> obtenerTodas() {
        return evaluacionRepository.findAll();
    }

    public Optional<Evaluacion> obtenerPorId(Long id) {
        return evaluacionRepository.findById(id);
    }

    public List<Evaluacion> obtenerPorEstado(String estado) {
        return evaluacionRepository.findByEstado(normalizarEstado(estado));
    }

    public List<Evaluacion> obtenerPorServicio(Long servicioId) {
        return evaluacionRepository.findByServicioId(servicioId);
    }

    public List<Evaluacion> obtenerPorEmpresa(Long empresaId) {
        return evaluacionRepository.findByServicioEmpresaId(empresaId);
    }

    public Evaluacion crearEvaluacion(Evaluacion evaluacion) {
        validarEvaluacion(evaluacion);

        String emailUsuarioActual = SecurityUtils.getUsuarioActual();

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Servicio servicio = servicioRepository.findById(evaluacion.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        ItemChecklist itemChecklist = itemChecklistRepository.findById(evaluacion.getItemChecklist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ítem checklist no encontrado"));

        String estado = normalizarEstado(evaluacion.getEstado());
        validarEstado(estado);

        Optional<Evaluacion> existente = evaluacionRepository
                .findByServicioIdAndItemChecklistIdAndUsuarioId(
                        servicio.getId(),
                        itemChecklist.getId(),
                        usuario.getId()
                );

        Evaluacion entidad = existente.orElse(new Evaluacion());
        entidad.setServicio(servicio);
        entidad.setItemChecklist(itemChecklist);
        entidad.setUsuario(usuario);
        entidad.setEstado(estado);
        entidad.setObservacion(
                evaluacion.getObservacion() != null ? evaluacion.getObservacion().trim() : null
        );
        entidad.setFechaEvaluacion(LocalDateTime.now());

        return evaluacionRepository.save(entidad);
    }

    public Evaluacion actualizarEvaluacion(Long id, Evaluacion evaluacionActualizada) {
        Evaluacion evaluacionExistente = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        validarEvaluacion(evaluacionActualizada);

        Servicio servicio = servicioRepository.findById(evaluacionActualizada.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        ItemChecklist itemChecklist = itemChecklistRepository.findById(evaluacionActualizada.getItemChecklist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ítem checklist no encontrado"));

        String estado = normalizarEstado(evaluacionActualizada.getEstado());
        validarEstado(estado);

        evaluacionExistente.setServicio(servicio);
        evaluacionExistente.setItemChecklist(itemChecklist);
        evaluacionExistente.setEstado(estado);
        evaluacionExistente.setObservacion(
                evaluacionActualizada.getObservacion() != null
                        ? evaluacionActualizada.getObservacion().trim()
                        : null
        );
        evaluacionExistente.setFechaEvaluacion(LocalDateTime.now());

        return evaluacionRepository.save(evaluacionExistente);
    }

    public void eliminarEvaluacion(Long id) {
        if (!evaluacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evaluación no encontrada");
        }

        evaluacionRepository.deleteById(id);
    }

    private void validarEvaluacion(Evaluacion evaluacion) {
        if (evaluacion == null) {
            throw new BadRequestException("El body de la evaluación es obligatorio");
        }

        if (evaluacion.getServicio() == null || evaluacion.getServicio().getId() == null) {
            throw new BadRequestException("El servicio es obligatorio");
        }

        if (evaluacion.getItemChecklist() == null || evaluacion.getItemChecklist().getId() == null) {
            throw new BadRequestException("El itemChecklist es obligatorio");
        }

        if (evaluacion.getEstado() == null || evaluacion.getEstado().trim().isEmpty()) {
            throw new BadRequestException("El estado es obligatorio");
        }

        String estado = normalizarEstado(evaluacion.getEstado());
        if ((estado.equals("NO_CUMPLE") || estado.equals("EN_PROCESO"))
                && (evaluacion.getObservacion() == null || evaluacion.getObservacion().trim().isEmpty())) {
            throw new BadRequestException("La observación es obligatoria cuando el estado es NO_CUMPLE o EN_PROCESO");
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException(
                    "Estado no válido. Use: CUMPLE, NO_CUMPLE o EN_PROCESO"
            );
        }
    }

    private String normalizarEstado(String estado) {
        return estado.trim().toUpperCase();
    }
}