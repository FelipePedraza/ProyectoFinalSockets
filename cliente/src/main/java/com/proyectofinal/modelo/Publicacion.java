package com.proyectofinal.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Publicacion implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private Producto producto;
    private Vendedor vendedor;
    private List<Comentario> comentarios;
    private LocalDateTime fechaPublicacion;
    private double meGusta;

    // Constructor
    public Publicacion(Vendedor vendedor, Producto producto, LocalDateTime fechaPublicacion) {
        this.producto = producto;
        this.comentarios = new ArrayList<>();
        this.fechaPublicacion = fechaPublicacion;
        this.vendedor = vendedor;
        this.meGusta = 0;
    }
    // Gets y sets
    public Vendedor getVendedor() {
        return vendedor;
    }
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public double getMeGusta() {
        return meGusta;
    }
    public void setMeGusta(double meGusta) {
        this.meGusta = meGusta;
    }

}
