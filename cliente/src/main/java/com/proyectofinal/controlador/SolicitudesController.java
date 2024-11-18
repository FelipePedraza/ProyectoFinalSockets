package com.proyectofinal.controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

import com.proyectofinal.modelo.AdministradorLogger;
import com.proyectofinal.modelo.SolicitudAmistad;
import com.proyectofinal.modelo.Vendedor;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SolicitudesController {

    @FXML
    private Button botonAceptar;

    @FXML
    private Button botonRechazar;

    @FXML
    private TableColumn<SolicitudAmistad, String> columnaDetalle;

    @FXML
    private TableView<SolicitudAmistad> tablaSolicitudes;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Vendedor vendedorActual;
    private PerfilVendedorController perfilVendedorController;
    private List<SolicitudAmistad> solicitudAmistades;


    @SuppressWarnings("unchecked")
    public void inicializarDatos() throws IOException, ClassNotFoundException{
        conectarAlServidor();
        out.writeObject("CARGAR_SOLICITUDES");
        out.writeObject(vendedorActual);
        out.flush();
        this.solicitudAmistades = (List<SolicitudAmistad>) in.readObject();
        cargarDatos (solicitudAmistades);
    }

    public void cargarDatos(List<SolicitudAmistad> solicitudAmistades) {

        // Configurar las columnas de la tabla
        columnaDetalle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        
        tablaSolicitudes.getSelectionModel().clearSelection();

        // Cargar los productos en la tabla
        tablaSolicitudes.getItems().setAll(solicitudAmistades);
    }
    public void setVendedorActual(Vendedor vendedor) {
        this.vendedorActual = vendedor;
    }
    
    public void setPerfilVendedorController(PerfilVendedorController perfilVendedorController) {
        this.perfilVendedorController = perfilVendedorController;
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

    public void aceptarSolicitud() throws IOException, ClassNotFoundException {
        conectarAlServidor();

        SolicitudAmistad solicitudSeleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitudSeleccionada == null) {
            mostrarAlerta("Error", "No se seleccionó ninguna solicitud.", "Por favor, seleccione una solicitud para aceptar.");
            return;
        }
        out.writeObject("ACEPTAR_SOLICITUD");
        out.writeObject(vendedorActual);
        out.writeObject(solicitudSeleccionada);
        out.flush();
        this.vendedorActual = (Vendedor) in.readObject();
        perfilVendedorController.setVendedorActual(vendedorActual);
        String mensajeServidor = (String) in.readObject();
        if(mensajeServidor.startsWith("EXITO")){
            mostrarInformacion("EXITO", "Solicitud aceptada", "La solicitud se acepto correctamente");
        }
        else{
            mostrarAlerta("ERROR", "Error aceptando", "No se puedo aceptar la solicitud ");
        }

    }

    
    public void rechazarSolicitud() throws IOException, ClassNotFoundException {
        conectarAlServidor();

        SolicitudAmistad solicitudSeleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitudSeleccionada == null) {
            mostrarAlerta("Error", "No se seleccionó ninguna solicitud.", "Por favor, seleccione una solicitud para aceptar.");
            return;
        }
        out.writeObject("RECHAZAR_SOLICITUD");
        out.writeObject(vendedorActual);
        out.writeObject(solicitudSeleccionada);
        out.flush();
        vendedorActual = (Vendedor) in.readObject();
        perfilVendedorController.setVendedorActual(vendedorActual);
        String mensajeServidor = (String) in.readObject();
        if(mensajeServidor.startsWith("EXITO")){
            mostrarInformacion("EXITO", "Solicitud rechazada", "La solicitud se rechazo correctamente");
        }
        else{
            mostrarAlerta("ERROR", "Error rechazando", "No se puedo rechazar la solicitud ");
        }
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Cambiar a tipo de alerta de información
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

}

