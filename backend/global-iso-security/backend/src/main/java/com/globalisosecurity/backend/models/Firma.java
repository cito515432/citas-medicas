/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "firmas")
public class Firma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreFirmante;

    @Column(nullable = false)
    private String cargo;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @Column(nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    public Long getId() {
        return id;
    }

    public String getNombreFirmante() {
        return nombreFirmante;
    }

    public String getCargo() {
        return cargo;
    }

    public LocalDateTime getFechaFirma() {
        return fechaFirma;
    }

    public String getEstado() {
        return estado;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreFirmante(String nombreFirmante) {
        this.nombreFirmante = nombreFirmante;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setFechaFirma(LocalDateTime fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}
