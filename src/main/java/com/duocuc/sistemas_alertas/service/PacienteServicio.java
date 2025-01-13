package com.duocuc.sistemas_alertas.service;

import com.duocuc.sistemas_alertas.model.Paciente;
import com.duocuc.sistemas_alertas.model.SignosVitales;
import com.duocuc.sistemas_alertas.repository.PacienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PacienteServicio {
    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private SignosVitalesServicio signosVitalesServicio;

    // Encontrar todos los pacientes
    public List<Paciente> buscarTodos() {
        return pacienteRepositorio.findAll();
    }

    // Buscar paciente por ID
    public Paciente buscarPorId(Long id) {
        return pacienteRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    public Paciente guardar(Paciente paciente) {
    // Validaciones básicas
    if (paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()) {
        throw new RuntimeException("El nombre del paciente es obligatorio");
    }
    if (paciente.getNumeroHabitacion() == null || paciente.getNumeroHabitacion().trim().isEmpty()) {
        throw new RuntimeException("El número de habitación es obligatorio");
    }
    
    // Si no se especifica un estado, se establece como ESTABLE por defecto
    if (paciente.getEstado() == null) {
        paciente.setEstado("ACTIVO");
    }
    
    // Establecer la relación bidireccional con SignosVitales
    if (paciente.getSignosVitales() != null) {
        for (SignosVitales signoVital : paciente.getSignosVitales()) {
            signoVital.setPaciente(paciente);
            if (signoVital.getFechaRegistro() == null) {
                signoVital.setFechaRegistro(LocalDateTime.now());
            }
        }
    }
    
    return pacienteRepositorio.save(paciente);
}
public Paciente actualizar(Long id, Paciente pacienteActualizado) {
    Paciente pacienteExistente = buscarPorId(id);
    
    // Actualizar solo los campos no nulos
    if (pacienteActualizado.getNombre() != null) {
        pacienteExistente.setNombre(pacienteActualizado.getNombre());
    }
    if (pacienteActualizado.getNumeroHabitacion() != null) {
        pacienteExistente.setNumeroHabitacion(pacienteActualizado.getNumeroHabitacion());
    }
    if (pacienteActualizado.getEstado() != null) {
        pacienteExistente.setEstado(pacienteActualizado.getEstado());
    }
    if (pacienteActualizado.getEdad() != null) {
        pacienteExistente.setEdad(pacienteActualizado.getEdad());
    }
    
    // Actualizar signos vitales si se proporcionan
    if (pacienteActualizado.getSignosVitales() != null) {
        // Limpiar los signos vitales existentes
        pacienteExistente.getSignosVitales().clear();
        
        // Agregar los nuevos signos vitales
        for (SignosVitales signoVital : pacienteActualizado.getSignosVitales()) {
            signoVital.setPaciente(pacienteExistente);
            if (signoVital.getFechaRegistro() == null) {
                signoVital.setFechaRegistro(LocalDateTime.now());
            }
            pacienteExistente.getSignosVitales().add(signoVital);
        }
    }
    
    return pacienteRepositorio.save(pacienteExistente);
}

    // Eliminar paciente
    public boolean eliminar(Long id) {
        try {
            Paciente paciente = buscarPorId(id);
            pacienteRepositorio.delete(paciente);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    // Buscar pacientes por estado
    public List<Paciente> buscarPorEstado(String estado) {
        return pacienteRepositorio.findByEstado(estado);
    }

    // Buscar pacientes críticos
    public List<Paciente> buscarPacientesCriticos() {
        return pacienteRepositorio.findByEstado("CRITICO");
    }

    // Actualizar estado del paciente
    public Paciente actualizarEstado(Long id, String nuevoEstado) {
        Paciente paciente = buscarPorId(id);
        paciente.setEstado(nuevoEstado);
        return pacienteRepositorio.save(paciente);
    }

    // Obtener últimos signos vitales del paciente
    public SignosVitales obtenerUltimosSignosVitales(Long id) {
        Paciente paciente = buscarPorId(id);
        return signosVitalesServicio.obtenerUltimoPorPaciente(id);
    }

    // Verificar si el paciente tiene alertas activas
    public boolean tieneAlertasActivas(Long id) {
        Paciente paciente = buscarPorId(id);
        List<SignosVitales> signosVitales = paciente.getSignosVitales();
        if (signosVitales != null && !signosVitales.isEmpty()) {
            return signosVitales.stream()
                .anyMatch(sv -> sv.getTieneAlerta() != null && sv.getTieneAlerta());
        }
        return false;
    }

    public boolean existePaciente(Long id) {
        return pacienteRepositorio.existsById(id);
    }
}

