/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.repositories;

import com.globalisosecurity.backend.models.ConstanciaCapacitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstanciaCapacitacionRepository extends JpaRepository<ConstanciaCapacitacion, Long> {
    List<ConstanciaCapacitacion> findByServicioId(Long servicioId);
    List<ConstanciaCapacitacion> findByCapacitacionId(Long capacitacionId);
    List<ConstanciaCapacitacion> findByDocumento(String documento);
}
