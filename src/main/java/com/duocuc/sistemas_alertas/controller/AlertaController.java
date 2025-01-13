package com.duocuc.sistemas_alertas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.duocuc.sistemas_alertas.model.Alerta;
import com.duocuc.sistemas_alertas.service.ServicioAlertas;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {
    
    @Autowired
    private ServicioAlertas alertaServicio;

    // Obtener todas las alertas
    @GetMapping
    public ResponseEntity<List<Alerta>> obtenerTodasLasAlertas() {
        List<Alerta> alertas = alertaServicio.buscarTodas();
        return ResponseEntity.ok(alertas);
    }

    // Obtener alertas pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<Alerta>> obtenerAlertasPendientes() {
        List<Alerta> pendientes = alertaServicio.buscarPendientes();
        return ResponseEntity.ok(pendientes);
    }

    // Obtener alertas por paciente
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Alerta>> obtenerAlertasPorPaciente(@PathVariable Long pacienteId) {
        List<Alerta> alertas = alertaServicio.buscarPorPaciente(pacienteId);
        if (alertas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alertas);
    }

    // Marcar alerta como atendida
    @PutMapping("/{id}/atender")
    public ResponseEntity<Alerta> atenderAlerta(@PathVariable Long id) {
        try {
            Alerta alerta = alertaServicio.marcarComoAtendida(id);
            return ResponseEntity.ok(alerta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}