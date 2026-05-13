/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.repositories;

import com.globalisosecurity.backend.models.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByEstado(String estado);
    List<Evaluacion> findByServicioId(Long servicioId);
    List<Evaluacion> findByServicioEmpresaId(Long empresaId);
    Optional<Evaluacion> findByServicioIdAndItemChecklistIdAndUsuarioId(Long servicioId, Long itemChecklistId, Long usuarioId);
}