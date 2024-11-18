package com.proyectofinal.modelo;

import java.io.Serializable;

public class SolicitudAmistad implements Serializable {
    private static final long serialVersionUID = 1L;

    private Vendedor remitente;  // Quien envía la solicitud
    private Vendedor destinatario;  // Quien recibe la solicitud
    private static final String MENSAJE_PREDETERMINADO = "Te ha enviado una solicitud de amistad."; // Mensaje estándar

    public SolicitudAmistad(Vendedor remitente, Vendedor destinatario) {
        this.remitente = remitente;
        this.destinatario = destinatario;
    }

    public Vendedor getRemitente() {
        return remitente;
    }

    public Vendedor getDestinatario() {
        return destinatario;
    }

    public String getMensaje() {
        return MENSAJE_PREDETERMINADO;
    }

    @Override
    public String toString() {
        return getRemitente().getNombre()+ " " + getRemitente().getApellidos()+ " " + getMensaje();
    }

}


