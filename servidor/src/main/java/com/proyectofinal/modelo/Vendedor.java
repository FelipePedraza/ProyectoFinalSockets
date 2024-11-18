package com.proyectofinal.modelo;

import java.util.ArrayList;
import java.util.List;

import com.proyectofinal.excepciones.AlreadyRegisteredUser;

import java.io.IOException;
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

    public void setSolicitudesRecibidas(List<SolicitudAmistad> solicitudesRecibidas) {
        this.solicitudesRecibidas = solicitudesRecibidas;
    }

    // Métodos para manejar contactos
    public void agregarContacto(Vendedor contacto) {
        if (!contactos.contains(contacto)) {
            contactos.add(contacto);
        }
    }

    public boolean esContacto(Vendedor vendedor) {
        return contactos.contains(vendedor);
    }

    // Métodos para manejar solicitudes
    public void recibirSolicitud(SolicitudAmistad solicitud) {
        solicitudesRecibidas.add(solicitud);
    }

    public List<SolicitudAmistad> getSolicitudesRecibidas() {
        return solicitudesRecibidas;
    }

    // Método para publicar un producto en el muro y la lista de productos
    public void publicarProducto(Producto producto) {
        productos.add(producto); // Agregar a la lista de productos
        Publicacion publicacion = new Publicacion(this, producto, producto.getFechaPublicacion());
        // Serializar la lista de productos del vendedor
        try {
            ProductoCRUD productoCRUD = new ProductoCRUD(); // Crear instancia de ProductoCRUD
            productoCRUD.registrarProducto(producto); // Registrar el producto
            
        } catch (IOException | AlreadyRegisteredUser e) {
            AdministradorLogger.getInstance().escribirLog(Vendedor.class, e.toString() + " " + "Error al registrar el producto.", java.util.logging.Level.SEVERE);
        }
        try {
            PublicacionCrud publicacionCRUD = new PublicacionCrud();
            publicacionCRUD.registrarPublicacion(publicacion);
            muro.agregarProductoPublicado(publicacion); // Agregar al muro
        } catch (IOException |  AlreadyRegisteredUser e) {
            AdministradorLogger.getInstance().escribirLog(Vendedor.class, e.toString() + " " + "Error al publicar el producto.", java.util.logging.Level.SEVERE);
        }
        try {
            VendedorCRUD vendedorCRUD = new VendedorCRUD(); // Crear instancia de VendedorCRUD
            vendedorCRUD.actualizarVendedor(this); // Actualizar el vendedor
        } catch (IOException e) {
            AdministradorLogger.getInstance().escribirLog(Vendedor.class, e.toString() + " " + "Error al actualizar el vendedor.", java.util.logging.Level.SEVERE);
        }
    }

    // Método para agregar un mensaje al muro
    public void enviarMensaje(Vendedor destinatario, String contenido) {
        Mensaje mensaje = new Mensaje(this, destinatario, contenido);
        this.muro.agregarMensaje(mensaje);
        destinatario.getMuro().agregarMensaje(mensaje); // Añadir mensaje al muro del destinatario
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

    public String toStringReporte(){
        String registro = nombre + " - " + cedula + "\n";
        if(productos.isEmpty()){
            return (registro += "-sin productos- \n");
        }else{
            return (registro += listarProductos());
        }
    }

    public String listarProductos(){
        List<Producto> publicados = new ArrayList<>();                
        List<Producto> vendidos = new ArrayList<>();

        for(Producto producto : productos){
            if(producto.getEstado().equals(Estado.PUBLICADO)){
                publicados.add(producto);
            }
            if(producto.getEstado().equals(Estado.VENDIDO)){
                vendidos.add(producto);
            }
        }

        String lista = "";
        
        if(!publicados.isEmpty()){
            int i = 1;
            lista += "Productos Publicados: \n";
            for(Producto pro : publicados){
                lista += i + "-" + pro.getNombre() + ", $" + pro.getPrecio() + "\n";
                i++;
            }
        }
        if(!vendidos.isEmpty()){
            int j = 1;
            lista += "Productos Vendidos: \n";
            for(Producto pro : vendidos){
                lista += j + "-" + pro.getNombre() + ", $" + pro.getPrecio() + "\n";
                j++;
            }
        }

        return lista;
    }

}
