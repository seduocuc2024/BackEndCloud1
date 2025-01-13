package com.duocuc.sistemas_alertas.repository;

import com.duocuc.sistemas_alertas.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface PacienteRepositorio extends JpaRepository<Paciente, Long> {
    List<Paciente> findByEstado(String estado);
    List<Paciente> findByNumeroHabitacion(String numeroHabitacion);
    // Puedes agregar más métodos de búsqueda según necesites
}