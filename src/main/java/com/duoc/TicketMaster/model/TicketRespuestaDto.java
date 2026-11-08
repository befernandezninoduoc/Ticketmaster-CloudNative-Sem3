package com.duoc.TicketMaster.model;

import java.math.BigDecimal;

/**
 * DTO (Record) que encapsula la información detallada de un ticket o compra
 * asociado a un evento específico, evitando la anidación innecesaria de la entidad Evento.
 */
public record TicketRespuestaDto(
        Long id,
        String usuarioEmail,
        int cantidadEntradas,
        BigDecimal precioUnitario,
        BigDecimal precioTotal,
        String artista,
        String ubicacion
) {}