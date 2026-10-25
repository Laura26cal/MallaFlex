package com.mallaflex.repository;

import com.mallaflex.model.Materia;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MateriaRepository {

    private final JdbcTemplate jdbcTemplate;

    public MateriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Materia> rowMapper = (rs, rowNum) -> new Materia(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getInt("creditos")
    );

    public List<Materia> findAll() {
        String sql = "SELECT codigo, nombre, creditos FROM materia";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Materia> findById(String codigo) {
        String sql = "SELECT codigo, nombre, creditos FROM materia WHERE codigo = ?";
        List<Materia> resultados = jdbcTemplate.query(sql, rowMapper, codigo);
        return resultados.stream().findFirst();
    }
}