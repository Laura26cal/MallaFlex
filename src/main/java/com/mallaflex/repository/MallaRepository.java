package com.mallaflex.repository;

import com.mallaflex.model.MateriaMalla;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MallaRepository {

    private final JdbcTemplate jdbcTemplate;

    public MallaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MateriaMalla> materiaMallaRowMapper = (rs, rowNum) -> new MateriaMalla(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getInt("creditos"),
            rs.getString("tipologia"),
            rs.getObject("semestre_sugerido") != null ? rs.getInt("semestre_sugerido") : null,
            rs.getInt("requiere_malla_completa") == 1
    );

    public List<MateriaMalla> findMateriasByCarrera(String carreraCodigo) {
        String sql = """
            SELECT m.codigo, m.nombre, m.creditos, 
                   cm.tipologia, cm.semestre_sugerido, cm.requiere_malla_completa
            FROM carrera_materia cm
            JOIN materia m ON cm.materia_codigo = m.codigo
            WHERE cm.carrera_codigo = ?
        """;
        return jdbcTemplate.query(sql, materiaMallaRowMapper, carreraCodigo);
    }

    public List<String> findPrerrequisitosByMateria(String materiaCodigo) {
        String sql = "SELECT requisito_codigo FROM prerrequisito WHERE materia_codigo = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("requisito_codigo"), materiaCodigo);
    }
    
    public List<String> findCorrequisitosByMateria(String materiaCodigo) {
        String sql = "SELECT correquisito_codigo FROM correquisito WHERE materia_codigo = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("correquisito_codigo"), materiaCodigo);
    }
}