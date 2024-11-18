package com.proyectofinal;

import java.io.IOException;
import java.util.logging.Level;

import com.proyectofinal.controlador.MuroController;
import com.proyectofinal.controlador.PerfilVendedorController;
import com.proyectofinal.controlador.SolicitudesController;
import com.proyectofinal.modelo.AdministradorLogger;
import com.proyectofinal.modelo.Vendedor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ManejadorEscenas {

    private static Stage primaryStage;

    public static void inicializar(@SuppressWarnings("exports") Stage stage) {
        if (primaryStage == null) {
            primaryStage = stage;
        } else {
            AdministradorLogger.getInstance().escribirLog(ManejadorEscenas.class, "Stage ya ha sido inicializado.", Level.WARNING);
            throw new IllegalStateException("Stage ya ha sido inicializado.");
        }
    }

    public static void cambiarEscena(String fxml) throws IOException {
        if (primaryStage == null) {
            AdministradorLogger.getInstance().escribirLog(ManejadorEscenas.class, "Stage no ha sido inicializado.", Level.WARNING);
            throw new IllegalStateException("Stage no ha sido inicializado.");
        }

        FXMLLoader loader = new FXMLLoader(ManejadorEscenas.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);

        primaryStage.setScene(newScene);

        // Como la ventana del perfil del vendor es muy amplia, esta condición aplica a esa ventana para recortarla.
        if(fxml == "perfilVendedor"){
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
        }
        primaryStage.show();
    }
    //cambiar escena con datos
    public static void cambiarEscenaConDatos(String fxml, Vendedor vendedor) throws IOException, ClassNotFoundException {
        if (primaryStage == null) {
            AdministradorLogger.getInstance().escribirLog(ManejadorEscenas.class, "Stage no ha sido inicializado.", Level.WARNING);
            throw new IllegalStateException("Stage no ha sido inicializado.");
        }

        FXMLLoader loader = new FXMLLoader(ManejadorEscenas.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);

        if ("solicitudes".equals(fxml)) {
            SolicitudesController solicitudesController = loader.getController();
            solicitudesController.setVendedorActual(vendedor);
            solicitudesController.inicializarDatos();
            primaryStage.setScene(newScene);
        }

        if ("muro".equals(fxml)) {
            MuroController muroController = loader.getController();
            muroController.setVendedorActual(vendedor);
            muroController.cargarPublicaciones();
            primaryStage.setScene(newScene);
        }

        // Ajustar tamaño si es la ventana de perfil del vendedor
        if ("perfilVendedor".equals(fxml)) {
            PerfilVendedorController perfilController = loader.getController();
            perfilController.setVendedorActual(vendedor);
            primaryStage.setScene(newScene);
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
        }
        primaryStage.show();
    }
}
