package com.duoc.TicketMaster.controller;

import com.duoc.TicketMaster.model.Compra;
import com.duoc.TicketMaster.model.CompraRespuesta;
import com.duoc.TicketMaster.model.CompraSolicitud;
import com.duoc.TicketMaster.model.EstadisticaRespuesta;
import com.duoc.TicketMaster.repository.CompraRepository;
import com.duoc.TicketMaster.service.CompraService;
import com.duoc.TicketMaster.service.S3Service;
import com.duoc.TicketMaster.service.TicketPdfService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST encargado de gestionar los endpoints de tickets,
 * incluyendo compra/generación en EFS/S3, descargas, modificaciones, eliminaciones y reportes.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final CompraService compraService;
    private final S3Service s3Service;
    private final TicketPdfService pdfService;
    private final CompraRepository compraRepository;

    public TicketController(
            CompraService compraService,
            S3Service s3Service,
            TicketPdfService pdfService,
            CompraRepository compraRepository) {

        this.compraService = compraService;
        this.s3Service = s3Service;
        this.pdfService = pdfService;
        this.compraRepository = compraRepository;
    }

    /**
     * Endpoint para procesar la compra de entradas, registrar la compra,
     * generar el PDF en EFS y subirlo a AWS S3.
     */
    @PostMapping("/comprar")
    public ResponseEntity<CompraRespuesta> comprarEntradas(@RequestBody CompraSolicitud solicitud) {
        CompraRespuesta respuesta = compraService.comprarEntradas(solicitud);
        return ResponseEntity.ok(respuesta);
    }

    //Endpoint para descargar el archivo PDF del ticket desde S3

    @GetMapping("/{eventoId}/{usuarioEmail}/{ticketId}/descargar")
    public ResponseEntity<InputStreamResource> descargarTicket(
            @PathVariable String eventoId,
            @PathVariable String usuarioEmail,
            @PathVariable Long ticketId) {

        InputStream inputStream = s3Service.descargarTicketS3(eventoId, usuarioEmail, ticketId);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_" + ticketId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // Endpoint para modificar los detalles (como artista o ubicación) de un ticket existente

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarTicket(
            @PathVariable Long id,
            @RequestBody Compra nuevosDetalles) {

        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        String artista = nuevosDetalles.getArtista();
        String ubicacion = nuevosDetalles.getUbicacion();

        if (artista == null || artista.isBlank()
                || ubicacion == null || ubicacion.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes indicar artista y ubicacion");
        }

        // Generar el PDF actualizado
        byte[] pdfBytes = pdfService.generarPdfTicket(
                compra.getEvento().getNombre(),
                artista,
                ubicacion,
                compra.getPrecioTotal().doubleValue(),
                compra.getUsuarioEmail(),
                compra.getId()
        );

        // Reemplazar el PDF existente en S3
        s3Service.subirTicketS3(
                String.valueOf(compra.getEvento().getId()),
                compra.getUsuarioEmail(),
                compra.getId(),
                pdfBytes
        );

        // Actualizar los datos de Oracle
        compra.setArtista(artista);
        compra.setUbicacion(ubicacion);
        compraRepository.save(compra);

        return ResponseEntity.ok(
                "Ticket " + id + " actualizado en Oracle y S3.");
    }

    /**
     * Endpoint para eliminar un ticket específico tanto de S3 como de la base de datos.
     */
    @DeleteMapping("/{eventoId}/{usuarioEmail}/{ticketId}")
    public ResponseEntity<String> eliminarTicket(
            @PathVariable String eventoId,
            @PathVariable String usuarioEmail,
            @PathVariable Long ticketId) {

        s3Service.borrarTicketS3(eventoId, usuarioEmail, ticketId);
        compraRepository.deleteById(ticketId);
        return ResponseEntity.ok("Ticket eliminado correctamente.");
    }

    /**
     * Endpoint para obtener el reporte de estadísticas financieras,
     * total de entradas vendidas y reservas asociadas a un evento.
     */
    @GetMapping("/estadisticas/{eventoId}")
    public ResponseEntity<EstadisticaRespuesta> obtenerEstadisticasEvento(@PathVariable Long eventoId) {
        List<Compra> comprasEvento = compraRepository.findByEventoId(eventoId);

        int totalEntradasVendidas = comprasEvento.stream().mapToInt(Compra::getCantidadEntradas).sum();
        BigDecimal recaudacionTotal = comprasEvento.stream()
                .map(Compra::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        EstadisticaRespuesta reporte = new EstadisticaRespuesta(
                eventoId,
                totalEntradasVendidas,
                recaudacionTotal,
                comprasEvento
        );

        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para filtrar y listar tickets aplicando doble filtro (por eventoId, por usuarioEmail, o ambos).
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<Compra>> filtrarTickets(
            @RequestParam(required = false) Long eventoId,
            @RequestParam(required = false) String usuarioEmail) {

        List<Compra> tickets;

        if (eventoId != null && usuarioEmail != null && !usuarioEmail.isBlank()) {
            tickets = compraRepository.findByEventoIdAndUsuarioEmail(eventoId, usuarioEmail);
        } else if (eventoId != null) {
            tickets = compraRepository.findByEventoId(eventoId);
        } else if (usuarioEmail != null && !usuarioEmail.isBlank()) {
            tickets = compraRepository.findByUsuarioEmail(usuarioEmail);
        } else {
            tickets = compraRepository.findAll();
        }

        return ResponseEntity.ok(tickets);
    }
}