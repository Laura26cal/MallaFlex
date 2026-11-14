package com.mallaflex.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mallaflex.entity.Usuario;
import com.mallaflex.exception.RecursoNoEncontradoException;
import com.mallaflex.exception.ReglaNegocioException;
import com.mallaflex.repository.CarreraRepository;
import com.mallaflex.repository.UsuarioRepository;
@Service 
@Transactional 
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final CarreraRepository carreraRepository;
    private final AutenticacionService autenticacionService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CarreraRepository carreraRepository,
                          AutenticacionService autenticacionService) {
        this.usuarioRepository = usuarioRepository;
        this.carreraRepository = carreraRepository;
        this.autenticacionService = autenticacionService;
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String correo, String password) {
        return autenticacionService.iniciarSesion(correo, password);
    }

    public void registrar(Usuario usuario) {
        Usuario limpio = validarDatosRegistro(usuario);

        if (usuarioRepository.findByCorreo(limpio.correo()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un usuario registrado con el correo " + limpio.correo() + ".");
        }

        if (limpio.carreraCodigo() != null && !carreraRepository.existsByCodigo(limpio.carreraCodigo())) {
            throw new RecursoNoEncontradoException("La carrera " + limpio.carreraCodigo() + " no existe.");
        }

        String passwordHash = AutenticacionService.hashear(limpio.password());

        Usuario paraGuardar = new Usuario(
                null,
                limpio.nombre(),
                limpio.correo(),
                passwordHash,
                limpio.rol(),
                limpio.carreraCodigo()
        );

        usuarioRepository.save(paraGuardar);
    }

    private Usuario validarDatosRegistro(Usuario u) {
        if (u == null || esVacio(u.nombre())) {
            throw new ReglaNegocioException("El nombre del usuario es obligatorio.");
        }
        if (esVacio(u.correo())) {
            throw new ReglaNegocioException("El correo es obligatorio.");
        }
        if (esVacio(u.password())) {
            throw new ReglaNegocioException("La contraseña es obligatoria.");
        }

        String rol = esVacio(u.rol()) ? "ESTUDIANTE" : u.rol().trim();
        String carrera = esVacio(u.carreraCodigo()) ? null : u.carreraCodigo().trim();

        return new Usuario(
                u.id(),
                u.nombre().trim(),
                u.correo().trim(),
                u.password(),
                rol,
                carrera
        );
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
