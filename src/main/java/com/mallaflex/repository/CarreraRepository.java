package com.mallaflex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mallaflex.model.Carrera;

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

    public boolean existsByCodigo(String codigo) {
        String sql = "SELECT 1 FROM carrera WHERE codigo = ?";
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, codigo).isEmpty();
    }

    public void save(Carrera carrera) {
        String sql = "INSERT INTO carrera (codigo, nombre, plan, creditos_totales, num_semestres, creditos_libre_eleccion) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                carrera.codigo(),
                carrera.nombre(),
                carrera.plan(),
                carrera.creditosTotales(),
                carrera.numSemestres(),
                carrera.creditosLibreEleccion()
        );
    }

    public boolean update(Carrera carrera) {
        String sql = "UPDATE carrera SET nombre = ?, plan = ?, creditos_totales = ?, num_semestres = ?, "
                + "creditos_libre_eleccion = ? WHERE codigo = ?";
        return jdbcTemplate.update(sql,
                carrera.nombre(),
                carrera.plan(),
                carrera.creditosTotales(),
                carrera.numSemestres(),
                carrera.creditosLibreEleccion(),
                carrera.codigo()
        ) > 0;
    }

    public boolean delete(String codigo) {
        String sql = "DELETE FROM carrera WHERE codigo = ?";
        return jdbcTemplate.update(sql, codigo) > 0;
    }

}