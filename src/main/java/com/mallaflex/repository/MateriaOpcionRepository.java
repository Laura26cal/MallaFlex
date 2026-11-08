package com.mallaflex.repository;

import com.mallaflex.entity.Materia;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MateriaOpcionRepository {

    private final JdbcTemplate jdbcTemplate;

    public MateriaOpcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Materia> materiaRowMapper = (rs, rowNum) -> new Materia(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getInt("creditos")
    );

    public List<Materia> findOpcionesByMateria(String materiaCodigo) {
        String sql = """
            SELECT m.codigo, m.nombre, m.creditos 
            FROM materia_opcion mo
            JOIN materia m ON mo.opcion_codigo = m.codigo
            WHERE mo.materia_codigo = ?
        """;
        return jdbcTemplate.query(sql, materiaRowMapper, materiaCodigo);
    }

    // cambiar las opciones de una materia
    @Transactional
    public void replaceOpciones(String materiaCodigo, List<String> opciones) {
        jdbcTemplate.update("DELETE FROM materia_opcion WHERE materia_codigo = ?", materiaCodigo);
        List<Object[]> filas = opciones.stream()
                .distinct()
                .map(o -> new Object[]{materiaCodigo, o})
                .toList();
        if (!filas.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO materia_opcion (materia_codigo, opcion_codigo) VALUES (?, ?)", filas);
        }
    }

}
