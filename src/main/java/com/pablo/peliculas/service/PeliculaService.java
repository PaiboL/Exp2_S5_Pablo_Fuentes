package com.pablo.peliculas.service;

import com.pablo.peliculas.dto.PeliculaDTO;

import java.util.List;
import java.util.Optional;

public interface PeliculaService {
    List<PeliculaDTO> obtenerTodas();
    Optional<PeliculaDTO> obtenerPorId(Long id);
    PeliculaDTO crear(PeliculaDTO peliculaDTO);
    Optional<PeliculaDTO> actualizar(Long id, PeliculaDTO peliculaDTO);
    boolean eliminar(Long id);
    boolean existePorTituloYDirector(String titulo, String director);
}
