/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.dto;

import com.globalisosecurity.backend.models.Checklist;
import com.globalisosecurity.backend.models.ItemChecklist;

import java.util.List;

public class ChecklistCompletoResponse {

    private Checklist checklist;
    private List<ItemChecklist> items;

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public List<ItemChecklist> getItems() {
        return items;
    }

    public void setItems(List<ItemChecklist> items) {
        this.items = items;
    }
}