/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.Sector;
import com.globalisosecurity.backend.services.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sectores")
@CrossOrigin(origins = "*")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @GetMapping
    public List<Sector> obtenerTodos() {
        return sectorService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Sector> sector = sectorService.obtenerPorId(id);
        if (sector.isPresent()) {
            return ResponseEntity.ok(sector.get());
        }
        return ResponseEntity.status(404).body("Sector no encontrado");
    }

    @PostMapping
    public ResponseEntity<?> crearSector(@RequestBody Sector sector) {
        Sector nuevo = sectorService.crearSector(sector);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSector(@PathVariable Long id, @RequestBody Sector sector) {
        Sector actualizado = sectorService.actualizarSector(id, sector);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.status(404).body("Sector no encontrado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSector(@PathVariable Long id) {
        sectorService.eliminarSector(id);
        return ResponseEntity.ok("Sector eliminado correctamente");
    }
}