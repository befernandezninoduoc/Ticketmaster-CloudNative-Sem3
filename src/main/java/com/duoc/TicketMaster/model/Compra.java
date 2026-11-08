package com.duoc.TicketMaster.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad JPA que representa una compra de entradas, relacionada con un evento
 * y preparada para la generación de tickets PDF y reportes de ventas.
 */
@Entity
@Table(name = "COMPRAS")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENTO_ID", nullable = false)
    private Evento evento;

    @Column(name = "CANTIDAD_ENTRADAS", nullable = false)
    private int cantidadEntradas;

    @Column(name = "PRECIO_UNITARIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "PRECIO_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "ARTISTA", length = 100)
    private String artista = "Artista Invitado";

    @Column(name = "UBICACION", length = 100)
    private String ubicacion = "Zona General - Santiago";

    @Column(name = "USUARIO_EMAIL", length = 100)
    private String usuarioEmail = "cliente@duocuc.cl";

    protected Compra() {
    }

    public Compra(Evento evento, int cantidadEntradas, BigDecimal precioUnitario, BigDecimal precioTotal) {
        this.evento = evento;
        this.cantidadEntradas = cantidadEntradas;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        if (evento != null) {
            this.artista = evento.getNombre();
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