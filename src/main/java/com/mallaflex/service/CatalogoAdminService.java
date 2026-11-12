package com.mallaflex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mallaflex.exception.RecursoNoEncontradoException;
import com.mallaflex.exception.ReglaNegocioException;
import com.mallaflex.entity.Carrera;
import com.mallaflex.entity.Materia;
import com.mallaflex.repository.CarreraRepository;
import com.mallaflex.repository.HistoriaAcademicaRepository;
import com.mallaflex.repository.MallaRepository;
import com.mallaflex.repository.MateriaOpcionRepository;
import com.mallaflex.repository.MateriaRepository;

@Service
@Transactional
public class CatalogoAdminService implements ICatalogoAdmin {

    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final MallaRepository mallaRepository;
    private final MateriaOpcionRepository materiaOpcionRepository;
    private final HistoriaAcademicaRepository historiaAcademicaRepository;

    // logica pura, sin estado
    private final ValidadorPrerrequisitos validadorPrerrequisitos = new ValidadorPrerrequisitos();

    public CatalogoAdminService(CarreraRepository carreraRepository,
                                MateriaRepository materiaRepository,
                                MallaRepository mallaRepository,
                                MateriaOpcionRepository materiaOpcionRepository,
                                HistoriaAcademicaRepository historiaAcademicaRepository) {
        this.carreraRepository = carreraRepository;
        this.materiaRepository = materiaRepository;
        this.mallaRepository = mallaRepository;
        this.materiaOpcionRepository = materiaOpcionRepository;
        this.historiaAcademicaRepository = historiaAcademicaRepository;
    }

    // carreras

    @Override
    public Carrera crearCarrera(Carrera carrera) {
        Carrera limpia = validarDatosCarrera(carrera);
        if (carreraRepository.existsByCodigo(limpia.codigo())) {
            throw new ReglaNegocioException("Ya existe una carrera con el código " + limpia.codigo() + ".");
        }
        carreraRepository.save(limpia);
        return limpia;
    }

    @Override
    public Carrera actualizarCarrera(Carrera carrera) {
        Carrera limpia = validarDatosCarrera(carrera);
        obtenerCarreraOFallar(limpia.codigo());
        carreraRepository.update(limpia);
        return limpia;
    }

    @Override
    public void eliminarCarrera(String codigo) {
        obtenerCarreraOFallar(codigo);
        carreraRepository.delete(codigo);
    }

    // materias

    @Override
    public Materia crearMateria(Materia materia, List<String> prerrequisitos, List<String> correquisitos) {
        Materia limpia = validarDatosMateria(materia);

        // codigo unico
        if (materiaRepository.existsByCodigo(limpia.codigo())) {
            throw new ReglaNegocioException("Ya existe una materia con el código " + limpia.codigo() + ".");
        }

        List<String> pre = normalizar(prerrequisitos);
        List<String> co = normalizar(correquisitos);
        validarPrerrequisitos(limpia.codigo(), pre);
        validarCorrequisitos(limpia.codigo(), co);

        // ya validado, se guarda
        materiaRepository.save(limpia);
        if (!pre.isEmpty()) {
            materiaRepository.replacePrerrequisitos(limpia.codigo(), pre);
        }
        if (!co.isEmpty()) {
            materiaRepository.replaceCorrequisitos(limpia.codigo(), co);
        }
        return limpia;
    }

    @Override
    public Materia actualizarMateria(Materia materia) {
        Materia limpia = validarDatosMateria(materia);
        obtenerMateriaOFallar(limpia.codigo());
        materiaRepository.update(limpia);
        return limpia;
    }

    @Override
    public void eliminarMateria(String codigo) {
        Materia materia = obtenerMateriaOFallar(codigo);
        String nombre = etiquetaDe(materia);

        // no puede ser prerrequisito de otra
        List<String> dependientes = materiaRepository.findMateriasQueRequieren(codigo);
        if (!dependientes.isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar " + nombre
                    + ": es prerrequisito de " + etiquetas(dependientes) + ".");
        }

        // no puede estar en una malla
        List<Carrera> carreras = mallaRepository.findCarrerasByMateria(codigo);
        if (!carreras.isEmpty()) {
            String nombres = carreras.stream().map(Carrera::nombre).collect(Collectors.joining(", "));
            throw new ReglaNegocioException("No se puede eliminar " + nombre
                    + ": está en la malla de " + nombres + ". Quítala de la malla primero.");
        }

        // no puede tener historia academica
        if (historiaAcademicaRepository.existsByMateria(codigo)) {
            throw new ReglaNegocioException("No se puede eliminar " + nombre
                    + ": algún estudiante la tiene en su historia académica.");
        }

        materiaRepository.delete(codigo);
    }

    @Override
    public void reemplazarPrerrequisitos(String materiaCodigo, List<String> prerrequisitos) {
        obtenerMateriaOFallar(materiaCodigo);
        List<String> pre = normalizar(prerrequisitos);
        validarPrerrequisitos(materiaCodigo, pre);
        materiaRepository.replacePrerrequisitos(materiaCodigo, pre);
    }

    @Override
    public void reemplazarCorrequisitos(String materiaCodigo, List<String> correquisitos) {
        obtenerMateriaOFallar(materiaCodigo);
        List<String> co = normalizar(correquisitos);
        validarCorrequisitos(materiaCodigo, co);
        materiaRepository.replaceCorrequisitos(materiaCodigo, co);
    }

    @Override
    public void reemplazarOpciones(String materiaCodigo, List<String> opciones) {
        obtenerMateriaOFallar(materiaCodigo);
        List<String> ops = normalizar(opciones);
        for (String opcion : ops) {
            if (opcion.equals(materiaCodigo)) {
                throw new ReglaNegocioException("Una materia no puede ser opción de sí misma.");
            }
            if (!materiaRepository.existsByCodigo(opcion)) {
                throw new RecursoNoEncontradoException("La materia opción " + opcion + " no existe.");
            }
        }
        materiaOpcionRepository.replaceOpciones(materiaCodigo, ops);
    }

    // malla

    @Override
    public void asignarMateriaACarrera(String carreraCodigo, String materiaCodigo, String tipologia,
                                       Integer semestreSugerido, boolean requiereMallaCompleta) {
        Carrera carrera = obtenerCarreraOFallar(carreraCodigo);
        Materia materia = obtenerMateriaOFallar(materiaCodigo);

        // no asignar dos veces la misma materia
        if (mallaRepository.existsMateriaEnCarrera(carreraCodigo, materiaCodigo)) {
            throw new ReglaNegocioException("La materia " + etiquetaDe(materia)
                    + " ya está asignada a la carrera " + carrera.nombre() + ".");
        }

        // semestre entre 1 y num_semestres
        if (semestreSugerido != null
                && (semestreSugerido < 1 || semestreSugerido > carrera.numSemestres())) {
            throw new ReglaNegocioException("El semestre sugerido debe estar entre 1 y "
                    + carrera.numSemestres() + " (la carrera " + carrera.nombre() + " tiene "
                    + carrera.numSemestres() + " semestres).");
        }

        mallaRepository.addMateria(carreraCodigo, materiaCodigo, tipologia, semestreSugerido,
                requiereMallaCompleta);
    }

    @Override
    public void quitarMateriaDeCarrera(String carreraCodigo, String materiaCodigo) {
        Carrera carrera = obtenerCarreraOFallar(carreraCodigo);
        obtenerMateriaOFallar(materiaCodigo);
        if (!mallaRepository.existsMateriaEnCarrera(carreraCodigo, materiaCodigo)) {
            throw new RecursoNoEncontradoException("La materia " + etiqueta(materiaCodigo)
                    + " no está en la malla de la carrera " + carrera.nombre() + ".");
        }
        mallaRepository.removeMateria(carreraCodigo, materiaCodigo);
    }

    // validaciones

    private Carrera validarDatosCarrera(Carrera c) {
        if (c == null || esVacio(c.codigo())) {
            throw new ReglaNegocioException("El código de la carrera es obligatorio.");
        }
        if (esVacio(c.nombre())) {
            throw new ReglaNegocioException("El nombre de la carrera es obligatorio.");
        }
        if (esVacio(c.plan())) {
            throw new ReglaNegocioException("El plan de la carrera es obligatorio.");
        }
        if (c.creditosTotales() == null || c.creditosTotales() <= 0) {
            throw new ReglaNegocioException("Los créditos totales de la carrera deben ser mayores a 0.");
        }
        if (c.numSemestres() == null || c.numSemestres() <= 0) {
            throw new ReglaNegocioException("El número de semestres debe ser mayor a 0.");
        }
        int libre = c.creditosLibreEleccion() == null ? 0 : c.creditosLibreEleccion();
        if (libre < 0) {
            throw new ReglaNegocioException("Los créditos de libre elección no pueden ser negativos.");
        }
        return new Carrera(c.codigo().trim(), c.nombre().trim(), c.plan().trim(),
                c.creditosTotales(), c.numSemestres(), libre);
    }

    private Materia validarDatosMateria(Materia m) {
        if (m == null || esVacio(m.codigo())) {
            throw new ReglaNegocioException("El código de la materia es obligatorio.");
        }
        if (esVacio(m.nombre())) {
            throw new ReglaNegocioException("El nombre de la materia es obligatorio.");
        }
        if (m.creditos() == null || m.creditos() <= 0) {
            throw new ReglaNegocioException("Los créditos deben ser mayores a 0.");
        }
        return new Materia(m.codigo().trim(), m.nombre().trim(), m.creditos());
    }

    private void validarPrerrequisitos(String materiaCodigo, List<String> prerrequisitos) {
        for (String requisito : prerrequisitos) {
            if (requisito.equals(materiaCodigo)) {
                throw new ReglaNegocioException("Una materia no puede ser prerrequisito de sí misma.");
            }
            if (!materiaRepository.existsByCodigo(requisito)) {
                throw new RecursoNoEncontradoException("El prerrequisito " + requisito + " no existe.");
            }
        }

        Map<String, List<String>> grafo = materiaRepository.findGrafoPrerrequisitos();
        Optional<List<String>> ciclo = validadorPrerrequisitos.encontrarCiclo(grafo, materiaCodigo, prerrequisitos);
        if (ciclo.isPresent()) {
            String cadena = ciclo.get().stream().map(this::etiqueta).collect(Collectors.joining(" → "));
            throw new ReglaNegocioException("No se puede guardar: se crearía una dependencia circular "
                    + "de prerrequisitos (" + cadena + "). Cada flecha significa «requiere a».");
        }
    }

    private void validarCorrequisitos(String materiaCodigo, List<String> correquisitos) {
        for (String co : correquisitos) {
            if (co.equals(materiaCodigo)) {
                throw new ReglaNegocioException("Una materia no puede ser correquisito de sí misma.");
            }
            if (!materiaRepository.existsByCodigo(co)) {
                throw new RecursoNoEncontradoException("El correquisito " + co + " no existe.");
            }
        }
    }

    // utilidades

    private Carrera obtenerCarreraOFallar(String codigo) {
        return carreraRepository.findById(codigo).orElseThrow(
                () -> new RecursoNoEncontradoException("La carrera " + codigo + " no existe."));
    }

    private Materia obtenerMateriaOFallar(String codigo) {
        return materiaRepository.findById(codigo).orElseThrow(
                () -> new RecursoNoEncontradoException("La materia " + codigo + " no existe."));
    }

    private List<String> normalizar(List<String> codigos) {
        if (codigos == null) {
            return new ArrayList<>();
        }
        return codigos.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String etiqueta(String codigo) {
        return materiaRepository.findById(codigo).map(this::etiquetaDe).orElse(codigo);
    }

    private String etiquetaDe(Materia m) {
        return m.nombre() + " (" + m.codigo() + ")";
    }

    private String etiquetas(List<String> codigos) {
        return codigos.stream().map(this::etiqueta).collect(Collectors.joining(", "));
    }
}
