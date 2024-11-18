package com.proyectofinal.controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.proyectofinal.ManejadorEscenas;
import com.proyectofinal.modelo.AdministradorLogger;
import com.proyectofinal.modelo.Publicacion;
import com.proyectofinal.modelo.Vendedor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MuroController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox publicacionesVBox;

    @FXML
    private Button regresarButton;

    private Vendedor vendedorActual;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void Regresar() throws ClassNotFoundException{
        try {
            ManejadorEscenas.cambiarEscenaConDatos("perfilVendedor", vendedorActual);
        } catch (IOException e) {
            e.printStackTrace();
            AdministradorLogger.getInstance().escribirLog(LoginController.class, e.toString() + "Error al cambiar de escena.", Level.SEVERE);
        }
    }
    private void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo conectar al servidor.", e.toString());
            AdministradorLogger.getInstance().escribirLog(AgregarProductoController.class, "Error de conexión con el servidor.", Level.SEVERE);
        }
    }

    public void setVendedorActual(Vendedor vendedor) {
        this.vendedorActual = vendedor;
    }

    @SuppressWarnings("unchecked")
    public void cargarPublicaciones(){
        conectarAlServidor();
        try {
            out.writeObject("CARGAR_PUBLICACIONES");
            out.flush();
            List<Publicacion> publicaciones= (List<Publicacion>) in.readObject();
            publicacionesVBox.getChildren().clear();
            for (Publicacion pu : publicaciones) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectofinal/Publicacion.fxml"));
                Pane publicacionPane = loader.load();
                PublicacionController controller = loader.getController();
                controller.setPublicacion(pu);
                controller.setVendedorActual(vendedorActual);
                controller.habilitarOpciones();
                publicacionesVBox.getChildren().add(publicacionPane);
            }
        }catch (IOException | ClassNotFoundException e) {
            AdministradorLogger.getInstance().escribirLog(MuroController.class, "Error al cargar publicaciones: " + e.toString(), Level.WARNING);
        }
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

}
