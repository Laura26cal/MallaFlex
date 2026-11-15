package com.mallaflex.service;

import com.mallaflex.entity.*;
import com.mallaflex.repository.ParametroRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private static final int MIN_POR_DEFECTO = 12;
    private static final int MAX_POR_DEFECTO = 20;
    private static final int TOPE_SEMESTRES = 30;

    private final CatalogoConsultaService catalogo;
    private final EstadosMallaService estadosService;
    private final ParametroRepository parametroRepo;

    public RutaService(CatalogoConsultaService catalogo,
                       EstadosMallaService estadosService,
                       ParametroRepository parametroRepo) {
        this.catalogo = catalogo;
        this.estadosService = estadosService;
        this.parametroRepo = parametroRepo;
    }

    public RutaSugerida calcularRuta(Long usuarioId, String carreraCodigo) {
        MallaCarrera malla = catalogo.obtenerMalla(carreraCodigo);
        Map<String, String> estados = estadosService.estadosMalla(usuarioId, carreraCodigo);

        int minimo = parametroRepo.findValorByClave("creditos_minimos").orElse(MIN_POR_DEFECTO);
        int maximo = parametroRepo.findValorByClave("creditos_sobrecarga").orElse(MAX_POR_DEFECTO);

        Map<String, MateriaDetalle> todas = malla.semestres().stream()
            .flatMap(s -> s.materias().stream())
            .collect(Collectors.toMap(MateriaDetalle::codigo, m -> m, (a, b) -> a, LinkedHashMap::new));

        // Ya cumplidas: aprobadas y en curso
        Set<String> cumplidas = new HashSet<>();
        Map<String, MateriaDetalle> pendientes = new LinkedHashMap<>();
        todas.forEach((codigo, m) -> {
            String e = estados.get(codigo);
            if ("APROBADA".equals(e) || "EN_CURSO".equals(e)) cumplidas.add(codigo);
            else pendientes.put(codigo, m);
        });

        Map<String, Integer> cadena = calcularCadenas(todas);
        Map<String, Integer> desbloquea = calcularDesbloqueos(todas);

        List<MallaSemestre> resultado = new ArrayList<>();
        int numero = 1;

        while (!pendientes.isEmpty() && numero <= TOPE_SEMESTRES) {
            List<MateriaDetalle> elegidas = new ArrayList<>();
            int creditos = 0;

            // Candidatas ordenadas por prioridad
            List<MateriaDetalle> candidatas = pendientes.values().stream()
                .sorted(Comparator
                    .comparing((MateriaDetalle m) -> m.requiereMallaCompleta())
                    .thenComparing((MateriaDetalle m) -> -cadena.getOrDefault(m.codigo(), 0))
                    .thenComparing((MateriaDetalle m) -> -desbloquea.getOrDefault(m.codigo(), 0))
                    .thenComparing(MateriaDetalle::codigo))
                .toList();

            boolean agrego = true;
            while (agrego) {
                agrego = false;
                for (MateriaDetalle m : candidatas) {
                    if (!pendientes.containsKey(m.codigo())) continue;
                    if (elegidas.contains(m)) continue;
                    if (!puedeIr(m, cumplidas, elegidas, pendientes)) continue;

                    // La materia junto con sus correquisitos pendientes
                    List<MateriaDetalle> grupo = grupoConCorrequisitos(m, todas, cumplidas, elegidas);
                    if (grupo == null) continue;
                    int extra = grupo.stream().mapToInt(MateriaDetalle::creditos).sum();
                    if (creditos + extra > maximo) continue;

                    elegidas.addAll(grupo);
                    creditos += extra;
                    agrego = true;
                }
            }

            if (elegidas.isEmpty()) break;   // nada se puede programar: evita bucle infinito

            for (MateriaDetalle m : elegidas) pendientes.remove(m.codigo());
            // Las elegidas cuentan como cumplidas solo para el semestre siguiente
            resultado.add(new MallaSemestre(numero, creditos, List.copyOf(elegidas)));
            elegidas.forEach(m -> cumplidas.add(m.codigo()));
            numero++;
        }

        // Si quedaron pendientes sin poder programarse, la ruta es incompleta
        Integer graduacion = pendientes.isEmpty() ? Integer.valueOf(resultado.size()) : null;
        return new RutaSugerida(resultado, graduacion);
    }

    // Una materia puede ir si todos sus prerrequisitos ya están cumplidos en semestres ANTERIORES
    private boolean puedeIr(MateriaDetalle m, Set<String> cumplidas,
                            List<MateriaDetalle> elegidasEsteSemestre,
                            Map<String, MateriaDetalle> pendientes) {
        if (!cumplidas.containsAll(m.prerrequisitos())) return false;
        // Malla completa: solo cuando no queden otras pendientes (aparte de ella y las ya elegidas)
        if (m.requiereMallaCompleta()) {
            long otras = pendientes.keySet().stream()
                .filter(c -> !c.equals(m.codigo()))
                .filter(c -> elegidasEsteSemestre.stream().noneMatch(e -> e.codigo().equals(c)))
                .count();
            return otras == 0;
        }
        return true;
    }

    // Devuelve la materia más sus correquisitos pendientes, o null si algún correquisito no puede ir
    private List<MateriaDetalle> grupoConCorrequisitos(MateriaDetalle m,
                                                      Map<String, MateriaDetalle> todas,
                                                      Set<String> cumplidas,
                                                      List<MateriaDetalle> elegidas) {
        List<MateriaDetalle> grupo = new ArrayList<>();
        grupo.add(m);
        for (String co : m.correquisitos()) {
            if (cumplidas.contains(co)) continue;
            boolean yaElegido = elegidas.stream().anyMatch(e -> e.codigo().equals(co));
            if (yaElegido) continue;
            MateriaDetalle c = todas.get(co);
            if (c == null) continue;                       // correquisito fuera de la malla
            if (!cumplidas.containsAll(c.prerrequisitos())) return null;
            grupo.add(c);
        }
        return grupo;
    }

    // Longitud de la cadena más larga de materias que dependen de esta (incluyéndola)
    private Map<String, Integer> calcularCadenas(Map<String, MateriaDetalle> todas) {
        Map<String, List<String>> dependientes = dependientes(todas);
        Map<String, Integer> memo = new HashMap<>();
        for (String c : todas.keySet()) cadena(c, dependientes, memo, new HashSet<>());
        return memo;
    }

    private int cadena(String codigo, Map<String, List<String>> dep,
                       Map<String, Integer> memo, Set<String> visitando) {
        if (memo.containsKey(codigo)) return memo.get(codigo);
        if (!visitando.add(codigo)) return 0;              // ciclo: se corta
        int max = 0;
        for (String d : dep.getOrDefault(codigo, List.of()))
            max = Math.max(max, cadena(d, dep, memo, visitando));
        visitando.remove(codigo);
        memo.put(codigo, max + 1);
        return max + 1;
    }

    // Cantidad total de materias que se desbloquean (directa o indirectamente)
    private Map<String, Integer> calcularDesbloqueos(Map<String, MateriaDetalle> todas) {
        Map<String, List<String>> dep = dependientes(todas);
        Map<String, Integer> resultado = new HashMap<>();
        for (String c : todas.keySet()) {
            Set<String> vistos = new HashSet<>();
            Deque<String> pila = new ArrayDeque<>(dep.getOrDefault(c, List.of()));
            while (!pila.isEmpty()) {
                String x = pila.pop();
                if (vistos.add(x)) pila.addAll(dep.getOrDefault(x, List.of()));
            }
            resultado.put(c, vistos.size());
        }
        return resultado;
    }

    private Map<String, List<String>> dependientes(Map<String, MateriaDetalle> todas) {
        Map<String, List<String>> dep = new HashMap<>();
        todas.values().forEach(m -> m.prerrequisitos()
            .forEach(p -> dep.computeIfAbsent(p, k -> new ArrayList<>()).add(m.codigo())));
        return dep;
    }
}