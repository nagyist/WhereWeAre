package com.dam.t07p02.Modelo;

public class Localizacion {
    private String dni;
    private double latitud;
    private double longitud;

    public Localizacion(String dni, double latitud, double longitud) {
        this.dni = dni;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getDni() {
        return dni;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
