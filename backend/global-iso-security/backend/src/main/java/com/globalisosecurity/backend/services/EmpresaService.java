/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.services;

import com.globalisosecurity.backend.exceptions.BadRequestException;
import com.globalisosecurity.backend.exceptions.ResourceNotFoundException;
import com.globalisosecurity.backend.models.Empresa;
import com.globalisosecurity.backend.models.Usuario;
import com.globalisosecurity.backend.repositories.EmpresaRepository;
import com.globalisosecurity.backend.repositories.UsuarioRepository;
import com.globalisosecurity.backend.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogAuditoriaService logAuditoriaService;

    public List<Empresa> obtenerTodas() {
        return empresaRepository.findAll();
    }

    public List<Empresa> obtenerEmpresasAsignadas() {
        String emailUsuarioActual = SecurityUtils.getUsuarioActual();

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        if (usuario.getEmpresa() == null) {
            return List.of();
        }

        return List.of(usuario.getEmpresa());
    }

    public Optional<Empresa> obtenerPorId(Long id) {
        return empresaRepository.findById(id);
    }

    public Empresa crearEmpresa(Empresa empresa) {
        validarNombreEmpresa(empresa.getNombre());

        Empresa existente = empresaRepository.findByNombre(empresa.getNombre().trim());
        if (existente != null) {
            throw new BadRequestException("Ya existe una empresa con ese nombre");
        }

        empresa.setNombre(empresa.getNombre().trim());
        Empresa nueva = empresaRepository.save(empresa);

        logAuditoriaService.registrarLog(
                "CREAR",
                "EMPRESAS",
                "Se creó la empresa: " + nueva.getNombre()
        );

        return nueva;
    }

    public Empresa actualizarEmpresa(Long id, Empresa empresaActualizada) {
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);

        if (empresaOpt.isEmpty()) {
            throw new ResourceNotFoundException("Empresa no encontrada");
        }

        validarNombreEmpresa(empresaActualizada.getNombre());

        Empresa empresaConMismoNombre = empresaRepository.findByNombre(empresaActualizada.getNombre().trim());
        if (empresaConMismoNombre != null && !empresaConMismoNombre.getId().equals(id)) {
            throw new BadRequestException("Ya existe una empresa con ese nombre");
        }

        Empresa empresa = empresaOpt.get();
        empresa.setNombre(empresaActualizada.getNombre().trim());

        Empresa actualizada = empresaRepository.save(empresa);

        logAuditoriaService.registrarLog(
                "ACTUALIZAR",
                "EMPRESAS",
                "Se actualizó la empresa con ID: " + actualizada.getId() + " y nombre: " + actualizada.getNombre()
        );

        return actualizada;
    }

    public void eliminarEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        empresaRepository.delete(empresa);

        logAuditoriaService.registrarLog(
                "ELIMINAR",
                "EMPRESAS",
                "Se eliminó la empresa con ID: " + empresa.getId() + " y nombre: " + empresa.getNombre()
        );
    }

    private void validarNombreEmpresa(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BadRequestException("El nombre de la empresa es obligatorio");
        }
    }
}