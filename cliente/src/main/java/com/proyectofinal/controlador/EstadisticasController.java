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
import com.proyectofinal.modelo.Comentario;
import com.proyectofinal.modelo.Estado;
import com.proyectofinal.modelo.Producto;
import com.proyectofinal.modelo.Publicacion;
import com.proyectofinal.modelo.Vendedor;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EstadisticasController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Producto, Double> columnaPrecios;

    @FXML
    private TableColumn<Producto, String> columnaProductos;

    @FXML
    private TableView<Producto> productosVendidosLista;

    @FXML
    private TableView<Comentario> comentariosLista;

    @FXML
    private TableColumn<Comentario, String> columnaComentarios;

    @FXML
    private Button generarReporteButton;

    @FXML
    private Label meGustaLabel;

    @FXML
    private Label vendedorLabel;

    private Vendedor vendedorActual;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @FXML
    void initialize() {
        cargarMeGusta();
        cargarComentarios();
        cargarProductosVendidos();

    }

    private void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            AdministradorLogger.getInstance().escribirLog(AgregarProductoController.class, "Error de conexión con el servidor.", Level.SEVERE);
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarMeGusta(){
        conectarAlServidor();
        try{
            out.writeObject("CARGAR_PUBLICACIONES");
            out.writeObject(vendedorActual);
            out.flush();

            List<Publicacion> publicaciones = (List<Publicacion>) in.readObject();
            int cantidadMeGusta = 0;

            for (Publicacion publicacion : publicaciones) {
                cantidadMeGusta += publicacion.getMeGusta();
            }

            meGustaLabel.setText("Me Gusta recibidos: " + cantidadMeGusta);

        } catch (IOException | ClassNotFoundException e) {
            AdministradorLogger.getInstance().escribirLog(EstadisticasController.class, "Error al cargar cantidad de Me Gusta: " + e.toString(), Level.WARNING);
        }
    }
    

    @SuppressWarnings("unchecked")
    private void cargarComentarios() {
        conectarAlServidor();
        columnaComentarios.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getContenido()));
        try {
            out.writeObject("CARGAR_PUBLICACIONES");
            out.writeObject(vendedorActual);
            out.flush();

            List<Publicacion> publicaciones = (List<Publicacion>) in.readObject();
            ObservableList<Comentario> comentarios = FXCollections.observableArrayList();

            for (Publicacion publicacion : publicaciones) {
                for(Comentario comen : publicacion.getComentarios()){
                    comentarios.add(comen);
                }
            }
            comentariosLista.setItems(comentarios);
        } catch (IOException | ClassNotFoundException e) {
            AdministradorLogger.getInstance().escribirLog(EstadisticasController.class, "Error al cargar comentarios: " + e.toString(), Level.WARNING);
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarProductosVendidos(){
        conectarAlServidor();
        columnaProductos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        columnaPrecios.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecio()));
        try {
            out.writeObject("CARGAR_PRODUCTOS_VENDIDOS");
            out.writeObject(vendedorActual);
            out.flush();

            List<Producto> productos = (List<Producto>) in.readObject();
            ObservableList<Producto> productosIncluidos = FXCollections.observableArrayList();

            for (Producto producto : productos) {
                if(producto.getEstado().equals(Estado.VENDIDO)){
                    productosIncluidos.add(producto);
                }
            }
            productosVendidosLista.getItems().setAll(productosIncluidos);
        } catch (IOException | ClassNotFoundException e) {
            AdministradorLogger.getInstance().escribirLog(EstadisticasController.class, "Error al cargar lista de productos vendidos: " + e.toString(), Level.WARNING);
        }

    }

        @FXML
    void generarReporte(ActionEvent event) {
        
    }

}