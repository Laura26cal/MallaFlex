package com.mallaflex.service;
  import com.mallaflex.model.*;
  import com.mallaflex.repository.*;
  import com.mallaflex.exception.RecursoNoEncontradoException;
  import java.util.List;
  import java.util.Map;
  import java.util.TreeMap;
  import java.util.stream.Collectors;
  import org.springframework.stereotype.Service;


@Service 
  public class CatalogoConsultaService {
   
    private final CarreraRepository carreraRepo;
    private final MateriaRepository materiaRepo;
    private final MallaRepository mallaRepo;
    private final MateriaOpcionRepository opcionRepo;

    public CatalogoConsultaService(CarreraRepository carreraRepo,
                                   MateriaRepository materiaRepo,
                                   MallaRepository mallaRepo,
                                   MateriaOpcionRepository opcionRepo) {
        this.carreraRepo = carreraRepo;
        this.materiaRepo = materiaRepo;
        this.mallaRepo = mallaRepo;
        this.opcionRepo = opcionRepo; 
    }

    public List<Carrera> listarCarreras() {
    return carreraRepo.findAll();
}

public Carrera obtenerCarrera(String codigo) {
    return carreraRepo.findById(codigo)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "Carrera no encontrada: " + codigo));
}

public List<Materia> listarMaterias() {
    return materiaRepo.findAll();
}

public Materia obtenerMateriaBasica(String codigo) {
    return materiaRepo.findById(codigo)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "Materia no encontrada: " + codigo));
}

public List<String> obtenerPrerrequisitos(String materiaCodigo) {
    obtenerMateriaBasica(materiaCodigo);   // valida que exista
    return materiaRepo.findPrerrequisitosByMateria(materiaCodigo);
}

public List<String> obtenerCorrequisitos(String materiaCodigo) {
    obtenerMateriaBasica(materiaCodigo);   // valida que exista
    return materiaRepo.findCorrequisitosByMateria(materiaCodigo);
}

private MateriaDetalle construirDetalle(MateriaMalla m) {
    return new MateriaDetalle(
        m.codigo(),
        m.nombre(),
        m.creditos(),
        m.tipologia(),
        m.semestreSugerido(),
        m.requiereMallaCompleta(),
        materiaRepo.findPrerrequisitosByMateria(m.codigo()),
        materiaRepo.findCorrequisitosByMateria(m.codigo()));
}

public MallaCarrera obtenerMalla(String carreraCodigo) {
    Carrera carrera = obtenerCarrera(carreraCodigo);   // valida que exista

    Map<Integer, List<MateriaDetalle>> porSemestre =
        mallaRepo.findMateriasByCarrera(carreraCodigo).stream()
            .map(this::construirDetalle)
            .collect(Collectors.groupingBy(
                d -> d.semestreSugerido() == null ? 0 : d.semestreSugerido(),
                TreeMap::new,
                Collectors.toList()));

    List<MallaSemestre> semestres = porSemestre.entrySet().stream()
        .map(e -> new MallaSemestre(
            e.getKey(),
            e.getValue().stream().mapToInt(MateriaDetalle::creditos).sum(),
            e.getValue()))
        .toList();

    int total = semestres.stream().mapToInt(MallaSemestre::creditos).sum();

    return new MallaCarrera(carrera, total, semestres);
}

public MateriaDetalle obtenerMateria(String carreraCodigo, String materiaCodigo) {
    obtenerCarrera(carreraCodigo);
    return mallaRepo.findMateriasByCarrera(carreraCodigo).stream()
        .filter(m -> m.codigo().equals(materiaCodigo))
        .findFirst()
        .map(this::construirDetalle)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "La materia " + materiaCodigo + " no está en la carrera " + carreraCodigo));
}
}
