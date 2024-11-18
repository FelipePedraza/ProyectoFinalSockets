package com.proyectofinal.controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.proyectofinal.modelo.AdministradorLogger;
import com.proyectofinal.modelo.Vendedor;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ContactosController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button chatButton;

    @FXML
    private TableColumn<Vendedor, String> columnaApellido;

    @FXML
    private TableColumn<Vendedor, String> columnaDireccion;

    @FXML
    private TableColumn<Vendedor, String> columnaNombre;

    @FXML
    private Button eliminarButton;

    @FXML
    private TableView<Vendedor> tablaContactos;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Vendedor vendedorActual;
    private List<Vendedor> contactos;
    private PerfilVendedorController perfilVendedorController;

    @FXML
    void chatearOpcion(ActionEvent event) {

    }

    @FXML
    void eliminarContacto(ActionEvent event) {

    }

    @FXML
    void initialize() {
        
    }

    @SuppressWarnings("unchecked")
    public void inicializarDatos() throws IOException, ClassNotFoundException{
        conectarAlServidor();
        out.writeObject("CARGAR_CONTACTOS");
        out.writeObject(vendedorActual);
        out.flush();
        this.contactos = (List<Vendedor>) in.readObject();
        cargarDatos(contactos);
    }

    public void setVendedorActual(Vendedor vendedor) {
        this.vendedorActual = vendedor;
    }
    
    public void setPerfilVendedorController(PerfilVendedorController perfilVendedorController) {
        this.perfilVendedorController = perfilVendedorController;
    }

    public void cargarDatos(List<Vendedor> contactos) {

        // Configurar las columnas de la tabla
        columnaNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        columnaDireccion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDireccion()));
        columnaApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellidos()));

        //Limpia la seleccion de la tabla
        tablaContactos.getSelectionModel().clearSelection();

        // Cargar los productos en la tabla
        tablaContactos.getItems().setAll(contactos);
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

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

}
