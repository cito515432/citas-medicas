/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.Capacitacion;
import com.globalisosecurity.backend.services.CapacitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/capacitaciones")
public class CapacitacionController {

    @Autowired
    private CapacitacionService capacitacionService;

    @GetMapping
    public List<Capacitacion> obtenerTodas() {
        return capacitacionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Capacitacion> capacitacion = capacitacionService.obtenerPorId(id);
        if (capacitacion.isPresent()) {
            return ResponseEntity.ok(capacitacion.get());
        }
        return ResponseEntity.status(404).body("Capacitación no encontrada");
    }

    @GetMapping("/estado/{estado}")
    public List<Capacitacion> obtenerPorEstado(@PathVariable String estado) {
        return capacitacionService.obtenerPorEstado(estado);
    }

    @GetMapping("/servicio/{servicioId}")
    public List<Capacitacion> obtenerPorServicio(@PathVariable Long servicioId) {
        return capacitacionService.obtenerPorServicio(servicioId);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Capacitacion> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return capacitacionService.obtenerPorEmpresa(empresaId);
    }

    @PostMapping
    public ResponseEntity<?> crearCapacitacion(@RequestBody Capacitacion capacitacion) {
        Capacitacion nueva = capacitacionService.crearCapacitacion(capacitacion);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCapacitacion(@PathVariable Long id, @RequestBody Capacitacion capacitacion) {
        Capacitacion actualizada = capacitacionService.actualizarCapacitacion(id, capacitacion);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCapacitacion(@PathVariable Long id) {
        capacitacionService.eliminarCapacitacion(id);
        return ResponseEntity.ok("Capacitación eliminada correctamente");
    }
}