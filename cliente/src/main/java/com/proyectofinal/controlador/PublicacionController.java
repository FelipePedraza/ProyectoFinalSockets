package com.proyectofinal.controlador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.proyectofinal.modelo.AdministradorLogger;
import com.proyectofinal.modelo.Producto;
import com.proyectofinal.modelo.Publicacion;
import com.proyectofinal.modelo.Vendedor;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class PublicacionController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button agregarComentarioBoton;

    @FXML
    private Label autorLabel;

    @FXML
    private AnchorPane comentariosCampo;

    @FXML
    private Label fechaPublicacionLabel;

    @FXML
    private ImageView imagenCampo;

    @FXML
    private Label likeLabel;

    @FXML
    private Button meGustaBoton;

    @FXML
    private Label nombreProductoLabel;

    @FXML
    private Label precioProductoLabel;

    @FXML
    private Button solicitudContactoButton;

    private int cantidadLikes = 0;
    private boolean likeDado = false;
    private Vendedor vendedorActual;
    private Publicacion publicacion;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @FXML
    void AgregarComentario() {

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
    void darMeGusta() {
        if (likeDado) { // Para ver si ya le dió like al post, se le quite el like al presionar el botón de nuevo
            cantidadLikes-=1;
            likeDado = false;
        }
        else{
            cantidadLikes+=1;
            likeDado = true;
        }

    }

    public void SolicitudContacto() throws ClassNotFoundException, IOException{
        conectarAlServidor();
        Vendedor remitente = vendedorActual;
        Vendedor destinatario = publicacion.getVendedor();
        out.writeObject("ENVIAR_SOLICITUD");
        out.writeObject(remitente);
        out.writeObject(destinatario);
        out.flush();

        String respuesta = (String) in.readObject();
        if (respuesta.startsWith("EXITO")){
            mostrarInformacion("EXITO", "Solicitud enviada", "La solicitud fue enviada a " + destinatario.getNombre() + destinatario.getApellidos() );
            AdministradorLogger.getInstance().escribirLog(PublicacionController.class, "Solicitud enviada a " + destinatario.getNombre(), Level.INFO);
        }
        else{
            mostrarAlerta("ERROR", "Solicitud no enviada", "Error enviando la solicitud");
        }
    }

    public void setVendedorActual(Vendedor vendedor) {
        this.vendedorActual = vendedor;
    }

    public void setPublicacion(Publicacion publicacion){
        this.publicacion = publicacion;
        Producto producto = publicacion.getProducto();
        Vendedor vendedor = publicacion.getVendedor();
        byte[] imagenPath = producto.getImagen(); 

        if (imagenPath != null && imagenPath.length > 0) {
            try {
                // Convertir los bytes en una InputStream y luego en una Image de JavaFX
                InputStream imagenStream = new ByteArrayInputStream(imagenPath);
                Image imagen = new Image(imagenStream);
                imagenCampo.setImage(imagen); // Asigna la imagen al componente de interfaz gráfica
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen: " + e.getMessage());
            }
        } else {
            System.out.println("La imagen del producto está vacía o es nula.");
        }
        nombreProductoLabel.setText("Nombre: " + producto.getNombre());
        precioProductoLabel.setText("Precio: $" + producto.getPrecio());
        autorLabel.setText("Publicado por: " + vendedor.getNombre());
        fechaPublicacionLabel.setText("Publicado el: " + LocalDate.now());
        likeLabel.setText("" + cantidadLikes);

    }

    public void habilitarOpciones(){
        if(vendedorActual.getCedula() == publicacion.getVendedor().getCedula()){
            solicitudContactoButton.setVisible(false);
        }
        else{
            solicitudContactoButton.setVisible(true);
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
