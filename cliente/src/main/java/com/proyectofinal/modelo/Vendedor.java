package com.proyectofinal.modelo;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Vendedor  implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private String apellidos;
    private int cedula; // Identificador único
    private String direccion;
    private List<Vendedor> contactos;   // Lista de contactos (vendedores aliados)
    private Muro muro;                  // Muro donde se publican productos y mensajes
    private List<Producto> productos;   // Lista de productos del vendedor
    private List<SolicitudAmistad> solicitudesRecibidas;

    public Vendedor(String nombre, String apellidos, int cedula, String direccion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.direccion = direccion;
        this.contactos = new ArrayList<>();
        this.muro = new Muro();         // Crear un muro vacío para el vendedor
        this.productos = new ArrayList<>(); // Inicializar la lista de productos
        this.solicitudesRecibidas = new ArrayList<>();
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Vendedor> getContactos() {
        return contactos;
    }

    public Muro getMuro() {
        return muro;
    }

    public List<Producto> getProductos() {
        return productos; // Getter para obtener la lista de productos
    }

    public List<SolicitudAmistad> getSolicitudesRecibidas() {
        return solicitudesRecibidas;
    }

    public void setSolicitudesRecibidas(List<SolicitudAmistad> solicitudesRecibidas) {
        this.solicitudesRecibidas = solicitudesRecibidas;
    }

    @Override
    public String toString() {
        return "Vendedor{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", cedula='" + cedula + '\'' +
                ", direccion='" + direccion + '\'' +
                ", contactos=" + contactos.size() +
                ", productos=" + productos.size() + // Muestra el número de productos
                ", muro=" + muro +
                '}';
    }
}
