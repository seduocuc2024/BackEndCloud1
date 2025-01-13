package com.duocuc.sistemas_alertas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.duocuc.sistemas_alertas.model.Paciente;
import com.duocuc.sistemas_alertas.service.PacienteServicio;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    @Autowired
    private PacienteServicio pacienteServicio;

    // Obtener todos los pacientes
    @GetMapping
    public ResponseEntity<List<Paciente>> obtenerTodos() {
        List<Paciente> pacientes = pacienteServicio.buscarTodos();
        return ResponseEntity.ok(pacientes);
    }

    // Crear un nuevo paciente
    @PostMapping
    public ResponseEntity<Paciente> crear(@RequestBody Paciente paciente) {
    try {
        System.out.println("Objeto paciente recibido: " + paciente.getNombre());  // Agregar este log
        Paciente nuevoPaciente = pacienteServicio.guardar(paciente);
        return ResponseEntity.status(201).body(nuevoPaciente);
    } catch (Exception e) {
        System.err.println("Error al crear paciente: " + e.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}

    // Obtener un paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPorId(@PathVariable Long id) {
        Paciente paciente = pacienteServicio.buscarPorId(id);
        if (paciente != null) {
            return ResponseEntity.ok(paciente);
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // Actualizar un paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        Paciente actualizado = pacienteServicio.actualizar(id, paciente);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // Eliminar un paciente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = pacienteServicio.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    @GetMapping("/{id}/existe")
    public boolean existePaciente(@PathVariable Long id) {
        return pacienteServicio.existePaciente(id);
    }
}
