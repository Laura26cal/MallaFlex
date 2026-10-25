package com.mallaflex.repository;

import com.mallaflex.model.Carrera;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarreraRepository {

    private final JdbcTemplate jdbcTemplate;

    public CarreraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Carrera> rowMapper = (rs, rowNum) -> new Carrera(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("plan"),
            rs.getInt("creditos_totales"),
            rs.getInt("num_semestres"),
            rs.getInt("creditos_libre_eleccion")
    );

    public List<Carrera> findAll() {
        String sql = "SELECT codigo, nombre, plan, creditos_totales, num_semestres, creditos_libre_eleccion FROM carrera";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Carrera> findById(String codigo) {
        String sql = "SELECT codigo, nombre, plan, creditos_totales, num_semestres, creditos_libre_eleccion FROM carrera WHERE codigo = ?";
        List<Carrera> resultados = jdbcTemplate.query(sql, rowMapper, codigo);
        return resultados.stream().findFirst();
    }
}