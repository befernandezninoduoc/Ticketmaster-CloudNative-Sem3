package com.duoc.TicketMaster.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad JPA que representa una compra de entradas, relacionada con un evento
 * y preparada para la generación de tickets PDF y reportes de ventas.
 */
@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false)
    private int cantidadEntradas;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(length = 100)
    private String artista = "Artista Invitado"; // Valor por defecto para evitar nulos

    @Column(length = 100)
    private String ubicacion = "Zona General - Santiago"; // Valor por defecto

    @Column(length = 100)
    private String usuarioEmail = "cliente@duocuc.cl"; // Email simulado o real

    protected Compra() {
    }

    // Constructor compatible con tu CompraService original
    public Compra(Evento evento, int cantidadEntradas, BigDecimal precioUnitario, BigDecimal precioTotal) {
        this.evento = evento;
        this.cantidadEntradas = cantidadEntradas;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        if (evento != null) {
            this.artista = evento.getNombre(); // Asigna el nombre del evento como artista por defecto si no viene especificado
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public int getCantidadEntradas() {
        return cantidadEntradas;
    }

    public void setCantidadEntradas(int cantidadEntradas) {
        this.cantidadEntradas = cantidadEntradas;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }
}