package com.duoc.TicketMaster.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Record (DTO) que encapsula las estadísticas de ventas,
 * el total recaudado y el listado de reservas para un evento específico.
 */
public record EstadisticaRespuesta(
        Long eventoId,
        int totalEntradasVendidas,
        BigDecimal recaudacionTotalCLP,
        List<Compra> detalleReservas
) {}