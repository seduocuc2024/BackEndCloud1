package com.duocuc.sistemas_alertas.service;

import com.duocuc.sistemas_alertas.model.Alerta;
import com.duocuc.sistemas_alertas.model.Paciente;
import com.duocuc.sistemas_alertas.model.SignosVitales;
import com.duocuc.sistemas_alertas.repository.SignosVitalesRepositorio;
import com.duocuc.sistemas_alertas.repository.PacienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SignosVitalesServicio {

    @Autowired
    private SignosVitalesRepositorio signosVitalesRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private ServicioAlertas servicioAlertas;

    // Registrar nuevos signos vitales para un paciente
    @Transactional
    public SignosVitales registrarParaPaciente(Long pacienteId, SignosVitales signosVitales) {
        Paciente paciente = pacienteRepositorio.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + pacienteId));

        signosVitales.setPaciente(paciente);
        signosVitales.setFechaRegistro(LocalDateTime.now());

        // Verificar si los signos vitales generan alertas
        boolean generaAlerta = verificarSignosVitalesCriticos(signosVitales);
        signosVitales.setTieneAlerta(generaAlerta);

        SignosVitales signosGuardados = signosVitalesRepositorio.save(signosVitales);

        if (generaAlerta) {
            generarAlertas(signosGuardados);
        }

        return signosGuardados;
    }

    // Obtener los últimos signos vitales de un paciente
    public SignosVitales obtenerUltimoPorPaciente(Long pacienteId) {
        return signosVitalesRepositorio.findFirstByPacienteIdOrderByFechaRegistroDesc(pacienteId)
                .orElseThrow(() -> new RuntimeException("No hay signos vitales registrados para el paciente con ID: " + pacienteId));
    }

    // Verificar si los signos vitales son críticos
    private boolean verificarSignosVitalesCriticos(SignosVitales signosVitales) {
        return (signosVitales.getFrecuenciaCardiaca() < 60 || signosVitales.getFrecuenciaCardiaca() > 100) ||
               (signosVitales.getTemperatura() > 38.5) ||
               (signosVitales.getSaturacionOxigeno() < 92);
    }

    // Generar alertas basadas en los signos vitales
    private void generarAlertas(SignosVitales signosVitales) {
        List<String> mensajesAlerta = new ArrayList<>();

        if (signosVitales.getFrecuenciaCardiaca() < 60) {
            mensajesAlerta.add("Frecuencia cardíaca baja: " + signosVitales.getFrecuenciaCardiaca() + " bpm");
        } else if (signosVitales.getFrecuenciaCardiaca() > 100) {
            mensajesAlerta.add("Frecuencia cardíaca alta: " + signosVitales.getFrecuenciaCardiaca() + " bpm");
        }

        if (signosVitales.getTemperatura() > 38.5) {
            mensajesAlerta.add("Temperatura elevada: " + signosVitales.getTemperatura() + "°C");
        }

        if (signosVitales.getSaturacionOxigeno() < 92) {
            mensajesAlerta.add("Saturación de oxígeno baja: " + signosVitales.getSaturacionOxigeno() + "%");
        }

        if (!mensajesAlerta.isEmpty()) {
            Alerta alerta = new Alerta();
            alerta.setPaciente(signosVitales.getPaciente());
            alerta.setSignosVitales(signosVitales);
            alerta.setTipoAlerta("CRITICA");
            alerta.setMensaje(String.join("; ", mensajesAlerta));
            alerta.setFechaCreacion(LocalDateTime.now());
            alerta.setAtendida(false);
            servicioAlertas.guardarAlerta(alerta);
        }
    }


    // Obtener historial de signos vitales
    public List<SignosVitales> obtenerHistorialPaciente(Long pacienteId) {
        return signosVitalesRepositorio.findByPacienteIdOrderByFechaRegistroDesc(pacienteId);
    }

    // Obtener signos vitales en un rango de fechas
    public List<SignosVitales> obtenerPorRangoFechas(Long pacienteId, LocalDateTime inicio, LocalDateTime fin) {
        return signosVitalesRepositorio.findByPacienteIdAndFechaRegistroBetweenOrderByFechaRegistroDesc(
                pacienteId, inicio, fin);
    }
}
