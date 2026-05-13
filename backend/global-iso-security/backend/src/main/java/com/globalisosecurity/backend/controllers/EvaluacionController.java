/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.Evaluacion;
import com.globalisosecurity.backend.services.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @GetMapping
    public List<Evaluacion> obtenerTodas() {
        return evaluacionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Evaluacion> evaluacion = evaluacionService.obtenerPorId(id);
        if (evaluacion.isPresent()) {
            return ResponseEntity.ok(evaluacion.get());
        }
        return ResponseEntity.status(404).body("Evaluación no encontrada");
    }

    @GetMapping("/estado/{estado}")
    public List<Evaluacion> obtenerPorEstado(@PathVariable String estado) {
        return evaluacionService.obtenerPorEstado(estado);
    }

    @GetMapping("/servicio/{servicioId}")
    public List<Evaluacion> obtenerPorServicio(@PathVariable Long servicioId) {
        return evaluacionService.obtenerPorServicio(servicioId);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Evaluacion> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return evaluacionService.obtenerPorEmpresa(empresaId);
    }

    @PostMapping
    public ResponseEntity<?> crearEvaluacion(@RequestBody Evaluacion evaluacion) {
        Evaluacion nueva = evaluacionService.crearEvaluacion(evaluacion);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEvaluacion(@PathVariable Long id, @RequestBody Evaluacion evaluacion) {
        Evaluacion actualizada = evaluacionService.actualizarEvaluacion(id, evaluacion);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvaluacion(@PathVariable Long id) {
        evaluacionService.eliminarEvaluacion(id);
        return ResponseEntity.ok("Evaluación eliminada correctamente");
    }
}