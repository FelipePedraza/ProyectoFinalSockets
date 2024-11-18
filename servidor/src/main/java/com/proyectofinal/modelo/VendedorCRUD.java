package com.proyectofinal.modelo;

import java.util.ArrayList;
import java.util.List;

import com.proyectofinal.excepciones.AlreadyRegisteredUser;

import java.io.IOException;

public class VendedorCRUD {

    private static final String ARCHIVO_VENDEDORES = AdministradorPropiedades.getInstance().getRuta("persistencia.directory") + "/Vendedores.dat";  // Archivo donde se almacenan los vendedores

    // Método para obtener todos los Vendedores (deserialización)
    @SuppressWarnings("unchecked")
    public List<Vendedor> obtenerTodosLosVendedores() {
        List<Vendedor> vendedores = null;
        // Guardar la lista actualizada en el archivo (serialización)
        HiloSerializador cargar = new HiloSerializador(ARCHIVO_VENDEDORES, "binario", false);
        Thread hilo = new Thread(cargar);
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, e.toString(), java.util.logging.Level.SEVERE);
        }
        vendedores = (List<Vendedor>) cargar.getResultadoDeserializacion();

        // Si no hay vendedores deserializados, retornar una lista vacía
        if (vendedores == null) {
            vendedores = new ArrayList<>();
        }
        return vendedores;
    }

    // Método para crear un nuevo vendedor
    public void registrarVendedor(Vendedor nuevoVendedor) throws IOException, AlreadyRegisteredUser {
        // Obtener la lista actual de vendedores desde el archivo serializado
        List<Vendedor> vendedores = obtenerTodosLosVendedores();

        // Verificar si el vendedor ya está registrado
        for (Vendedor vendedor : vendedores) {
            if (vendedor.getCedula() == nuevoVendedor.getCedula()) {
                AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "El vendedor ya está registrado.", java.util.logging.Level.INFO);
                throw new AlreadyRegisteredUser("El vendedor con la cédula " + nuevoVendedor.getCedula() + " ya está registrado.");
            }
        }

        // Agregar el nuevo vendedor a la lista
        vendedores.add(nuevoVendedor);

        // Guardar la lista actualizada en el archivo (serialización)
        HiloSerializador guardar = new HiloSerializador(vendedores, ARCHIVO_VENDEDORES, "binario", true);
        Thread hilo = new Thread(guardar);
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, e.toString(), java.util.logging.Level.SEVERE);
        }
        AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "Vendedor registrado correctamente.", java.util.logging.Level.INFO);
    }

    // Método para actualizar un vendedor
    public void actualizarVendedor(Vendedor vendedorActualizado) throws IOException {
        List<Vendedor> vendedores = obtenerTodosLosVendedores();
        boolean encontrado = false;

        // Buscar el vendedor por cédula y actualizarlo
        for (int i = 0; i < vendedores.size(); i++) {
            Vendedor vendedorExistente = vendedores.get(i);
            if (vendedorExistente.getCedula() == vendedorActualizado.getCedula()) {
                vendedores.set(i, vendedorActualizado);  // Reemplaza el vendedor
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            // Serializar la lista actualizada de vendedores 
            // Guardar la lista actualizada en el archivo (serialización)
            HiloSerializador guardar = new HiloSerializador(vendedores, ARCHIVO_VENDEDORES, "binario", true);
            Thread hilo = new Thread(guardar);
            hilo.start();
            try {
                hilo.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, e.toString(), java.util.logging.Level.SEVERE);
            }
            AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "Vendedor actualizado correctamente.", java.util.logging.Level.INFO);
        } else {
            AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "No se encontró un vendedor con la cédula: " + vendedorActualizado.getCedula(), java.util.logging.Level.INFO);
        }
    }

    // Método para eliminar un vendedor
    public void eliminarVendedor(int cedula) throws IOException {
        List<Vendedor> vendedores = obtenerTodosLosVendedores();
        boolean eliminado = false;

        // Buscar y eliminar el vendedor por cédula
        for (int i = 0; i < vendedores.size(); i++) {
            Vendedor vendedorExistente = vendedores.get(i);
            if (vendedorExistente.getCedula() == cedula) {
                vendedores.remove(i);  // Elimina el vendedor
                eliminado = true;
                break;
            }
        }

        if (eliminado) {
            // Serializar la lista actualizada después de eliminar el vendedor
            // Guardar la lista actualizada en el archivo (serialización)
            HiloSerializador respaldo = new HiloSerializador(vendedores, ARCHIVO_VENDEDORES, "binario", true);
            Thread hilo = new Thread(respaldo);
            hilo.start();
            try {
                hilo.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, e.toString(), java.util.logging.Level.SEVERE);
            }
            AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "Vendedor eliminado correctamente.", java.util.logging.Level.INFO);
        } else {
            AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "No se encontró un vendedor con la cédula: " + cedula, java.util.logging.Level.INFO);
        }
    }
    // Método para buscar un vendedor por nombre y cédula
    public Vendedor buscarVendedor(String nombre, int cedula) {
        List<Vendedor> vendedores = obtenerTodosLosVendedores();
        for (Vendedor vendedor : vendedores) {
            if (vendedor.getNombre().equals(nombre) && vendedor.getCedula() == cedula) {
                AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "Vendedor encontrado correctamente.", java.util.logging.Level.INFO);
                return vendedor; 
            }
        }
        AdministradorLogger.getInstance().escribirLog(VendedorCRUD.class, "No se encontró un vendedor con la cédula: " + cedula, java.util.logging.Level.INFO);
        return null;  // Si no se encuentra el vendedor
    }
}


