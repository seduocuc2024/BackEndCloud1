package com.duocuc.sistemas_alertas.service;

import com.duocuc.sistemas_alertas.model.Alerta;
import com.duocuc.sistemas_alertas.model.Paciente;
import com.duocuc.sistemas_alertas.model.SignosVitales;
import com.duocuc.sistemas_alertas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioAlertas {
    private static final int FRECUENCIA_CARDIACA_ALTA = 100;
    private static final int FRECUENCIA_CARDIACA_BAJA = 60;
    private static final double TEMPERATURA_ALTA = 38.5;
    private static final int SATURACION_BAJA = 92;

    @Autowired
    private AlertaRepositorio alertaRepositorio;

    // Buscar todas las alertas
    public List<Alerta> buscarTodas() {
        return alertaRepositorio.findAll();
    }

    // Buscar alertas pendientes (no atendidas)
    public List<Alerta> buscarPendientes() {
        return alertaRepositorio.findByAtendidaFalse();
    }

    // Buscar alertas por paciente
    public List<Alerta> buscarPorPaciente(Long pacienteId) {
        if (pacienteId == null) {
            throw new IllegalArgumentException("El ID del paciente no puede ser nulo.");
        }
        return alertaRepositorio.findByPacienteIdOrderByFechaCreacionDesc(pacienteId);
    }

    // Marcar alerta como atendida
    @Transactional
    public Alerta marcarComoAtendida(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la alerta no puede ser nulo.");
        }
        
        Alerta alerta = alertaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con ID: " + id));
        
        if (!alerta.isAtendida()) {
            alerta.setAtendida(true);
            alerta.setFechaAtencion(LocalDateTime.now());
            return alertaRepositorio.save(alerta);
        }
        
        return alerta;
    }
    @Transactional
public Alerta guardarAlerta(Alerta alerta) {
    if (alerta == null) {
        throw new IllegalArgumentException("La alerta no puede ser nula.");
    }
    if (alerta.getPaciente() == null) {
        throw new IllegalArgumentException("La alerta debe tener un paciente asociado.");
    }
    
    alerta.setFechaCreacion(LocalDateTime.now());
    if (alerta.getFechaCreacion() == null) {
        alerta.setFechaCreacion(LocalDateTime.now());
    }
    if (alerta.getTipoAlerta() == null) {
        alerta.setTipoAlerta("GENERAL");
    }
    
    return alertaRepositorio.save(alerta);
}

    // Verificar signos vitales y crear alertas si es necesario
    @Transactional
    public Alerta verificarSignosVitales(SignosVitales signosVitales) {
        validarSignosVitales(signosVitales);
        List<String> mensajesAlerta = generarMensajesAlerta(signosVitales);

        if (!mensajesAlerta.isEmpty()) {
            return crearAlerta(signosVitales.getPaciente(), signosVitales, mensajesAlerta);
        }

        return null;
    }

    // Validar signos vitales
    private void validarSignosVitales(SignosVitales signosVitales) {
        if (signosVitales == null) {
            throw new IllegalArgumentException("Los signos vitales no pueden ser nulos.");
        }
        if (signosVitales.getPaciente() == null) {
            throw new IllegalArgumentException("El paciente asociado a los signos vitales no puede ser nulo.");
        }
    }

    // Generar mensajes de alerta basados en los signos vitales
    private List<String> generarMensajesAlerta(SignosVitales signosVitales) {
        List<String> mensajesAlerta = new ArrayList<>();

        // Verificar frecuencia cardíaca
        if (signosVitales.getFrecuenciaCardiaca() != null) {
            int fc = signosVitales.getFrecuenciaCardiaca();
            if (fc > FRECUENCIA_CARDIACA_ALTA || fc < FRECUENCIA_CARDIACA_BAJA) {
                mensajesAlerta.add(String.format("Frecuencia cardíaca fuera de rango normal: %d bpm", fc));
            }
        }

        // Verificar temperatura
        if (signosVitales.getTemperatura() != null && signosVitales.getTemperatura() > TEMPERATURA_ALTA) {
            mensajesAlerta.add(String.format("Temperatura elevada: %.1f°C", signosVitales.getTemperatura()));
        }

        // Verificar saturación de oxígeno
        if (signosVitales.getSaturacionOxigeno() != null && signosVitales.getSaturacionOxigeno() < SATURACION_BAJA) {
            mensajesAlerta.add(String.format("Saturación de oxígeno baja: %d%%", signosVitales.getSaturacionOxigeno()));
        }

        return mensajesAlerta;
    }

    // Crear y guardar una nueva alerta
    private Alerta crearAlerta(Paciente paciente, SignosVitales signosVitales, List<String> mensajes) {
        Alerta alerta = new Alerta();
        alerta.setPaciente(paciente);
        alerta.setSignosVitales(signosVitales);
        alerta.setTipoAlerta("SIGNOS_VITALES");
        alerta.setMensaje(String.join(", ", mensajes));
        alerta.setFechaCreacion(LocalDateTime.now());
        alerta.setAtendida(false);

        return alertaRepositorio.save(alerta);
    }
}