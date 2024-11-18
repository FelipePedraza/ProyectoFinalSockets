package com.proyectofinal.modelo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

public class AdministradorArchivo {

    public static void crearEstructuraCarpetas(AdministradorPropiedades propiedades) throws IOException {
        // Obtener la ruta de la carpeta "log" desde config.properties
        String logPath = propiedades.getRuta("log.directory");
    
        // Verificar y crear la carpeta "log" antes de inicializar el logger
        File logDir = new File(logPath);
        if (!logDir.exists()) {
            logDir.mkdirs();  // Crear la carpeta si no existe
            System.out.println("Carpeta 'log' creada.");
        }
        // Inicializar el logger después de crear la carpeta "log"
        AdministradorLogger.getInstance().inicializarLogger(propiedades);
    
        try {
            // Obtener las otras rutas desde config.properties
            String persistenciaPath = propiedades.getRuta("persistencia.directory");
            String respaldoPath = propiedades.getRuta("respaldo.directory");
            String archivosPath = propiedades.getRuta("archivos.directory");
    
            // Verificar y crear la carpeta "persistencia"
            File persistenciaDir = new File(persistenciaPath);
            if (!persistenciaDir.exists()) {
                persistenciaDir.mkdirs();  // Crear la carpeta si no existe
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Carpeta '/td/persistencia' creada en el disco local C.", java.util.logging.Level.INFO);
            } else {
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Carpeta '/td/persistencia' ya existe.", java.util.logging.Level.INFO);
            }
    
            // Verificar y crear la carpeta "respaldo"
            File respaldoDir = new File(respaldoPath);
            if (!respaldoDir.exists()) {
                respaldoDir.mkdirs();  // Crear la carpeta si no existe
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Carpeta 'respaldo' creada.", java.util.logging.Level.INFO);
            } else {
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Carpeta 'respaldo' ya existe.", java.util.logging.Level.INFO);
            }
    
            // Verificar y crear la carpeta "archivos"
            File archivosDir = new File(archivosPath);
            if (!archivosDir.exists()) {
                archivosDir.mkdirs();  // Crear la carpeta si no existe
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Carpeta 'archivos' creada.", java.util.logging.Level.INFO);
            } else {
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Carpeta 'archivos' ya existe.", java.util.logging.Level.INFO);
            }
    
        } catch (Exception e) {
            AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, e.toString(), java.util.logging.Level.SEVERE);
        }
    }

    public static void eliminarImagen(String nombreImagen){
        File archivoImagen = new File(AdministradorPropiedades.getInstance().getRuta("archivos.directory") + "/" + nombreImagen);
        // Verificar si el archivo existe
        if (archivoImagen.exists()) {
            // Intentar eliminar el archivo
            if (archivoImagen.delete()) {
                System.out.println();
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Imagen eliminada exitosamente: " + nombreImagen, Level.INFO);
            } else {
                System.out.println();
                AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "No se pudo eliminar la imagen: " + nombreImagen, Level.INFO);
            }
        } else {
            AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "La imagen no existe: " + nombreImagen, Level.INFO);
        }
    }

    public static void generarReporte(List<Vendedor> vendedores, String nombreArchivo, LocalDate fecha) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(AdministradorPropiedades.getInstance().getRuta("archivos.directory")+ "/" + nombreArchivo + "_" + fecha + ".txt"));
            writer.write("-----Reporte de vendedores y productos-----\n");
            writer.write("--Fecha del reporte: " + fecha + "--\n");

            for(Vendedor vendedor : vendedores){
                writer.write(vendedor.toStringReporte());
            }
            writer.close();
            AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "Reporte de vendedores generado.", Level.INFO);
            
        } catch (IOException e) {
            AdministradorLogger.getInstance().escribirLog(AdministradorArchivo.class, "No se pudo generar el reporte de los vendedores.", Level.WARNING);
        }
    }
}
    


