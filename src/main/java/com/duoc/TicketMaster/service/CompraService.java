package com.duoc.TicketMaster.service;

import com.duoc.TicketMaster.model.Compra;
import com.duoc.TicketMaster.model.CompraRespuesta;
import com.duoc.TicketMaster.model.CompraSolicitud;
import com.duoc.TicketMaster.model.Evento;
import com.duoc.TicketMaster.repository.CompraRepository;
import com.duoc.TicketMaster.repository.EventoRepository;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio encargado de procesar la compra de entradas, registrar la entidad en base de datos,
 * generar el archivo PDF en EFS y sincronizarlo automáticamente con AWS S3.
 */
@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final EventoRepository eventoRepository;
    private final TicketPdfService pdfService;
    private final S3Service s3Service;

    public CompraService(CompraRepository compraRepository, EventoRepository eventoRepository,
                         TicketPdfService pdfService, S3Service s3Service) {
        this.compraRepository = compraRepository;
        this.eventoRepository = eventoRepository;
        this.pdfService = pdfService;
        this.s3Service = s3Service;
    }

    @Transactional
    public CompraRespuesta comprarEntradas(CompraSolicitud solicitud) {
        Evento evento = eventoRepository.findById(solicitud.eventoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento no encontrado"));

        BigDecimal precioUnitario = evento.getPrecioEntrada();
        BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(solicitud.cantidad()));

        // Crear y guardar la compra
        Compra nuevaCompra = new Compra(evento, solicitud.cantidad(), precioUnitario, precioTotal);
        Compra compraGuardada = compraRepository.save(nuevaCompra);

        // Generar PDF en EFS y subir automáticamente a S3 bajo la estructura /eventoID/usuarioX/ticketID.pdf
        try {
            byte[] pdfBytes = pdfService.generarPdfTicket(
                    evento.getNombre(),
                    compraGuardada.getArtista(),
                    compraGuardada.getUbicacion(),
                    precioTotal.doubleValue(),
                    compraGuardada.getUsuarioEmail(),
                    compraGuardada.getId()
            );

            s3Service.subirTicketS3(
                    String.valueOf(evento.getId()),
                    compraGuardada.getUsuarioEmail(),
                    compraGuardada.getId(),
                    pdfBytes
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al generar/subir el ticket a S3: " + e.getMessage(), e);
        }

        return new CompraRespuesta(
                evento.getNombre(),
                solicitud.cantidad(),
                precioUnitario,
                precioTotal);
    }
}