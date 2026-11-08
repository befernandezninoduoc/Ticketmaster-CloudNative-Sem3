package com.duoc.TicketMaster.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTOS")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "PRECIO_ENTRADA", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioEntrada;

    protected Evento() {
    }

    public Evento(String nombre, LocalDateTime fecha, BigDecimal precioEntrada) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.precioEntrada = precioEntrada;
    }

    public void actualizar(String nombre, LocalDateTime fecha, BigDecimal precioEntrada) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.precioEntrada = precioEntrada;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }
}