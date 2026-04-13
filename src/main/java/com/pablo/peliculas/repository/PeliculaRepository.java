package com.pablo.peliculas.repository;

import com.pablo.peliculas.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    boolean existsByTituloIgnoreCaseAndDirectorIgnoreCase(String titulo, String director);
}
