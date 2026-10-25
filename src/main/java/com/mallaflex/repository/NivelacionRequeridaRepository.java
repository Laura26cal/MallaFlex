package com.mallaflex.repository;

import com.mallaflex.model.NivelacionRequerida;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NivelacionRequeridaRepository {

    private final JdbcTemplate jdbcTemplate;

    public NivelacionRequeridaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findMateriasByUsuarioId(Long usuarioId) {
        String sql = "SELECT materia_codigo FROM nivelacion_requerida WHERE usuario_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("materia_codigo"), usuarioId);
    }

    public void save(NivelacionRequerida nivelacion) {
        String sql = "INSERT INTO nivelacion_requerida (usuario_id, materia_codigo) VALUES (?, ?)";
        jdbcTemplate.update(sql, nivelacion.usuarioId(), nivelacion.materiaCodigo());
    }

    public void delete(Long usuarioId, String materiaCodigo) {
        String sql = "DELETE FROM nivelacion_requerida WHERE usuario_id = ? AND materia_codigo = ?";
        jdbcTemplate.update(sql, usuarioId, materiaCodigo);
    }
}