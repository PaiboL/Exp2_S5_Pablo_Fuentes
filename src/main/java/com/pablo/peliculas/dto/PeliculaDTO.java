package com.pablo.peliculas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PeliculaDTO {

    @NotNull(message = "El id es obligatorio")
    @Positive(message = "El id debe ser mayor que cero")
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    private String titulo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1888, message = "El año debe ser válido")
    private Integer anio;

    @NotBlank(message = "El director es obligatorio")
    @Size(max = 120, message = "El director no puede superar 120 caracteres")
    private String director;

    @NotBlank(message = "El género es obligatorio")
    @Size(max = 80, message = "El género no puede superar 80 caracteres")
    private String genero;

    @NotBlank(message = "La sinopsis es obligatoria")
    @Size(max = 500, message = "La sinopsis no puede superar 500 caracteres")
    private String sinopsis;

    public PeliculaDTO() {
    }

    public PeliculaDTO(Long id, String titulo, Integer anio, String director, String genero, String sinopsis) {
        this.id = id;
        this.titulo = titulo;
        this.anio = anio;
        this.director = director;
        this.genero = genero;
        this.sinopsis = sinopsis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }
}
