package com.proyectofinal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.proyectofinal.modelo.AdministradorArchivo;
import com.proyectofinal.modelo.AdministradorPersistencia;
import com.proyectofinal.modelo.AdministradorPropiedades;
import com.proyectofinal.modelo.HiloCliente;
import com.proyectofinal.modelo.MarketPlaceServicios;

import javafx.application.Application;
import javafx.stage.Stage;

public class ServidorApp extends Application {
    
    private static final int PUERTO = 5000;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public ServidorApp() throws IOException {
        serverSocket = new ServerSocket(PUERTO);
        pool = Executors.newFixedThreadPool(10);  // Hasta 10 clientes concurrentes
        System.out.println("Servidor iniciado en el puerto " + PUERTO);
    }

    public void iniciar() {
        System.out.println("Esperando conexiones de clientes...");
        while (true) {
            try {
                Socket socketCliente =  serverSocket.accept();  // Espera aquí hastaque un cliente se conecte
                System.out.println("Nuevo cliente conectado desde: " + socketCliente.getInetAddress().getHostAddress());
                pool.execute(new HiloCliente(socketCliente));
            } catch (IOException e) {
                System.out.println("Error al aceptar conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        AdministradorPropiedades propiedades = new AdministradorPropiedades();
        AdministradorArchivo.crearEstructuraCarpetas(propiedades);
        AdministradorPersistencia.realizarRespaldoCompleto();
        MarketPlaceServicios.getInstance().generarReporte();
        try {
            new ServidorApp().iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage arg0) {
        
    }
}
