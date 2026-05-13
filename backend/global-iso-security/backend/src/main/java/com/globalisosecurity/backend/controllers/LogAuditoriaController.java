/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.LogAuditoria;
import com.globalisosecurity.backend.services.LogAuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs-auditoria")
@CrossOrigin(origins = "*")
public class LogAuditoriaController {

    @Autowired
    private LogAuditoriaService logAuditoriaService;

    @GetMapping
    public List<LogAuditoria> obtenerTodos() {
        return logAuditoriaService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<LogAuditoria> log = logAuditoriaService.obtenerPorId(id);
        if (log.isPresent()) {
            return ResponseEntity.ok(log.get());
        }
        return ResponseEntity.status(404).body("Log de auditoría no encontrado");
    }

    @GetMapping("/modulo/{modulo}")
    public List<LogAuditoria> obtenerPorModulo(@PathVariable String modulo) {
        return logAuditoriaService.obtenerPorModulo(modulo);
    }

    @GetMapping("/usuario/{usuario}")
    public List<LogAuditoria> obtenerPorUsuario(@PathVariable String usuario) {
        return logAuditoriaService.obtenerPorUsuario(usuario);
    }

    @GetMapping("/accion/{accion}")
    public List<LogAuditoria> obtenerPorAccion(@PathVariable String accion) {
        return logAuditoriaService.obtenerPorAccion(accion);
    }

    @PostMapping
    public ResponseEntity<?> crearLog(@RequestBody LogAuditoria log) {
        LogAuditoria nuevo = logAuditoriaService.crearLog(log);
        return ResponseEntity.ok(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLog(@PathVariable Long id) {
        logAuditoriaService.eliminarLog(id);
        return ResponseEntity.ok("Log de auditoría eliminado correctamente");
    }
}