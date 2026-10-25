package com.mallaflex.repository;

import com.mallaflex.model.Parametro;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParametroRepository {

    private final JdbcTemplate jdbcTemplate;

    public ParametroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Parametro> rowMapper = (rs, rowNum) -> new Parametro(
            rs.getString("clave"),
            rs.getInt("valor")
    );

    // Método para obtener un parámetro específico 
    public Optional<Integer> findValorByClave(String clave) {
        String sql = "SELECT valor FROM parametro WHERE clave = ?";
        List<Integer> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("valor"), clave);
        return resultados.stream().findFirst();
    }

    // Método para cargar todos los parámetros a la vez 
    public List<Parametro> findAll() {
        String sql = "SELECT clave, valor FROM parametro";
        return jdbcTemplate.query(sql, rowMapper);
    }
}