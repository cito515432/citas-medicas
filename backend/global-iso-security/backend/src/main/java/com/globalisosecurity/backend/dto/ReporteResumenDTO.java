
package com.globalisosecurity.backend.dto;

import java.util.List;

public class ReporteResumenDTO {

    private Long empresaId;
    private int totalServicios;
    private int totalEvaluaciones;
    private int totalFirmas;
    private int totalCapacitaciones;
    private List<String> estadosServicios;

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public int getTotalServicios() {
        return totalServicios;
    }

    public void setTotalServicios(int totalServicios) {
        this.totalServicios = totalServicios;
    }

    public int getTotalEvaluaciones() {
        return totalEvaluaciones;
    }

    public void setTotalEvaluaciones(int totalEvaluaciones) {
        this.totalEvaluaciones = totalEvaluaciones;
    }

    public int getTotalFirmas() {
        return totalFirmas;
    }

    public void setTotalFirmas(int totalFirmas) {
        this.totalFirmas = totalFirmas;
    }

    public int getTotalCapacitaciones() {
        return totalCapacitaciones;
    }

    public void setTotalCapacitaciones(int totalCapacitaciones) {
        this.totalCapacitaciones = totalCapacitaciones;
    }

    public List<String> getEstadosServicios() {
        return estadosServicios;
    }

    public void setEstadosServicios(List<String> estadosServicios) {
        this.estadosServicios = estadosServicios;
    }
}