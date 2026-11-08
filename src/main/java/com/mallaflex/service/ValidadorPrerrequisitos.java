package com.mallaflex.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Detecta ciclos en el grafo de prerrequisitos ANTES de guardar un cambio.
 *
 * Es una clase de lógica pura: no importa nada de Spring ni toca la base de
 * datos. Recibe el grafo actual ya cargado, lo que permite probarla sin
 * base de datos ni Spring.
 *
 * En el grafo, cada arista "materia -> requisito" significa que la materia
 * exige aprobar el requisito.
 */
public class ValidadorPrerrequisitos {

    /**
     * Simula reemplazar los prerrequisitos de {@code materia} por
     * {@code nuevosPrerrequisitos} y busca si eso crearía un ciclo.
     *
     * @param grafo                 prerrequisitos actuales (materia -> lista de requisitos)
     * @param materia               materia cuyos prerrequisitos se quieren cambiar
     * @param nuevosPrerrequisitos  prerrequisitos que se quieren guardar
     * @return el ciclo como lista de códigos que empieza y termina en {@code materia}
     *         (por ejemplo [A, B, A]), o vacío si no hay ciclo
     */
    public Optional<List<String>> encontrarCiclo(Map<String, List<String>> grafo,
                                                 String materia,
                                                 List<String> nuevosPrerrequisitos) {
        // Copia del grafo con los prerrequisitos de la materia ya reemplazados
        Map<String, List<String>> simulado = new HashMap<>();
        if (grafo != null) {
            simulado.putAll(grafo);
        }
        simulado.put(materia, nuevosPrerrequisitos == null ? List.of() : nuevosPrerrequisitos);

        // Búsqueda en anchura desde la materia: si volvemos a ella, hay ciclo
        Map<String, String> padre = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        Deque<String> cola = new ArrayDeque<>();
        visitados.add(materia);
        cola.add(materia);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            for (String vecino : simulado.getOrDefault(actual, List.of())) {
                if (vecino.equals(materia)) {
                    return Optional.of(reconstruirCamino(padre, actual, materia));
                }
                if (visitados.add(vecino)) {
                    padre.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }
        return Optional.empty();
    }

    /** Atajo: true si el cambio propuesto crearía un ciclo. */
    public boolean crearaCiclo(Map<String, List<String>> grafo,
                               String materia,
                               List<String> nuevosPrerrequisitos) {
        return encontrarCiclo(grafo, materia, nuevosPrerrequisitos).isPresent();
    }

    private List<String> reconstruirCamino(Map<String, String> padre, String ultimo, String materia) {
        LinkedList<String> camino = new LinkedList<>();
        String nodo = ultimo;
        while (nodo != null) {
            camino.addFirst(nodo);
            nodo = padre.get(nodo);
        }
        camino.addLast(materia);
        return camino;
    }
}
