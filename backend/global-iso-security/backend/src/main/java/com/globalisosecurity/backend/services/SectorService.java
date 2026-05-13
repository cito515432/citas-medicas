/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Sector;
import com.globalisosecurity.backend.repositories.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    public List<Sector> obtenerTodos() {
        return sectorRepository.findAll();
    }

    public Optional<Sector> obtenerPorId(Long id) {
        return sectorRepository.findById(id);
    }

    public Sector crearSector(Sector sector) {
        validarNombreSector(sector.getNombre());

        Sector existente = sectorRepository.findByNombre(sector.getNombre().trim());
        if (existente != null) {
            throw new BadRequestException("Ya existe un sector con ese nombre");
        }

        sector.setNombre(sector.getNombre().trim());
        return sectorRepository.save(sector);
    }

    public Sector actualizarSector(Long id, Sector sectorActualizado) {
        Optional<Sector> sectorOpt = sectorRepository.findById(id);

        if (sectorOpt.isEmpty()) {
            throw new ResourceNotFoundException("Sector no encontrado");
        }

        validarNombreSector(sectorActualizado.getNombre());

        Sector sectorConMismoNombre = sectorRepository.findByNombre(sectorActualizado.getNombre().trim());
        if (sectorConMismoNombre != null && !sectorConMismoNombre.getId().equals(id)) {
            throw new BadRequestException("Ya existe un sector con ese nombre");
        }

        Sector sector = sectorOpt.get();
        sector.setNombre(sectorActualizado.getNombre().trim());

        return sectorRepository.save(sector);
    }

    public void eliminarSector(Long id) {
        if (!sectorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sector no encontrado");
        }

        sectorRepository.deleteById(id);
    }

    private void validarNombreSector(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BadRequestException("El nombre del sector es obligatorio");
        }
    }
}