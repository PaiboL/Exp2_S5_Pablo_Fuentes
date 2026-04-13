package com.pablo.peliculas.service.impl;

import com.pablo.peliculas.dto.PeliculaDTO;
import com.pablo.peliculas.model.Pelicula;
import com.pablo.peliculas.repository.PeliculaRepository;
import com.pablo.peliculas.service.PeliculaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaServiceImpl implements PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public PeliculaServiceImpl(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    @Override
    public List<PeliculaDTO> obtenerTodas() {
        return peliculaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<PeliculaDTO> obtenerPorId(Long id) {
        return peliculaRepository.findById(id).map(this::toDTO);
    }

    @Override
    public PeliculaDTO crear(PeliculaDTO peliculaDTO) {
        Pelicula guardada = peliculaRepository.save(toEntity(peliculaDTO));
        return toDTO(guardada);
    }

    @Override
    public Optional<PeliculaDTO> actualizar(Long id, PeliculaDTO peliculaDTO) {
        return peliculaRepository.findById(id)
                .map(peliculaExistente -> {
                    peliculaExistente.setTitulo(peliculaDTO.getTitulo());
                    peliculaExistente.setAnio(peliculaDTO.getAnio());
                    peliculaExistente.setDirector(peliculaDTO.getDirector());
                    peliculaExistente.setGenero(peliculaDTO.getGenero());
                    peliculaExistente.setSinopsis(peliculaDTO.getSinopsis());
                    return toDTO(peliculaRepository.save(peliculaExistente));
                });
    }

    @Override
    public boolean eliminar(Long id) {
        if (!peliculaRepository.existsById(id)) {
            return false;
        }
        peliculaRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existePorTituloYDirector(String titulo, String director) {
        return peliculaRepository.existsByTituloIgnoreCaseAndDirectorIgnoreCase(titulo, director);
    }

    private PeliculaDTO toDTO(Pelicula pelicula) {
        return new PeliculaDTO(
                pelicula.getId(),
                pelicula.getTitulo(),
                pelicula.getAnio(),
                pelicula.getDirector(),
                pelicula.getGenero(),
                pelicula.getSinopsis()
        );
    }

    private Pelicula toEntity(PeliculaDTO peliculaDTO) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(peliculaDTO.getId());
        pelicula.setTitulo(peliculaDTO.getTitulo());
        pelicula.setAnio(peliculaDTO.getAnio());
        pelicula.setDirector(peliculaDTO.getDirector());
        pelicula.setGenero(peliculaDTO.getGenero());
        pelicula.setSinopsis(peliculaDTO.getSinopsis());
        return pelicula;
    }
}
