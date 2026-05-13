/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.dto;

public class DashboardResumenDTO {

    private long totalUsuarios;
    private long totalEmpresas;
    private long totalServicios;
    private long serviciosEnProceso;
    private long serviciosFirmados;
    private long serviciosCerrados;
    private long totalEvaluaciones;
    private long totalFirmas;
    private long totalChecklists;

    public long getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(long totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public long getTotalEmpresas() {
        return totalEmpresas;
    }

    public void setTotalEmpresas(long totalEmpresas) {
        this.totalEmpresas = totalEmpresas;
    }

    public long getTotalServicios() {
        return totalServicios;
    }

    public void setTotalServicios(long totalServicios) {
        this.totalServicios = totalServicios;
    }

    public long getServiciosEnProceso() {
        return serviciosEnProceso;
    }

    public void setServiciosEnProceso(long serviciosEnProceso) {
        this.serviciosEnProceso = serviciosEnProceso;
    }

    public long getServiciosFirmados() {
        return serviciosFirmados;
    }

    public void setServiciosFirmados(long serviciosFirmados) {
        this.serviciosFirmados = serviciosFirmados;
    }

    public long getServiciosCerrados() {
        return serviciosCerrados;
    }

    public void setServiciosCerrados(long serviciosCerrados) {
        this.serviciosCerrados = serviciosCerrados;
    }

    public long getTotalEvaluaciones() {
        return totalEvaluaciones;
    }

    public void setTotalEvaluaciones(long totalEvaluaciones) {
        this.totalEvaluaciones = totalEvaluaciones;
    }

    public long getTotalFirmas() {
        return totalFirmas;
    }

    public void setTotalFirmas(long totalFirmas) {
        this.totalFirmas = totalFirmas;
    }

    public long getTotalChecklists() {
        return totalChecklists;
    }

    public void setTotalChecklists(long totalChecklists) {
        this.totalChecklists = totalChecklists;
    }
}
