package com.duoc.TicketMaster.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompraSolicitud(
        @NotNull @Positive Long eventoId,
        @NotNull @Positive Integer cantidadEntradas,
        String usuarioEmail,
        String artista,
        String ubicacion) {
}