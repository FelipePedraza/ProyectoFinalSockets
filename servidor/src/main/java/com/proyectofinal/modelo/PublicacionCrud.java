package com.proyectofinal.modelo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.proyectofinal.excepciones.AlreadyRegisteredUser;

public class PublicacionCrud {
    private static final String ARCHIVO_PUBLICACIONES = AdministradorPropiedades.getInstance().getRuta("persistencia.directory") + "/Publicaciones.dat";  // Archivo donde se almacenan los publicaciones

    // Método para obtener todos los publicaciones (deserialización)
    @SuppressWarnings("unchecked")
    public List<Publicacion> obtenerTodosLasPublicaciones() {
        List<Publicacion> publicaciones = null;
            // Guardar la lista actualizada en el archivo (serialización)
            HiloSerializador cargar = new HiloSerializador(publicaciones, ARCHIVO_PUBLICACIONES, "binario", false);
            Thread hilo = new Thread(cargar);
            hilo.start();
            try {
                hilo.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, e.toString(), java.util.logging.Level.SEVERE);
            }
            publicaciones = (List<Publicacion>) cargar.getResultadoDeserializacion();

        // Si no hay publicaciones deserializados, retornar una lista vacía
        if (publicaciones == null) {
            publicaciones = new ArrayList<>();
        }
        return publicaciones;
    }

    // Método para registrar un nuevo publicacion
    public void registrarPublicacion(Publicacion nuevaPublicacion) throws IOException, AlreadyRegisteredUser {
        List<Publicacion> publicaciones = obtenerTodosLasPublicaciones();

        // Verificar si el publicacion ya está registrado (puedes definir tu propia lógica de comparación)
        for (Publicacion publicacion : publicaciones) {
            if (publicacion.getVendedor().equals(nuevaPublicacion.getVendedor())) { // Cambiar esto según tu lógica de identificación
                AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, "El publicacion ya está registrado.", java.util.logging.Level.INFO);
                throw new AlreadyRegisteredUser("El publicacion con el codigo " + nuevaPublicacion.toString() + " ya está registrado.");
            }
        }

        // Agregar el nuevo publicacion a la lista
        publicaciones.add(nuevaPublicacion);

        // Guardar la lista actualizada en el archivo (serialización)
        HiloSerializador guardar = new HiloSerializador(publicaciones, ARCHIVO_PUBLICACIONES, "binario", true);
        Thread hilo = new Thread(guardar);
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, e.toString(), java.util.logging.Level.SEVERE);
        }
        AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, "Publicacion registrado correctamente.", java.util.logging.Level.INFO);
    }

    // Método para actualizar un publicacion
    public void actualizarPublicacion(Publicacion publicacionActualizado) throws IOException {
        List<Publicacion> publicaciones = obtenerTodosLasPublicaciones();
        boolean encontrado = false;

        // Buscar el publicacion por ID y actualizarlo
        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion publicacionExistente = publicaciones.get(i);
            if (publicacionExistente.getVendedor().equals(publicacionActualizado.getVendedor())) {
                publicaciones.set(i, publicacionActualizado);  // Reemplaza el publicacion
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            // Serializar la lista actualizada de publicaciones
            // Guardar la lista actualizada en el archivo (serialización)
            HiloSerializador guardar = new HiloSerializador(publicaciones, ARCHIVO_PUBLICACIONES, "binario", true);
            Thread hilo = new Thread(guardar);
            hilo.start();
            try {
                hilo.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, e.toString(), java.util.logging.Level.SEVERE);
            }
            AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, "Publicacion actualizado correctamente.", java.util.logging.Level.INFO);
        } else {
            AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, "No se encontró un publicacion con el ID: " + publicacionActualizado.toString(), java.util.logging.Level.INFO);
        }
    }

    // Método para eliminar un publicacion
    public void eliminarPublicacion(Publicacion publicacion) throws IOException {
        List<Publicacion> publicaciones = obtenerTodosLasPublicaciones();
        boolean eliminado = false;

        // Buscar y eliminar el publicacion por ID
        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion publicacionExistente = publicaciones.get(i);
            if (publicacionExistente.getVendedor().equals(publicacion.getVendedor())) {
                publicaciones.remove(i);  // Elimina el publicacion
                eliminado = true;
                break;
            }
        }

        if (eliminado) {
            // Serializar la lista actualizada después de eliminar el publicacion
            // Guardar la lista actualizada en el archivo (serialización)
            HiloSerializador guardar = new HiloSerializador(publicaciones, ARCHIVO_PUBLICACIONES, "binario", true);
            Thread hilo = new Thread(guardar);
            hilo.start();
            try {
                hilo.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, e.toString(), java.util.logging.Level.SEVERE);
            }
            
            AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, "Publicacion eliminado correctamente.", java.util.logging.Level.INFO);
        } else {
            AdministradorLogger.getInstance().escribirLog(PublicacionCrud.class, "No se encontró un publicacion con el ID: " + publicacion.toString(), java.util.logging.Level.INFO);
        }
    }
}
