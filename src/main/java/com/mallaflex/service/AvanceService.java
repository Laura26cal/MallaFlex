package com.mallaflex.service;

import com.mallaflex.entity.*;
import com.mallaflex.repository.HistoriaAcademicaRepository;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvanceService {

    private final CatalogoConsultaService catalogo;
    private final EstadosMallaService estadosService;
    private final HistoriaAcademicaRepository historiaRepo;

    public AvanceService(CatalogoConsultaService catalogo,
                         EstadosMallaService estadosService,
                         HistoriaAcademicaRepository historiaRepo) {
        this.catalogo = catalogo;
        this.estadosService = estadosService;
        this.historiaRepo = historiaRepo;
    }

    public ResumenAcademico obtenerResumen(Long usuarioId, String carreraCodigo) {
        MallaCarrera malla = catalogo.obtenerMalla(carreraCodigo);
        Carrera carrera = malla.carrera();

        // Estado por materia (APROBADA, EN_CURSO, PERDIDA, DESBLOQUEADA, BLOQUEADA)
        Map<String, String> estados = estadosService.estadosMalla(usuarioId, carreraCodigo);

        List<MateriaDetalle> materias = malla.semestres().stream()
            .flatMap(s -> s.materias().stream())
            .toList();

        // Contadores
        int aprobadas = 0, cursando = 0, perdidas = 0, creditosAprobados = 0;
        for (MateriaDetalle m : materias) {
            String e = estados.get(m.codigo());
            if ("APROBADA".equals(e)) { aprobadas++; creditosAprobados += m.creditos(); }
            else if ("EN_CURSO".equals(e)) cursando++;
            else if ("PERDIDA".equals(e)) perdidas++;
        }

        double progreso = carrera.creditosTotales() > 0
            ? creditosAprobados * 100.0 / carrera.creditosTotales() : 0.0;

        return new ResumenAcademico(
            calcularPapa(usuarioId, materias),
            aprobadas, cursando, perdidas, creditosAprobados,
            progreso,
            null,                       // semestre estimado: sale de la ruta (Tarea C)
            resumenPorTipologia(materias, estados));
    }

    // PAPA = suma(nota * creditos) / suma(creditos), con la mejor nota por materia
    private Double calcularPapa(Long usuarioId, List<MateriaDetalle> materiasMalla) {
        Map<String, Integer> creditos = materiasMalla.stream()
            .collect(Collectors.toMap(MateriaDetalle::codigo, MateriaDetalle::creditos, (a, b) -> a));

        // Mejor nota aprobada por materia (ignora registros sin nota)
        Map<String, Double> mejorNota = new HashMap<>();
        for (HistoriaAcademica h : historiaRepo.findByUsuarioId(usuarioId)) {
            if (h.nota() == null || !creditos.containsKey(h.materiaCodigo())) continue;
            mejorNota.merge(h.materiaCodigo(), h.nota(), Math::max);
        }

        double suma = 0; int totalCreditos = 0;
        for (Map.Entry<String, Double> e : mejorNota.entrySet()) {
            int cr = creditos.get(e.getKey());
            suma += e.getValue() * cr;
            totalCreditos += cr;
        }
        return totalCreditos == 0 ? null : suma / totalCreditos;
    }

    private List<ResumenTipologia> resumenPorTipologia(List<MateriaDetalle> materias,
                                                       Map<String, String> estados) {
        Map<String, List<MateriaDetalle>> porTipologia = materias.stream()
            .collect(Collectors.groupingBy(
                m -> m.tipologia() == null ? "SIN_TIPOLOGIA" : m.tipologia(),
                TreeMap::new, Collectors.toList()));

        List<ResumenTipologia> resultado = new ArrayList<>();
        porTipologia.forEach((tip, lista) -> {
            int apr = 0, cur = 0;
            for (MateriaDetalle m : lista) {
                String e = estados.get(m.codigo());
                if ("APROBADA".equals(e)) apr++;
                else if ("EN_CURSO".equals(e)) cur++;
            }
            resultado.add(new ResumenTipologia(tip, lista.size(), apr,
                lista.size() - apr - cur, cur));
        });
        return resultado;
    }
}
