/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String observacion;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;

    @Column(nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "item_checklist_id", nullable = false)
    private ItemChecklist itemChecklist;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public String getObservacion() {
        return observacion;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public String getEstado() {
        return estado;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public ItemChecklist getItemChecklist() {
        return itemChecklist;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public void setItemChecklist(ItemChecklist itemChecklist) {
        this.itemChecklist = itemChecklist;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}