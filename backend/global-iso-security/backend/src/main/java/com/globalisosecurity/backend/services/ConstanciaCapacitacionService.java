/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Capacitacion;
import com.globalisosecurity.backend.models.ConstanciaCapacitacion;
import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.repositories.CapacitacionRepository;
import com.globalisosecurity.backend.repositories.ConstanciaCapacitacionRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConstanciaCapacitacionService {

    @Autowired
    private ConstanciaCapacitacionRepository constanciaRepository;

    @Autowired
    private CapacitacionRepository capacitacionRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<ConstanciaCapacitacion> obtenerTodas() {
        return constanciaRepository.findAll();
    }

    public Optional<ConstanciaCapacitacion> obtenerPorId(Long id) {
        return constanciaRepository.findById(id);
    }

    public List<ConstanciaCapacitacion> obtenerPorServicio(Long servicioId) {
        return constanciaRepository.findByServicioId(servicioId);
    }

    public List<ConstanciaCapacitacion> obtenerPorCapacitacion(Long capacitacionId) {
        return constanciaRepository.findByCapacitacionId(capacitacionId);
    }

    public List<ConstanciaCapacitacion> obtenerPorDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new BadRequestException("El documento es obligatorio");
        }
        return constanciaRepository.findByDocumento(documento.trim());
    }

    public ConstanciaCapacitacion crearConstancia(ConstanciaCapacitacion constancia) {
        validarConstancia(constancia);

        Capacitacion capacitacion = capacitacionRepository.findById(constancia.getCapacitacion().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Capacitación no encontrada"));

        Servicio servicio = servicioRepository.findById(constancia.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        constancia.setNombreCompleto(constancia.getNombreCompleto().trim());
        constancia.setDocumento(constancia.getDocumento().trim());
        constancia.setCodigoInterno(
                constancia.getCodigoInterno() != null ? constancia.getCodigoInterno().trim() : null
        );
        constancia.setCargo(constancia.getCargo().trim());
        constancia.setCapacitacion(capacitacion);
        constancia.setServicio(servicio);

        if (constancia.getFechaFirma() == null) {
            constancia.setFechaFirma(LocalDateTime.now());
        }

        return constanciaRepository.save(constancia);
    }

    public ConstanciaCapacitacion actualizarConstancia(Long id, ConstanciaCapacitacion constanciaActualizada) {
        ConstanciaCapacitacion existente = constanciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Constancia de capacitación no encontrada"));

        validarConstancia(constanciaActualizada);

        Capacitacion capacitacion = capacitacionRepository.findById(constanciaActualizada.getCapacitacion().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Capacitación no encontrada"));

        Servicio servicio = servicioRepository.findById(constanciaActualizada.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        existente.setNombreCompleto(constanciaActualizada.getNombreCompleto().trim());
        existente.setDocumento(constanciaActualizada.getDocumento().trim());
        existente.setCodigoInterno(
                constanciaActualizada.getCodigoInterno() != null
                        ? constanciaActualizada.getCodigoInterno().trim()
                        : null
        );
        existente.setCargo(constanciaActualizada.getCargo().trim());
        existente.setCapacitacion(capacitacion);
        existente.setServicio(servicio);

        return constanciaRepository.save(existente);
    }

    public void eliminarConstancia(Long id) {
        if (!constanciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Constancia de capacitación no encontrada");
        }

        constanciaRepository.deleteById(id);
    }

    private void validarConstancia(ConstanciaCapacitacion constancia) {
        if (constancia.getNombreCompleto() == null || constancia.getNombreCompleto().trim().isEmpty()) {
            throw new BadRequestException("El nombre completo es obligatorio");
        }

        if (constancia.getDocumento() == null || constancia.getDocumento().trim().isEmpty()) {
            throw new BadRequestException("El documento es obligatorio");
        }

        if (constancia.getCargo() == null || constancia.getCargo().trim().isEmpty()) {
            throw new BadRequestException("El cargo es obligatorio");
        }

        if (constancia.getCapacitacion() == null || constancia.getCapacitacion().getId() == null) {
            throw new BadRequestException("La capacitación es obligatoria");
        }

        if (constancia.getServicio() == null || constancia.getServicio().getId() == null) {
            throw new BadRequestException("El servicio es obligatorio");
        }
    }
}
