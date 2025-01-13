package com.duocuc.sistemas_alertas.repository;

import com.duocuc.sistemas_alertas.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertaRepositorio extends JpaRepository<Alerta, Long> {
    List<Alerta> findByAtendidaFalse();
    List<Alerta> findByPacienteIdOrderByFechaCreacionDesc(Long pacienteId);
    
}