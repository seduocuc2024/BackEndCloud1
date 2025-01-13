package com.duocuc.sistemas_alertas.repository;
import com.duocuc.sistemas_alertas.model.SignosVitales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface SignosVitalesRepositorio extends JpaRepository<SignosVitales, Long> {
    Optional<SignosVitales> findFirstByPacienteIdOrderByFechaRegistroDesc(Long pacienteId);
    
    List<SignosVitales> findByPacienteIdOrderByFechaRegistroDesc(Long pacienteId);
    
    List<SignosVitales> findByPacienteIdAndFechaRegistroBetweenOrderByFechaRegistroDesc(
        Long pacienteId, LocalDateTime inicio, LocalDateTime fin);
}