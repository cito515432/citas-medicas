/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.repositories;

import com.globalisosecurity.backend.models.ItemChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemChecklistRepository extends JpaRepository<ItemChecklist, Long> {
    List<ItemChecklist> findByEstado(String estado);
    List<ItemChecklist> findByChecklistId(Long checklistId);
}
