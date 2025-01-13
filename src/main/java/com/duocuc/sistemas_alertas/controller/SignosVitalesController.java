package com.duocuc.sistemas_alertas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.duocuc.sistemas_alertas.model.SignosVitales;
import com.duocuc.sistemas_alertas.service.SignosVitalesServicio;

@RestController
@RequestMapping("/api/signos-vitales")
public class SignosVitalesController {
    @Autowired
    private SignosVitalesServicio signosVitalesServicio;

    // Registrar signos vitales para un paciente
    @PostMapping("/paciente/{pacienteId}")
    public ResponseEntity<SignosVitales> registrarSignosVitales(
        @PathVariable Long pacienteId,
        @RequestBody SignosVitales signosVitales
    ) {
        try {
            SignosVitales registrado = signosVitalesServicio.registrarParaPaciente(pacienteId, signosVitales);
            return ResponseEntity.status(201).body(registrado); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // 500 Internal Server Error
        }
    }

    // Obtener los últimos signos vitales de un paciente
    @GetMapping("/paciente/{pacienteId}/ultimo")
    public ResponseEntity<SignosVitales> obtenerUltimosSignosVitales(@PathVariable Long pacienteId) {
        try {
            SignosVitales ultimoSignoVital = signosVitalesServicio.obtenerUltimoPorPaciente(pacienteId);
            if (ultimoSignoVital != null) {
                return ResponseEntity.ok(ultimoSignoVital); // 200 OK
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // 500 Internal Server Error
        }
    }
}
