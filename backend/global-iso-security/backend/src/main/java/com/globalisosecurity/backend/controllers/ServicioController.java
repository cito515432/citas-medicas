/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.services.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globalisosecurity.backend.dto.ServicioResponseDTO;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
public ResponseEntity<List<ServicioResponseDTO>> obtenerTodos(
        @RequestParam(required = false) Long empresaId) {
    return ResponseEntity.ok(servicioService.listarServiciosDTO(empresaId));
}
   @GetMapping("/mi-servicio")
public ResponseEntity<Map<String, Object>> obtenerMiServicio() {
    return ResponseEntity.ok(servicioService.obtenerMiServicio());
}

@GetMapping("/{servicioId}/resumen")
public ResponseEntity<Map<String, Object>> obtenerResumen(@PathVariable Long servicioId) {
    return ResponseEntity.ok(servicioService.obtenerResumen(servicioId));
}

@GetMapping("/{servicioId}/estado-completo")
public ResponseEntity<Map<String, Object>> obtenerEstadoCompleto(@PathVariable Long servicioId) {
    return ResponseEntity.ok(servicioService.obtenerEstadoCompleto(servicioId));
}

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Servicio> servicio = servicioService.obtenerPorId(id);
        if (servicio.isPresent()) {
            return ResponseEntity.ok(servicio.get());
        }
        return ResponseEntity.status(404).body("Servicio no encontrado");
    }

    @GetMapping("/estado/{estado}")
    public List<Servicio> obtenerPorEstado(@PathVariable String estado) {
        return servicioService.obtenerPorEstado(estado);
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Servicio> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return servicioService.obtenerPorEmpresa(empresaId);
    }

    @PostMapping
    public ResponseEntity<?> crearServicio(@RequestBody Servicio servicio) {
        Servicio nuevo = servicioService.crearServicio(servicio);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        Servicio actualizado = servicioService.actualizarServicio(id, servicio);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.ok("Servicio eliminado correctamente");
    }
}