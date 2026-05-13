/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.ConstanciaCapacitacion;
import com.globalisosecurity.backend.services.ConstanciaCapacitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/constancias-capacitacion")
@CrossOrigin(origins = "*")
public class ConstanciaCapacitacionController {

    @Autowired
    private ConstanciaCapacitacionService constanciaService;

    @GetMapping
    public List<ConstanciaCapacitacion> obtenerTodas() {
        return constanciaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<ConstanciaCapacitacion> constancia = constanciaService.obtenerPorId(id);
        if (constancia.isPresent()) {
            return ResponseEntity.ok(constancia.get());
        }
        return ResponseEntity.status(404).body("Constancia de capacitación no encontrada");
    }

    @GetMapping("/servicio/{servicioId}")
    public List<ConstanciaCapacitacion> obtenerPorServicio(@PathVariable Long servicioId) {
        return constanciaService.obtenerPorServicio(servicioId);
    }

    @GetMapping("/capacitacion/{capacitacionId}")
    public List<ConstanciaCapacitacion> obtenerPorCapacitacion(@PathVariable Long capacitacionId) {
        return constanciaService.obtenerPorCapacitacion(capacitacionId);
    }

    @GetMapping("/documento/{documento}")
    public List<ConstanciaCapacitacion> obtenerPorDocumento(@PathVariable String documento) {
        return constanciaService.obtenerPorDocumento(documento);
    }

    @PostMapping
    public ResponseEntity<?> crearConstancia(@RequestBody ConstanciaCapacitacion constancia) {
        ConstanciaCapacitacion nueva = constanciaService.crearConstancia(constancia);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarConstancia(@PathVariable Long id,
                                                  @RequestBody ConstanciaCapacitacion constancia) {
        ConstanciaCapacitacion actualizada = constanciaService.actualizarConstancia(id, constancia);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarConstancia(@PathVariable Long id) {
        constanciaService.eliminarConstancia(id);
        return ResponseEntity.ok("Constancia de capacitación eliminada correctamente");
    }
}