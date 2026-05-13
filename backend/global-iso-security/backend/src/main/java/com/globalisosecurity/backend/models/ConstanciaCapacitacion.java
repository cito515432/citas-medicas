/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "constancias_capacitacion")
public class ConstanciaCapacitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String documento;

    @Column(name = "codigo_interno")
    private String codigoInterno;

    @Column(nullable = false)
    private String cargo;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @ManyToOne
    @JoinColumn(name = "capacitacion_id", nullable = false)
    private Capacitacion capacitacion;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    public Long getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getDocumento() {
        return documento;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getCargo() {
        return cargo;
    }

    public LocalDateTime getFechaFirma() {
        return fechaFirma;
    }

    public Capacitacion getCapacitacion() {
        return capacitacion;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setFechaFirma(LocalDateTime fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public void setCapacitacion(Capacitacion capacitacion) {
        this.capacitacion = capacitacion;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}
