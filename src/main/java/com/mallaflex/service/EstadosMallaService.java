package com.mallaflex.service;

import com.mallaflex.entity.HistoriaAcademica;
import com.mallaflex.entity.MallaCarrera;
import com.mallaflex.entity.MallaSemestre;
import com.mallaflex.entity.MateriaDetalle;
import com.mallaflex.repository.HistoriaAcademicaRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EstadosMallaService {

    private final CatalogoConsultaService catalogo;
    private final HistoriaAcademicaRepository historiaRepo;

    public EstadosMallaService(CatalogoConsultaService catalogo,
                               HistoriaAcademicaRepository historiaRepo) {
        this.catalogo = catalogo;
        this.historiaRepo = historiaRepo;
    }

    public Map<String, String> estadosMalla(Long usuarioId, String carreraCodigo) {
        MallaCarrera malla = catalogo.obtenerMalla(carreraCodigo);   // valida la carrera
        Map<String, String> estadoHistoria = estadoPorMateria(usuarioId);

        Set<String> aprobadas = estadoHistoria.entrySet().stream()
            .filter(e -> e.getValue().equals("APROBADA"))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        Map<String, String> resultado = new LinkedHashMap<>();
        for (MallaSemestre semestre : malla.semestres()) {
            for (MateriaDetalle m : semestre.materias()) {
                String estado = estadoHistoria.get(m.codigo());
                if (estado == null) {
                    estado = aprobadas.containsAll(m.prerrequisitos())
                        ? "DESBLOQUEADA" : "BLOQUEADA";
                }
                resultado.put(m.codigo(), estado);
            }
        }
        return resultado;
    }


    private Map<String, String> estadoPorMateria(Long usuarioId) {
        Map<String, List<HistoriaAcademica>> porMateria =
            historiaRepo.findByUsuarioId(usuarioId).stream()
                .collect(Collectors.groupingBy(HistoriaAcademica::materiaCodigo));

        Map<String, String> resultado = new HashMap<>();
        porMateria.forEach((codigo, registros) -> {
            if (tiene(registros, "APROBADA")) {
                resultado.put(codigo, "APROBADA");
            } else if (tiene(registros, "EN_CURSO")) {
                resultado.put(codigo, "EN_CURSO");
            } else {
                resultado.put(codigo, "PERDIDA");
            }
        });
        return resultado;
    }

    private boolean tiene(List<HistoriaAcademica> registros, String estado) {
        return registros.stream().anyMatch(r -> estado.equalsIgnoreCase(r.estado()));
    }
}