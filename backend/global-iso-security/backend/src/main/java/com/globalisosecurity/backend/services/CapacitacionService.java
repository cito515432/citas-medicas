/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Capacitacion;
import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.repositories.CapacitacionRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CapacitacionService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE",
            "EN_PROCESO",
            "COMPLETADA"
    );

    @Autowired
    private CapacitacionRepository capacitacionRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Capacitacion> obtenerTodas() {
        return capacitacionRepository.findAll();
    }

    public Optional<Capacitacion> obtenerPorId(Long id) {
        return capacitacionRepository.findById(id);
    }

    public List<Capacitacion> obtenerPorEstado(String estado) {
        return capacitacionRepository.findByEstado(normalizarEstado(estado));
    }

    public List<Capacitacion> obtenerPorServicio(Long servicioId) {
        return capacitacionRepository.findByServicioId(servicioId);
    }

    public List<Capacitacion> obtenerPorEmpresa(Long empresaId) {
        return capacitacionRepository.findByServicioEmpresaId(empresaId);
    }

    public Capacitacion crearCapacitacion(Capacitacion capacitacion) {
        validarCapacitacion(capacitacion);

        String estado = capacitacion.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = "PENDIENTE";
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Servicio servicio = servicioRepository.findById(capacitacion.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        capacitacion.setTitulo(capacitacion.getTitulo().trim());
        capacitacion.setDescripcion(
                capacitacion.getDescripcion() != null ? capacitacion.getDescripcion().trim() : null
        );
        capacitacion.setMaterialUrl(
                capacitacion.getMaterialUrl() != null ? capacitacion.getMaterialUrl().trim() : null
        );
        capacitacion.setVideoUrl(
                capacitacion.getVideoUrl() != null ? capacitacion.getVideoUrl().trim() : null
        );
        capacitacion.setEstado(estado);
        capacitacion.setServicio(servicio);

        if (estado.equals("COMPLETADA") && capacitacion.getFechaFinalizacion() == null) {
            capacitacion.setFechaFinalizacion(LocalDateTime.now());
        }

        return capacitacionRepository.save(capacitacion);
    }

    public Capacitacion actualizarCapacitacion(Long id, Capacitacion capacitacionActualizada) {
        Capacitacion existente = capacitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capacitación no encontrada"));

        validarCapacitacion(capacitacionActualizada);

        String estado = capacitacionActualizada.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            throw new BadRequestException("El estado de la capacitación es obligatorio");
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Servicio servicio = servicioRepository.findById(capacitacionActualizada.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        existente.setTitulo(capacitacionActualizada.getTitulo().trim());
        existente.setDescripcion(
                capacitacionActualizada.getDescripcion() != null
                        ? capacitacionActualizada.getDescripcion().trim()
                        : null
        );
        existente.setMaterialUrl(
                capacitacionActualizada.getMaterialUrl() != null
                        ? capacitacionActualizada.getMaterialUrl().trim()
                        : null
        );
        existente.setVideoUrl(
                capacitacionActualizada.getVideoUrl() != null
                        ? capacitacionActualizada.getVideoUrl().trim()
                        : null
        );
        existente.setEstado(estado);
        existente.setServicio(servicio);

        if (estado.equals("COMPLETADA") && existente.getFechaFinalizacion() == null) {
            existente.setFechaFinalizacion(LocalDateTime.now());
        }

        return capacitacionRepository.save(existente);
    }

    public void eliminarCapacitacion(Long id) {
        if (!capacitacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Capacitación no encontrada");
        }

        capacitacionRepository.deleteById(id);
    }

    private void validarCapacitacion(Capacitacion capacitacion) {
        if (capacitacion == null) {
            throw new BadRequestException("El body de la capacitación es obligatorio");
        }

        if (capacitacion.getTitulo() == null || capacitacion.getTitulo().trim().isEmpty()) {
            throw new BadRequestException("El título de la capacitación es obligatorio");
        }

        if (capacitacion.getServicio() == null || capacitacion.getServicio().getId() == null) {
            throw new BadRequestException("El servicio es obligatorio");
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException(
                    "Estado no válido. Use: PENDIENTE, EN_PROCESO o COMPLETADA"
            );
        }
    }

    private String normalizarEstado(String estado) {
        return estado.trim().toUpperCase();
    }
}