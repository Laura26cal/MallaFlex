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

public class ValidadorPrerrequisitos {

    // devuelve el ciclo (ej. [A, B, A]) si guardar los nuevos prerrequisitos lo crea
    public Optional<List<String>> encontrarCiclo(Map<String, List<String>> grafo,
                                                 String materia,
                                                 List<String> nuevosPrerrequisitos) {
        // copia del grafo con los prerrequisitos nuevos de la materia
        Map<String, List<String>> simulado = new HashMap<>();
        if (grafo != null) {
            simulado.putAll(grafo);
        }
        simulado.put(materia, nuevosPrerrequisitos == null ? List.of() : nuevosPrerrequisitos);

        // recorrido en anchura desde la materia; si vuelve a ella hay ciclo
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
