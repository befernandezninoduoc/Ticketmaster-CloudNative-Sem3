package com.duoc.TicketMaster.controller;

import com.duoc.TicketMaster.model.Compra;
import com.duoc.TicketMaster.model.CompraRespuesta;
import com.duoc.TicketMaster.model.CompraSolicitud;
import com.duoc.TicketMaster.model.EstadisticaRespuesta;
import com.duoc.TicketMaster.repository.CompraRepository;
import com.duoc.TicketMaster.service.CompraService;
import com.duoc.TicketMaster.service.S3Service;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final CompraRepository compraRepository;

    public TicketController(CompraService compraService, S3Service s3Service, CompraRepository compraRepository) {
        this.compraService = compraService;
        this.s3Service = s3Service;
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

    /**
     * Endpoint para descargar el archivo PDF del ticket desde S3 utilizando los filtros correspondientes.
     */
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

    /**
     * Endpoint para modificar los detalles (como artista o ubicación) de un ticket existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarTicket(@PathVariable Long id, @RequestBody Compra nuevosDetalles) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        compra.setArtista(nuevosDetalles.getArtista());
        compra.setUbicacion(nuevosDetalles.getUbicacion());
        compraRepository.save(compra);

        return ResponseEntity.ok("Detalles del ticket " + id + " actualizados con éxito.");
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
}