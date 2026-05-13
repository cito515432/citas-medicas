/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Firma;
import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.repositories.FirmaRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.globalisosecurity.backend.dto.FirmaCreateRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FirmaService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE",
            "FIRMADA",
            "RECHAZADA"
    );

    @Autowired
    private FirmaRepository firmaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Firma> obtenerTodas() {
        return firmaRepository.findAll();
    }

    public Optional<Firma> obtenerPorId(Long id) {
        return firmaRepository.findById(id);
    }

    public List<Firma> obtenerPorEstado(String estado) {
        return firmaRepository.findByEstado(normalizarEstado(estado));
    }

    public List<Firma> obtenerPorServicio(Long servicioId) {
        return firmaRepository.findByServicioId(servicioId);
    }

    public List<Firma> obtenerPorEmpresa(Long empresaId) {
        return firmaRepository.findByServicioEmpresaId(empresaId);
    }
public Firma crearFirma(FirmaCreateRequest request) {
    if (request == null) {
        throw new BadRequestException("El body de la firma es obligatorio");
    }

    if (request.getNombreFirmante() == null || request.getNombreFirmante().trim().isEmpty()) {
        throw new BadRequestException("El nombre del firmante es obligatorio");
    }

    if (request.getCargo() == null || request.getCargo().trim().isEmpty()) {
        throw new BadRequestException("El cargo es obligatorio");
    }

    if (request.getServicioId() == null) {
        throw new BadRequestException("El servicioId es obligatorio");
    }

    String estado = request.getEstado();
    if (estado == null || estado.trim().isEmpty()) {
        estado = "PENDIENTE";
    }

    estado = normalizarEstado(estado);
    validarEstado(estado);

    Servicio servicio = servicioRepository.findById(request.getServicioId())
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    Firma firma = new Firma();
    firma.setNombreFirmante(request.getNombreFirmante().trim());
    firma.setCargo(request.getCargo().trim());
    firma.setEstado(estado);
    firma.setFechaFirma(LocalDateTime.now());
    firma.setServicio(servicio);

    return firmaRepository.save(firma);
}
    public Firma crearFirma(Firma firma) {
        validarFirma(firma);

        if (firma.getFechaFirma() == null) {
            firma.setFechaFirma(LocalDateTime.now());
        }

        String estado = firma.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = "PENDIENTE";
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Servicio servicio = servicioRepository.findById(firma.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        firma.setNombreFirmante(firma.getNombreFirmante().trim());
        firma.setCargo(firma.getCargo().trim());
        firma.setEstado(estado);
        firma.setServicio(servicio);

        return firmaRepository.save(firma);
    }

    public Firma actualizarFirma(Long id, Firma firmaActualizada) {
        Firma firmaExistente = firmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Firma no encontrada"));

        validarFirma(firmaActualizada);

        String estado = firmaActualizada.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            throw new BadRequestException("El estado de la firma es obligatorio");
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Servicio servicio = servicioRepository.findById(firmaActualizada.getServicio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        firmaExistente.setNombreFirmante(firmaActualizada.getNombreFirmante().trim());
        firmaExistente.setCargo(firmaActualizada.getCargo().trim());
        firmaExistente.setEstado(estado);
        firmaExistente.setServicio(servicio);

        return firmaRepository.save(firmaExistente);
    }

    public void eliminarFirma(Long id) {
        if (!firmaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Firma no encontrada");
        }

        firmaRepository.deleteById(id);
    }

    private void validarFirma(Firma firma) {
        if (firma == null) {
            throw new BadRequestException("El body de la firma es obligatorio");
        }

        if (firma.getNombreFirmante() == null || firma.getNombreFirmante().trim().isEmpty()) {
            throw new BadRequestException("El nombre del firmante es obligatorio");
        }

        if (firma.getCargo() == null || firma.getCargo().trim().isEmpty()) {
            throw new BadRequestException("El cargo es obligatorio");
        }

        if (firma.getServicio() == null || firma.getServicio().getId() == null) {
            throw new BadRequestException("El servicio es obligatorio");
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException(
                    "Estado no válido. Use: PENDIENTE, FIRMADA o RECHAZADA"
            );
        }
    }

    private String normalizarEstado(String estado) {
        return estado.trim().toUpperCase();
    }
}