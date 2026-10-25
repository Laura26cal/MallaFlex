package com.mallaflex.repository;

import com.mallaflex.model.LibreEleccion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LibreEleccionRepository {

    private final JdbcTemplate jdbcTemplate;

    public LibreEleccionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<LibreEleccion> rowMapper = (rs, rowNum) -> new LibreEleccion(
            rs.getLong("id"),
            rs.getLong("usuario_id"),
            rs.getString("nombre"),
            rs.getInt("creditos"),
            rs.getInt("numero_matricula"),
     
            rs.getObject("nota") != null ? rs.getDouble("nota") : null,
            rs.getString("estado")
    );

    public List<LibreEleccion> findByUsuarioId(Long usuarioId) {
        String sql = "SELECT id, usuario_id, nombre, creditos, numero_matricula, nota, estado FROM libre_eleccion WHERE usuario_id = ?";
        return jdbcTemplate.query(sql, rowMapper, usuarioId);
    }

    public void save(LibreEleccion libreEleccion) {
        String sql = "INSERT INTO libre_eleccion (usuario_id, nombre, creditos, numero_matricula, nota, estado) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                libreEleccion.usuarioId(),
                libreEleccion.nombre(),
                libreEleccion.creditos(),
                libreEleccion.numeroMatricula(),
                libreEleccion.nota(),
                libreEleccion.estado()
        );
    }
}