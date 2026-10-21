package com.duoc.TicketMaster.service;

import com.duoc.TicketMaster.model.Evento;
import com.duoc.TicketMaster.model.NuevoEventoSolicitud;
import com.duoc.TicketMaster.repository.EventoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public Evento crearEvento(NuevoEventoSolicitud solicitud) {
        Evento evento = new Evento(
                solicitud.nombre(),
                solicitud.fecha(),
                solicitud.precioEntrada());
        return eventoRepository.save(evento);
    }
}
