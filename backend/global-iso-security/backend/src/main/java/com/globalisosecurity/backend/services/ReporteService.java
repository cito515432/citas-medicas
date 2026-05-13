/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.dto.ReporteResumenDTO;
import com.globalisosecurity.backend.models.Servicio;
import com.globalisosecurity.backend.repositories.CapacitacionRepository;
import com.globalisosecurity.backend.repositories.EvaluacionRepository;
import com.globalisosecurity.backend.repositories.FirmaRepository;
import com.globalisosecurity.backend.repositories.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private FirmaRepository firmaRepository;

    @Autowired
    private CapacitacionRepository capacitacionRepository;

    public ReporteResumenDTO obtenerReportePorEmpresa(Long empresaId) {
        List<Servicio> servicios = servicioRepository.findByEmpresaId(empresaId);

        ReporteResumenDTO reporte = new ReporteResumenDTO();
        reporte.setEmpresaId(empresaId);
        reporte.setTotalServicios(servicios.size());
        reporte.setTotalEvaluaciones(evaluacionRepository.findByServicioEmpresaId(empresaId).size());
        reporte.setTotalFirmas(firmaRepository.findByServicioEmpresaId(empresaId).size());
        reporte.setTotalCapacitaciones(capacitacionRepository.findByServicioEmpresaId(empresaId).size());
        reporte.setEstadosServicios(servicios.stream().map(Servicio::getEstado).toList());

        return reporte;
    }
}