/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.dto.ChecklistCompletoResponse;
import com.globalisosecurity.backend.models.Checklist;
import com.globalisosecurity.backend.services.ChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globalisosecurity.backend.models.ItemChecklist;
import com.globalisosecurity.backend.services.ItemChecklistService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {

    @Autowired
    private ChecklistService checklistService;
    @Autowired
private ItemChecklistService itemChecklistService;
    
    @GetMapping
    public List<Checklist> obtenerTodos() {
        return checklistService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Checklist> checklist = checklistService.obtenerPorId(id);
        if (checklist.isPresent()) {
            return ResponseEntity.ok(checklist.get());
        }
        return ResponseEntity.status(404).body("Checklist no encontrado");
    }

    @GetMapping("/estado/{estado}")
    public List<Checklist> obtenerPorEstado(@PathVariable String estado) {
        return checklistService.obtenerPorEstado(estado);
    }

    @GetMapping("/servicio/{servicioId}")
    public List<Checklist> obtenerPorServicio(@PathVariable Long servicioId) {
        return checklistService.obtenerPorServicio(servicioId);
    }
@GetMapping("/{checklistId}/items")
public ResponseEntity<List<ItemChecklist>> obtenerItemsPorChecklist(@PathVariable Long checklistId) {
    return ResponseEntity.ok(itemChecklistService.obtenerPorChecklist(checklistId));
}
    @GetMapping("/servicio/{servicioId}/completo")
    public ResponseEntity<ChecklistCompletoResponse> obtenerChecklistCompletoPorServicio(
            @PathVariable Long servicioId) {
        return ResponseEntity.ok(checklistService.obtenerChecklistCompletoPorServicio(servicioId));
    }

    @PostMapping
    public ResponseEntity<?> crearChecklist(@RequestBody Checklist checklist) {
        Checklist nuevo = checklistService.crearChecklist(checklist);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarChecklist(@PathVariable Long id, @RequestBody Checklist checklist) {
        Checklist actualizado = checklistService.actualizarChecklist(id, checklist);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarChecklist(@PathVariable Long id) {
        checklistService.eliminarChecklist(id);
        return ResponseEntity.ok("Checklist eliminado correctamente");
    }
}
