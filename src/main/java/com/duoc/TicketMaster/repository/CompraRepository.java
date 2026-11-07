package com.duoc.TicketMaster.repository;

import com.duoc.TicketMaster.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad Compra, encargado de las operaciones de base de datos
 * y consultas personalizadas para el reporte de estadísticas y filtros.
 */
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Método que busca todas las compras asociadas a un ID de evento específico
    List<Compra> findByEventoId(Long eventoId);

    // Método que busca todas las compras realizadas por un usuario específico (por email)
    List<Compra> findByUsuarioEmail(String usuarioEmail);

    // Doble filtro: Busca compras que coincidan exactamente con el evento y el usuario
    List<Compra> findByEventoIdAndUsuarioEmail(Long eventoId, String usuarioEmail);
}