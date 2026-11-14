package com.mallaflex.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;

import com.mallaflex.entity.Usuario;
import com.mallaflex.repository.UsuarioRepository;

@Service 
public class AutenticacionService {
    private static final String MENSAJE_CREDENCIALES = "Correo o contraseña incorrectos";

    private static final String PREFIJO = "pbkdf2";
    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_CLAVE_BITS = 256;
    private static final int LONGITUD_SALT_BYTES = 16;
    private static final String HASH_FALSO = hashear("contraseña-falsa");

    private final UsuarioRepository usuarioRepo;

    public AutenticacionService(UsuarioRepository usuarioRepo) {
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

        Usuario usuario = usuarioRepo.findByCorreo(correo.trim()).orElse(null);

        if (usuario == null) {
            verificar(password, HASH_FALSO);
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES);
        }

        if (!verificar(password, usuario.password())) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES);
        }
        return new Usuario(usuario.id(), usuario.nombre(), usuario.correo(),
                           null, usuario.rol(), usuario.carreraCodigo());
    }

    public static String hashear(String password) {
        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derivar(password, salt, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIJO + "$" + ITERACIONES + "$" + b64.encodeToString(salt)
               + "$" + b64.encodeToString(hash);
    }

    private static boolean verificar(String password, String almacenado) {
        if (almacenado == null) {
            return false;
        }
        String[] partes = almacenado.split("\\$");
        if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(password, salt, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String password, byte[] salt, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, iteraciones, LONGITUD_CLAVE_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                                   .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        }
    }
}
