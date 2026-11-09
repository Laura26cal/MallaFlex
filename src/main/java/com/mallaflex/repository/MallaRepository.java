package com.mallaflex.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mallaflex.model.Carrera;
import com.mallaflex.model.MateriaMalla;

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

    public boolean existsMateriaEnCarrera(String carreraCodigo, String materiaCodigo) {
        String sql = "SELECT 1 FROM carrera_materia WHERE carrera_codigo = ? AND materia_codigo = ?";
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, carreraCodigo, materiaCodigo).isEmpty();
    }

    public List<Carrera> findCarrerasByMateria(String materiaCodigo) {
        String sql = "SELECT c.codigo, c.nombre, c.plan, c.creditos_totales, c.num_semestres, "
                + "c.creditos_libre_eleccion "
                + "FROM carrera_materia cm JOIN carrera c ON cm.carrera_codigo = c.codigo "
                + "WHERE cm.materia_codigo = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Carrera(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("plan"),
                rs.getInt("creditos_totales"),
                rs.getInt("num_semestres"),
                rs.getInt("creditos_libre_eleccion")
        ), materiaCodigo);
    }

    public void addMateria(String carreraCodigo, String materiaCodigo, String tipologia,
                           Integer semestreSugerido, boolean requiereMallaCompleta) {
        String sql = "INSERT INTO carrera_materia "
                + "(carrera_codigo, materia_codigo, tipologia, semestre_sugerido, requiere_malla_completa) "
                + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, carreraCodigo, materiaCodigo, tipologia, semestreSugerido,
                requiereMallaCompleta ? 1 : 0);
    }

    public boolean removeMateria(String carreraCodigo, String materiaCodigo) {
        String sql = "DELETE FROM carrera_materia WHERE carrera_codigo = ? AND materia_codigo = ?";
        return jdbcTemplate.update(sql, carreraCodigo, materiaCodigo) > 0;
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