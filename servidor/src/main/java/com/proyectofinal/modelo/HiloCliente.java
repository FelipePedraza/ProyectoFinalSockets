package com.proyectofinal.modelo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.proyectofinal.excepciones.AlreadyRegisteredUser;
import com.proyectofinal.excepciones.ContactAlreadyExistsException;

public class HiloCliente implements Runnable {

    private Socket socketCliente;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public HiloCliente(Socket socketCliente) {
        this.socketCliente = socketCliente;
        try {
            out = new ObjectOutputStream(socketCliente.getOutputStream());
            in = new ObjectInputStream(socketCliente.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            String comando = (String) in.readObject(); // Leer comando
            switch (comando) {
                case "REGISTRAR_VENDEDOR":
                    Vendedor vendedor = (Vendedor) in.readObject();
                    try {
                        MarketPlaceServicios.getInstance().registrarVendedor(vendedor);
                    } catch (AlreadyRegisteredUser e) {
                        out.writeObject("Error: El usuario ya existe, por favor verificar los datos");
                    }
                    out.writeObject("Vendedor registrado: " + vendedor.getNombre() + " " + vendedor.getApellidos());
                    out.flush();
                    break;
                    
                case "LOGIN":
                    String nombreVendedor = (String) in.readObject();
                    int cedulaVendedor = (int) in.readObject();
                    Vendedor vendedorEncontrado = MarketPlaceServicios.getInstance().buscarVendedor(nombreVendedor, cedulaVendedor);
                    out.writeObject(vendedorEncontrado);
                    out.flush();
                    break;
                case "CARGAR_PRODUCTOS":
                    List<Producto> productos = null;
                    Vendedor vendedorProductos = (Vendedor) in.readObject();
                    productos = vendedorProductos.getProductos();
                    out.writeObject(productos);
                    out.flush();
                    break;
                case "CARGAR_PUBLICACIONES":
                    List<Publicacion> productosPublicados = null;
                    productosPublicados = MarketPlaceServicios.getInstance().cargarPublicaciones();
                    out.writeObject(productosPublicados);
                    out.flush();
                    break;
                case "AGREGAR_PRODUCTO":
                        // Recibe el vendedor actual y el producto completo (incluida la imagen)
                        Vendedor vendedorActual = (Vendedor) in.readObject();
                        Producto nuevoProducto = (Producto) in.readObject();
                
                        // Guarda la imagen en el servidor si el producto contiene una imagen
                        if (nuevoProducto.getImagen() != null) {
                            String fileName = nuevoProducto.getImagenNombre();
                            Path serverPath = Paths.get(AdministradorPropiedades.getInstance().getRuta("archivos.directory") + "/" + fileName);
                            Files.write(serverPath, nuevoProducto.getImagen()); // Escribe los bytes de la imagen en el servidor
                
                            System.out.println("Imagen " + fileName + " recibida y guardada en el servidor.");
                        }
                
                        // Agrega el producto al vendedor
                        vendedorActual.publicarProducto(nuevoProducto);
                        out.writeObject(vendedorActual);
                        out.writeObject("EXITO");
                        out.flush();
                    break;
                case "ELIMINAR_PRODUCTO":
                    Vendedor vendedorActual2 = (Vendedor) in.readObject();
                    Producto producto = (Producto) in.readObject();
                    MarketPlaceServicios.getInstance().eliminarProducto(vendedorActual2, producto);
                    out.writeObject(vendedorActual2);
                    out.writeObject("EXITO");
                    out.flush();
                    break;
                case "EDITAR_PRODUCTO":
                    Vendedor vendedorEditarP = (Vendedor) in.readObject();
                    Producto productoEditar = (Producto) in.readObject();
                    MarketPlaceServicios.getInstance().editarProducto(vendedorEditarP, productoEditar);
                    out.writeObject(vendedorEditarP);
                    out.writeObject("EXITO");
                    out.flush();
                    break;
                case "CARGAR_PRODUCTOS_VENDIDOS":
                    Vendedor vendedorVendidos = (Vendedor) in.readObject();
                    List<Producto> productosVendedor = vendedorVendidos.getProductos();
                    List<Producto> productosVendidos = new ArrayList<>();
                    for(Producto p : productosVendedor){
                        if(p.getEstado().equals(Estado.VENDIDO)){
                            productosVendidos.add(p);
                        }
                    }
                    out.writeObject(productosVendidos);
                    out.writeObject("EXITO");
                    out.flush();
                break;
                case "ENVIAR_SOLICITUD":
                    Vendedor remitente = (Vendedor) in.readObject();
                    Vendedor destinatario = (Vendedor) in.readObject();
                    try {
                        MarketPlaceServicios.getInstance().enviarSolicitud(remitente, destinatario);
                        out.writeObject("EXITO");
                        out.flush();
                    } catch (ContactAlreadyExistsException e) {
                        AdministradorLogger.getInstance().escribirLog(MarketPlaceServicios.class, e.toString(), Level.WARNING);
                    }
                break;
                case "CARGAR_SOLICITUDES":
                    Vendedor vendedorSolicitudes = (Vendedor) in.readObject();
                    List<SolicitudAmistad> solicitudAmistades = vendedorSolicitudes.getSolicitudesRecibidas();
                    out.writeObject(solicitudAmistades);
                    out.flush();
                break;
                case "ACEPTAR_SOLICITUD":
                    Vendedor vendedorAceptar = (Vendedor) in.readObject();
                    SolicitudAmistad solicitudSeleccionada = (SolicitudAmistad) in.readObject();
                    MarketPlaceServicios.getInstance().aceptarSolicitud(vendedorAceptar, solicitudSeleccionada);
                    out.writeObject(vendedorAceptar);
                    out.writeObject("EXITO");
                    out.flush();
                break;
                case "RECHAZAR_SOLICITUD":
                    Vendedor vendedorRechazar = (Vendedor) in.readObject();
                    SolicitudAmistad solicitudEscogida = (SolicitudAmistad) in.readObject();
                    MarketPlaceServicios.getInstance().rechazarSolicitud(vendedorRechazar, solicitudEscogida);
                    out.writeObject(vendedorRechazar);
                    out.writeObject("EXITO");
                    out.flush();
                break;  
                case "CARGAR_CONTACTOS":
                    List<Vendedor> contactos = null;
                    Vendedor vendedorActual3 = (Vendedor) in.readObject();
                    contactos = vendedorActual3.getContactos();
                    out.writeObject(contactos);
                    out.flush();
                    break;      
                default:
                    out.writeObject("Comando no reconocido.");
                    out.flush();
                    break;
            }
        } catch (IOException e) {
                    AdministradorLogger.getInstance().escribirLog(HiloCliente.class, "Error recibiendo el comando", Level.SEVERE);
                } catch (ClassNotFoundException e1) {
                        AdministradorLogger.getInstance().escribirLog(HiloCliente.class, "Error buscando la clase", Level.SEVERE);
                        }finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socketCliente != null && !socketCliente.isClosed()) socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
