/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.dto;

import java.time.LocalDateTime;

public class ServicioResponseDTO {

    private Long id;
    private String estado;
    private LocalDateTime fechaCreacion;
    private SimpleRef empresa;
    private SimpleRef sector;

    public static class SimpleRef {
        private Long id;
        private String nombre;

        public SimpleRef() {
        }

        public SimpleRef(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }

    public ServicioResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public SimpleRef getEmpresa() {
        return empresa;
    }

    public void setEmpresa(SimpleRef empresa) {
        this.empresa = empresa;
    }

    public SimpleRef getSector() {
        return sector;
    }

    public void setSector(SimpleRef sector) {
        this.sector = sector;
    }
}