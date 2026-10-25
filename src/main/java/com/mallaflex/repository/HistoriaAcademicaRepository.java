package com.mallaflex.repository;

import com.mallaflex.model.HistoriaAcademica;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HistoriaAcademicaRepository {

    private final JdbcTemplate jdbcTemplate;

    public HistoriaAcademicaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<HistoriaAcademica> rowMapper = (rs, rowNum) -> new HistoriaAcademica(
            rs.getLong("id"),
            rs.getLong("usuario_id"),
            rs.getString("materia_codigo"),
            rs.getInt("numero_matricula"),
           
            rs.getObject("nota") != null ? rs.getDouble("nota") : null,
            rs.getString("estado")
    );

    public List<HistoriaAcademica> findByUsuarioId(Long usuarioId) {
        String sql = "SELECT id, usuario_id, materia_codigo, numero_matricula, nota, estado FROM historia_academica WHERE usuario_id = ?";
        return jdbcTemplate.query(sql, rowMapper, usuarioId);
    }

    public void save(HistoriaAcademica historia) {
        String sql = "INSERT INTO historia_academica (usuario_id, materia_codigo, numero_matricula, nota, estado) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                historia.usuarioId(),
                historia.materiaCodigo(),
                historia.numeroMatricula(),
                historia.nota(),
                historia.estado()
        );
    }
}