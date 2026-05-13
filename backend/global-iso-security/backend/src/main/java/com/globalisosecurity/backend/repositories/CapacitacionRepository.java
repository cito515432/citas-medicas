/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.repositories;

import com.globalisosecurity.backend.models.Capacitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapacitacionRepository extends JpaRepository<Capacitacion, Long> {
    List<Capacitacion> findByEstado(String estado);
    List<Capacitacion> findByServicioId(Long servicioId);
    List<Capacitacion> findByServicioEmpresaId(Long empresaId);
}