/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.dto.DashboardResumenDTO;
import com.globalisosecurity.backend.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumen")
    public DashboardResumenDTO obtenerResumen() {
        return dashboardService.obtenerResumen();
    }

    @GetMapping("/resumen/empresa/{empresaId}")
    public DashboardResumenDTO obtenerResumenPorEmpresa(@PathVariable Long empresaId) {
        return dashboardService.obtenerResumenPorEmpresa(empresaId);
    }
}