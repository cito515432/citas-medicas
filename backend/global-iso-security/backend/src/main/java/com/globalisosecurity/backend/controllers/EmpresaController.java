/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.Empresa;
import com.globalisosecurity.backend.services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public List<Empresa> obtenerTodas() {
        return empresaService.obtenerTodas();
    }

    @GetMapping("/asignadas")
    public ResponseEntity<List<Empresa>> obtenerAsignadas() {
        return ResponseEntity.ok(empresaService.obtenerEmpresasAsignadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Empresa> empresa = empresaService.obtenerPorId(id);
        if (empresa.isPresent()) {
            return ResponseEntity.ok(empresa.get());
        }
        return ResponseEntity.status(404).body("Empresa no encontrada");
    }

    @PostMapping
    public ResponseEntity<?> crearEmpresa(@RequestBody Empresa empresa) {
        Empresa nueva = empresaService.crearEmpresa(empresa);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEmpresa(@PathVariable Long id, @RequestBody Empresa empresa) {
        Empresa actualizada = empresaService.actualizarEmpresa(id, empresa);
        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        }
        return ResponseEntity.status(404).body("Empresa no encontrada");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return ResponseEntity.ok("Empresa eliminada correctamente");
    }
}