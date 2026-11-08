package com.duoc.TicketMaster.controller;

import com.duoc.TicketMaster.model.Compra;
import com.duoc.TicketMaster.model.Evento;
import com.duoc.TicketMaster.model.NuevoEventoSolicitud;
import com.duoc.TicketMaster.repository.CompraRepository;
import com.duoc.TicketMaster.service.EventoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final CompraRepository compraRepository;

    public EventoController(EventoService eventoService, CompraRepository compraRepository) {
        this.eventoService = eventoService;
        this.compraRepository = compraRepository;
    }

    @GetMapping
    public List<Evento> listarEventos() {
        return eventoService.listarEventos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento crearEvento(@Valid @RequestBody NuevoEventoSolicitud solicitud) {
        return eventoService.crearEvento(solicitud);
    }

    @PutMapping("/{id}")
    public Evento actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody NuevoEventoSolicitud solicitud) {
        return eventoService.actualizarEvento(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
    }

    /**
     * Endpoint para obtener todos los tickets/compras asociados a un evento específico.
     */
    @GetMapping("/{eventoId}/tickets")
    public List<Compra> obtenerTicketsPorEvento(@PathVariable Long eventoId) {
        return compraRepository.findByEventoId(eventoId);
    }
}