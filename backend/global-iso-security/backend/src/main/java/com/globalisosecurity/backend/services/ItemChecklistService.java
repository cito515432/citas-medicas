/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Checklist;
import com.globalisosecurity.backend.models.ItemChecklist;
import com.globalisosecurity.backend.repositories.ChecklistRepository;
import com.globalisosecurity.backend.repositories.ItemChecklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.globalisosecurity.backend.dto.EvaluarItemRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ItemChecklistService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE",
            "CUMPLE",
            "NO_CUMPLE",
            "EN_PROCESO"
    );

    @Autowired
    private ItemChecklistRepository itemChecklistRepository;

    @Autowired
    private ChecklistRepository checklistRepository;

    public List<ItemChecklist> obtenerTodos() {
        return itemChecklistRepository.findAll();
    }

    public Optional<ItemChecklist> obtenerPorId(Long id) {
        return itemChecklistRepository.findById(id);
    }

    public List<ItemChecklist> obtenerPorEstado(String estado) {
        return itemChecklistRepository.findByEstado(normalizarEstado(estado));
    }

    public List<ItemChecklist> obtenerPorChecklist(Long checklistId) {
        return itemChecklistRepository.findByChecklistId(checklistId);
    }
public ItemChecklist evaluarItem(Long itemId, EvaluarItemRequest request) {
    ItemChecklist item = itemChecklistRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));

    if (request == null) {
        throw new BadRequestException("El body de la evaluación es obligatorio");
    }

    if (request.getEstado() == null || request.getEstado().trim().isEmpty()) {
        throw new BadRequestException("El estado es obligatorio");
    }

    String estado = normalizarEstado(request.getEstado());
    validarEstado(estado);

    String observacion = request.getObservacion() != null ? request.getObservacion().trim() : null;

    if ((estado.equals("NO_CUMPLE") || estado.equals("EN_PROCESO"))
            && (observacion == null || observacion.isEmpty())) {
        throw new BadRequestException("La observación es obligatoria para NO_CUMPLE o EN_PROCESO");
    }

    if (estado.equals("CUMPLE") || estado.equals("PENDIENTE")) {
        observacion = null;
    }

    item.setEstado(estado);
    item.setObservacion(observacion);

    return itemChecklistRepository.save(item);
}
    public ItemChecklist crearItem(ItemChecklist item) {
        validarItem(item);

        String estado = item.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = "PENDIENTE";
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Checklist checklist = checklistRepository.findById(item.getChecklist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Checklist no encontrado"));

        item.setPregunta(item.getPregunta().trim());
        item.setRespuesta(item.getRespuesta() != null ? item.getRespuesta().trim() : null);
        item.setObservacion(item.getObservacion() != null ? item.getObservacion().trim() : null);
        item.setEstado(estado);
        item.setChecklist(checklist);

        return itemChecklistRepository.save(item);
    }

    public ItemChecklist actualizarItem(Long id, ItemChecklist itemActualizado) {
        ItemChecklist itemExistente = itemChecklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));

        validarItem(itemActualizado);

        String estado = itemActualizado.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            throw new BadRequestException("El estado del ítem es obligatorio");
        }

        estado = normalizarEstado(estado);
        validarEstado(estado);

        Checklist checklist = checklistRepository.findById(itemActualizado.getChecklist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Checklist no encontrado"));

        itemExistente.setPregunta(itemActualizado.getPregunta().trim());
        itemExistente.setRespuesta(
                itemActualizado.getRespuesta() != null ? itemActualizado.getRespuesta().trim() : null
        );
        itemExistente.setObservacion(
                itemActualizado.getObservacion() != null ? itemActualizado.getObservacion().trim() : null
        );
        itemExistente.setEstado(estado);
        itemExistente.setChecklist(checklist);

        return itemChecklistRepository.save(itemExistente);
    }

    public void eliminarItem(Long id) {
        if (!itemChecklistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ítem no encontrado");
        }

        itemChecklistRepository.deleteById(id);
    }

    private void validarItem(ItemChecklist item) {
        if (item.getPregunta() == null || item.getPregunta().trim().isEmpty()) {
            throw new BadRequestException("La pregunta del ítem es obligatoria");
        }

        if (item.getChecklist() == null || item.getChecklist().getId() == null) {
            throw new BadRequestException("El checklist es obligatorio");
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException(
                    "Estado no válido. Use: PENDIENTE, CUMPLE, NO_CUMPLE o EN_PROCESO"
            );
        }
    }

    private String normalizarEstado(String estado) {
        return estado.trim().toUpperCase();
    }
}
