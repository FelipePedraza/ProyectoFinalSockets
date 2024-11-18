package com.proyectofinal.modelo;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;


public class AdministradorPersistencia {

    private static final String CARPETA_PERSISTENCIA = AdministradorPropiedades.getInstance().getRuta("persistencia.directory");
    private static final String CARPETA_RESPALDO = AdministradorPropiedades.getInstance().getRuta("respaldo.directory");

    public static void serializarObjetoXML(Object objeto, String nombre) throws IOException {
        XMLEncoder codificador = null;
        try {
         // Log al inicio
            codificador = new XMLEncoder(new FileOutputStream(nombre));
            codificador.writeObject(objeto);
            // Log de éxito
        } catch (FileNotFoundException e) {
            // Log de error
        } finally {
            if (codificador != null) {
                codificador.close();
            }
        }
    }

    public static Object deserializarObjetoXML(String nombre) throws IOException, ClassNotFoundException {
        XMLDecoder decodificador = null;
        Object objeto = null;
        try {
            // Log al inicio
            decodificador = new XMLDecoder(new FileInputStream(nombre));
            objeto = decodificador.readObject();
           // Log de éxito
        }catch (IOException e) {
            // Log de error
        }finally {
            if (decodificador != null) {
                decodificador.close();
            }
        }
        return objeto;
    }

    public static void serializarObjetoBinario(Object objeto, String nombre) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombre))) {
            oos.writeObject(objeto);
            // Log de info
        } catch (IOException e) {
            // Log de error
            throw e;
        }
    }

    public static Object deserializarObjetoBinario(String nombre) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombre))) {
            Object objeto = ois.readObject();
            // Log de info
            return objeto;
        } catch (IOException | ClassNotFoundException e) {
            // Log de error
            throw e;
        }
    }
     public static void realizarRespaldoCompleto() throws IOException {

        // Lista de archivos de datos que queremos respaldar
        String[] archivosDeDatos = { "Vendedores.dat", "Productos.dat", "Publicaciones.dat" };

        // Obtener la fecha y hora actual para el nombre del respaldo
        String tiempo = new SimpleDateFormat("ddMMyy_HHmmss").format(new Date());

        // Realizar respaldo de cada archivo en la lista
        for (String archivoNombre : archivosDeDatos) {
            File archivoOriginal = new File(CARPETA_PERSISTENCIA + File.separator + archivoNombre);
            if (archivoOriginal.exists()) {
                File archivoRespaldo = new File(CARPETA_RESPALDO + File.separator + archivoNombre.replace(".dat", "") + "_backup_" + tiempo + ".dat");

                try (InputStream in = new FileInputStream(archivoOriginal);
                     OutputStream out = new FileOutputStream(archivoRespaldo)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    AdministradorLogger.getInstance().escribirLog(AdministradorPersistencia.class, "Respaldo realizado para " + archivoNombre + " en: " + archivoRespaldo.getAbsolutePath() , Level.INFO);
                }
            } else {
                AdministradorLogger.getInstance().escribirLog(AdministradorPersistencia.class, "El archivo " + archivoNombre + " no existe en la carpeta de persistencia." , Level.INFO);
            }
        }
    }
}
