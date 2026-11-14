package com.mallaflex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mallaflex.entity.HistoriaAcademica;
import com.mallaflex.repository.HistoriaAcademicaRepository;

@Service
public class HistoriaService {

    private final HistoriaAcademicaRepository repo;
    private final CatalogoConsultaService consulta;

    public HistoriaService(HistoriaAcademicaRepository repo, CatalogoConsultaService consulta) {
        this.repo = repo;
        this.consulta = consulta;
    }

    public List<HistoriaAcademica> listarHistoria(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    public void registrarMateria(Long usuarioId, String materiaCodigo, Integer matricula, Double nota) {
        consulta.obtenerMateriaBasica(materiaCodigo);   // valida que la materia exista

        String estado = nota == null ? "EN_CURSO" : (nota >= 3.0 ? "APROBADA" : "PERDIDA");

        repo.save(new HistoriaAcademica(null, usuarioId, materiaCodigo, matricula, nota, estado));
    }
}