package com.mallaflex.repository;

import com.mallaflex.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Usuario> rowMapper = (rs, rowNum) -> new Usuario(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("correo"),
            rs.getString("password"),
            rs.getString("rol"),
            rs.getString("carrera_codigo")
    );

    public Optional<Usuario> findByCorreo(String correo) {
        String sql = "SELECT id, nombre, correo, password, rol, carrera_codigo FROM usuario WHERE correo = ?";
        List<Usuario> resultados = jdbcTemplate.query(sql, rowMapper, correo);
        return resultados.stream().findFirst();
    }

    public void save(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre, correo, password, rol, carrera_codigo) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                usuario.nombre(),
                usuario.correo(),
                usuario.password(),
                usuario.rol(),
                usuario.carreraCodigo()
        );
    }
}