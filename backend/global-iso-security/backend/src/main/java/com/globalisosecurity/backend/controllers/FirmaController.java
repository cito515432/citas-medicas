/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.Firma;
import com.globalisosecurity.backend.services.FirmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globalisosecurity.backend.dto.FirmaCreateRequest;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/firmas")
public class FirmaController {

    @Autowired
    private FirmaService firmaService;

    @GetMapping
    public List<Firma> obtenerTodas() {
        return firmaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Firma> firma = firmaService.obtenerPorId(id);
        if (firma.isPresent()) {
            return ResponseEntity.ok(firma.get());
        }
        return ResponseEntity.status(404).body("Firma no encontrada");
    }

    @GetMapping("/estado/{estado}")
    public List<Firma> obtenerPorEstado(@PathVariable String estado) {
        return firmaService.obtenerPorEstado(estado);
    }

    @GetMapping("/servicio/{servicioId}")
    public List<Firma> obtenerPorServicio(@PathVariable Long servicioId) {
        return firmaService.obtenerPorServicio(servicioId);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Firma> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return firmaService.obtenerPorEmpresa(empresaId);
    }

    @PostMapping
public ResponseEntity<Firma> crearFirma(@RequestBody FirmaCreateRequest request) {
    Firma nueva = firmaService.crearFirma(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
}

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFirma(@PathVariable Long id, @RequestBody Firma firma) {
        Firma actualizada = firmaService.actualizarFirma(id, firma);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFirma(@PathVariable Long id) {
        firmaService.eliminarFirma(id);
        return ResponseEntity.ok("Firma eliminada correctamente");
    }
}