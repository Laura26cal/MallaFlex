package com.mallaflex.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mallaflex.entity.HistoriaAcademica;
import com.mallaflex.entity.Materia;
import com.mallaflex.exception.RecursoNoEncontradoException;
import com.mallaflex.exception.ReglaNegocioException;
import com.mallaflex.repository.HistoriaAcademicaRepository;

@Service
@Transactional
public class HistoriaService {

    private final HistoriaAcademicaRepository repo;
    private final CatalogoConsultaService consulta;

    public HistoriaService(HistoriaAcademicaRepository repo, CatalogoConsultaService consulta) {
        this.repo = repo;
        this.consulta = consulta;
    }

    public List<HistoriaAcademica> listarHistoria(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    public void registrarMateria(Long usuarioId, String materiaCodigo, Integer matricula, Double nota) {
        Materia materia = consulta.obtenerMateriaBasica(materiaCodigo);   // valida que la materia exista
        validarMatricula(matricula);
        validarNota(nota);

        // no repetir la misma materia con la misma matricula
        if (repo.existsByUsuarioMateriaMatricula(usuarioId, materiaCodigo, matricula)) {
            throw new ReglaNegocioException("Ya registraste " + materia.nombre()
                    + " con la matrícula " + matricula + ".");
        }

        repo.save(new HistoriaAcademica(null, usuarioId, materiaCodigo, matricula, nota, calcularEstado(nota)));
    }

    // corrige la nota de un intento; el estado se recalcula con la nueva nota
    public void actualizarRegistro(Long usuarioId, String materiaCodigo, Integer matricula, Double nota) {
        validarNota(nota);
        if (!repo.update(usuarioId, materiaCodigo, matricula, nota, calcularEstado(nota))) {
            throw new RecursoNoEncontradoException("No hay un registro de la materia "
                    + materiaCodigo + " con la matrícula " + matricula + ".");
        }
    }

    // deshace el ultimo intento registrado de la materia
    public void eliminarRegistro(Long usuarioId, String materiaCodigo) {
        if (!repo.deleteUltimoIntento(usuarioId, materiaCodigo)) {
            throw new RecursoNoEncontradoException("No hay registros de la materia "
                    + materiaCodigo + " para eliminar.");
        }
    }

    private String calcularEstado(Double nota) {
        return nota == null ? "EN_CURSO" : (nota >= 3.0 ? "APROBADA" : "PERDIDA");
    }

    private void validarNota(Double nota) {
        if (nota != null && (nota < 0.0 || nota > 5.0)) {
            throw new ReglaNegocioException("La nota debe estar entre 0.0 y 5.0.");
        }
    }

    private void validarMatricula(Integer matricula) {
        if (matricula == null || matricula < 1) {
            throw new ReglaNegocioException("La matrícula debe ser 1 o mayor.");
        }
    }
}
