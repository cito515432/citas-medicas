/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.models.ItemChecklist;
import com.globalisosecurity.backend.services.ItemChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globalisosecurity.backend.dto.EvaluarItemRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items-checklist")
public class ItemChecklistController {

    @Autowired
    private ItemChecklistService itemChecklistService;

    @GetMapping
    public List<ItemChecklist> obtenerTodos() {
        return itemChecklistService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<ItemChecklist> item = itemChecklistService.obtenerPorId(id);
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        return ResponseEntity.status(404).body("Ítem no encontrado");
    }

    @GetMapping("/estado/{estado}")
    public List<ItemChecklist> obtenerPorEstado(@PathVariable String estado) {
        return itemChecklistService.obtenerPorEstado(estado);
    }

    @GetMapping("/checklist/{checklistId}")
    public List<ItemChecklist> obtenerPorChecklist(@PathVariable Long checklistId) {
        return itemChecklistService.obtenerPorChecklist(checklistId);
    }
@PutMapping("/{itemId}/evaluar")
public ResponseEntity<ItemChecklist> evaluarItem(
        @PathVariable Long itemId,
        @RequestBody EvaluarItemRequest request) {
    return ResponseEntity.ok(itemChecklistService.evaluarItem(itemId, request));
}
    @PostMapping
    public ResponseEntity<?> crearItem(@RequestBody ItemChecklist item) {
        ItemChecklist nuevo = itemChecklistService.crearItem(item);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarItem(@PathVariable Long id, @RequestBody ItemChecklist item) {
        ItemChecklist actualizado = itemChecklistService.actualizarItem(id, item);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarItem(@PathVariable Long id) {
        itemChecklistService.eliminarItem(id);
        return ResponseEntity.ok("Ítem eliminado correctamente");
    }
}
