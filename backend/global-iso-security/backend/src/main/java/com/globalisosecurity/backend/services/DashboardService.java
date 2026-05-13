/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.dto.DashboardResumenDTO;
import com.globalisosecurity.backend.repositories.ChecklistRepository;
import com.globalisosecurity.backend.repositories.EmpresaRepository;
import com.globalisosecurity.backend.repositories.EvaluacionRepository;
import com.globalisosecurity.backend.repositories.FirmaRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import com.globalisosecurity.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private FirmaRepository firmaRepository;

    @Autowired
    private ChecklistRepository checklistRepository;

    public DashboardResumenDTO obtenerResumen() {
        DashboardResumenDTO resumen = new DashboardResumenDTO();

        resumen.setTotalUsuarios(usuarioRepository.count());
        resumen.setTotalEmpresas(empresaRepository.count());
        resumen.setTotalServicios(servicioRepository.count());
        resumen.setServiciosEnProceso(servicioRepository.findByEstado("EN_PROCESO").size());
        resumen.setServiciosFirmados(servicioRepository.findByEstado("FIRMADO").size());
        resumen.setServiciosCerrados(servicioRepository.findByEstado("CERRADO").size());
        resumen.setTotalEvaluaciones(evaluacionRepository.count());
        resumen.setTotalFirmas(firmaRepository.count());
        resumen.setTotalChecklists(checklistRepository.count());

        return resumen;
    }

    public DashboardResumenDTO obtenerResumenPorEmpresa(Long empresaId) {
        DashboardResumenDTO resumen = new DashboardResumenDTO();

        resumen.setTotalUsuarios(usuarioRepository.findByEmpresaId(empresaId).size());
        resumen.setTotalEmpresas(1);
        resumen.setTotalServicios(servicioRepository.findByEmpresaId(empresaId).size());
        resumen.setServiciosEnProceso(servicioRepository.findByEmpresaIdAndEstado(empresaId, "EN_PROCESO").size());
        resumen.setServiciosFirmados(servicioRepository.findByEmpresaIdAndEstado(empresaId, "FIRMADO").size());
        resumen.setServiciosCerrados(servicioRepository.findByEmpresaIdAndEstado(empresaId, "CERRADO").size());
        resumen.setTotalEvaluaciones(evaluacionRepository.findByServicioEmpresaId(empresaId).size());
        resumen.setTotalFirmas(firmaRepository.findByServicioEmpresaId(empresaId).size());
        resumen.setTotalChecklists(checklistRepository.findByServicioEmpresaId(empresaId).size());

        return resumen;
    }
}