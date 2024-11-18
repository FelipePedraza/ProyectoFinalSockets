package com.proyectofinal.controlador;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

import com.proyectofinal.modelo.AdministradorLogger;
import com.proyectofinal.modelo.Estado;
import com.proyectofinal.modelo.Producto;
import com.proyectofinal.modelo.Vendedor;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class EliminarProductoController {

    @FXML
    private Button cancelarButton;

    @FXML
    private TableColumn<Producto, String> columnaCategoria;

    @FXML
    private TableColumn<Producto, String> columnaCodigo;

    @FXML
    private TableColumn<Producto, Estado> columnaEstado;

    @FXML
    private TableColumn<Producto, String> columnaNombre;

    @FXML
    private TableColumn<Producto, Double> columnaPrecio;

    @FXML
    private Button eliminarButton;

    @FXML
    private TableView<Producto> tablaProductos;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Vendedor vendedorActual;
    private PerfilVendedorController perfilVendedorController;
    private List<Producto> productos;


    @SuppressWarnings("unchecked")
    public void inicializarDatos() throws IOException, ClassNotFoundException{
        conectarAlServidor();
        out.writeObject("CARGAR_PRODUCTOS");
        out.writeObject(vendedorActual);
        out.flush();
        this.productos = (List<Producto>) in.readObject();
        cargarDatos(productos);
    }
    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
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

    @FXML
    void eliminarProductoSeleccionado() throws ClassNotFoundException, IOException {

        conectarAlServidor();
        
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No se seleccionó ningún producto.", "Por favor, seleccione un producto para eliminar.");
            return;
        }
        
        // Ventana de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este producto?");
        confirmacion.setContentText("Producto: " + productoSeleccionado.getNombre());

        if (confirmacion.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
           
            out.writeObject("ELIMINAR_PRODUCTO");
            out.writeObject(vendedorActual);
            out.writeObject(productoSeleccionado);
            out.flush();
            this.vendedorActual = (Vendedor) in.readObject();
            String mensajeServidor = (String) in.readObject();
            if(mensajeServidor.equals("EXITO")){
                tablaProductos.getItems().remove(productoSeleccionado);
                perfilVendedorController.setVendedorActual(vendedorActual);
                mostrarInformacion("Éxito", "Producto eliminado", "El producto ha sido eliminado exitosamente.");
            }
            
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void cargarDatos(List<Producto> productos) {

        // Configurar las columnas de la tabla
        columnaNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        columnaCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
        columnaCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        columnaPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecio()));
        columnaEstado.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getEstado()));
        //Limpia la seleccion de la tabla
        tablaProductos.getSelectionModel().clearSelection();

        // Cargar los productos en la tabla
        tablaProductos.getItems().setAll(productos);
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
