package com.mallaflex.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Service;

import com.mallaflex.entity.Usuario;
import com.mallaflex.repository.UsuarioRepository;

@Service
public class AutenticacionUsuario {

    private static final String MENSAJE_CREDENCIALES = "Correo o contraseña incorrectos";

    private final UsuarioRepository usuarioRepo;

    public AutenticacionUsuario(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    /**
     * @return
     * @throws IllegalArgumentException
     */
    public Usuario iniciarSesion(String correo, String password) {
        if (correo == null || correo.isBlank() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar correo y contraseña");
        }

        Usuario usuario = usuarioRepo.findByCorreo(correo.trim())
            .orElseThrow(() -> new IllegalArgumentException(MENSAJE_CREDENCIALES));

        if (!coinciden(password, usuario.password())) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES);
        }
        
        return new Usuario(usuario.id(), usuario.nombre(), usuario.correo(),
                           null, usuario.rol(), usuario.carreraCodigo());
    }

    private boolean coinciden(String ingresada, String almacenada) {
        if (almacenada == null) {
            return false;
        }
        return MessageDigest.isEqual(
            ingresada.getBytes(StandardCharsets.UTF_8),
            almacenada.getBytes(StandardCharsets.UTF_8));
    }
}
