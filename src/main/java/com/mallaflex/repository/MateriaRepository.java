package com.mallaflex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mallaflex.model.Materia;


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

    public boolean existsByCodigo(String codigo) {
        String sql = "SELECT 1 FROM materia WHERE codigo = ?";
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, codigo).isEmpty();
    }

    public void save(Materia materia) {
        String sql = "INSERT INTO materia (codigo, nombre, creditos) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, materia.codigo(), materia.nombre(), materia.creditos());
    }

    public boolean update(Materia materia) {
        String sql = "UPDATE materia SET nombre = ?, creditos = ? WHERE codigo = ?";
        return jdbcTemplate.update(sql, materia.nombre(), materia.creditos(), materia.codigo()) > 0;
    }

    public boolean delete(String codigo) {
        String sql = "DELETE FROM materia WHERE codigo = ?";
        return jdbcTemplate.update(sql, codigo) > 0;
    }

    // Requisitos

    public List<String> findPrerrequisitosByMateria(String materiaCodigo) {
        String sql = "SELECT requisito_codigo FROM prerrequisito WHERE materia_codigo = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("requisito_codigo"), materiaCodigo);
    }

    public List<String> findCorrequisitosByMateria(String materiaCodigo) {
        String sql = "SELECT correquisito_codigo FROM correquisito WHERE materia_codigo = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("correquisito_codigo"), materiaCodigo);
    }

    // cambiar los prerrqsuisitos
    @Transactional
    public void replacePrerrequisitos(String materiaCodigo, List<String> requisitos) {
        jdbcTemplate.update("DELETE FROM prerrequisito WHERE materia_codigo = ?", materiaCodigo);
        List<Object[]> filas = requisitos.stream()
                .distinct()
                .map(r -> new Object[]{materiaCodigo, r})
                .toList();
        if (!filas.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO prerrequisito (materia_codigo, requisito_codigo) VALUES (?, ?)", filas);
        }
    }

    // cambiar los corrqsuisitos
    @Transactional
    public void replaceCorrequisitos(String materiaCodigo, List<String> correquisitos) {
        jdbcTemplate.update("DELETE FROM correquisito WHERE materia_codigo = ?", materiaCodigo);
        List<Object[]> filas = correquisitos.stream()
                .distinct()
                .map(r -> new Object[]{materiaCodigo, r})
                .toList();
        if (!filas.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO correquisito (materia_codigo, correquisito_codigo) VALUES (?, ?)", filas);
        }
    }

}