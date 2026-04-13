package com.pablo.peliculas.controller;

import com.pablo.peliculas.dto.PeliculaDTO;
import com.pablo.peliculas.service.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @GetMapping
    public ResponseEntity<List<PeliculaDTO>> obtenerTodas() {
        return ResponseEntity.ok(peliculaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return peliculaService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(mensaje("No se encontró la película con id " + id)));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PeliculaDTO peliculaDTO) {
        if (peliculaService.obtenerPorId(peliculaDTO.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensaje("Ya existe una película con id " + peliculaDTO.getId()));
        }

        if (peliculaService.existePorTituloYDirector(peliculaDTO.getTitulo(), peliculaDTO.getDirector())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensaje("Ya existe una película con el mismo título y director"));
        }

        PeliculaDTO creada = peliculaService.crear(peliculaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody PeliculaDTO peliculaDTO) {
        if (!id.equals(peliculaDTO.getId())) {
            return ResponseEntity.badRequest()
                    .body(mensaje("El id de la URL debe coincidir con el id enviado en el body"));
        }

        return peliculaService.actualizar(id, peliculaDTO)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(mensaje("No se encontró la película con id " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminada = peliculaService.eliminar(id);
        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, String> mensaje(String texto) {
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", texto);
        return respuesta;
    }
}
