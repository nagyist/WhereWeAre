package com.dam.t07p02.Modelo;

import android.net.NetworkInfo;

import java.sql.SQLException;
import java.sql.Statement;

public class Localizacion {
    private String dni;
    private double latitud;
    private double longitud;
    private boolean updateCorrecta;
    private Statement st;

    public Localizacion(String dni, double latitud, double longitud) {
        this.dni = dni;
        this.latitud = latitud;
        this.longitud = longitud;
        this.st =ConexionBD.getSt();
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

    private class Thread_actualizarLoc extends Thread
    {
        public void run() {
            String sql="UPDATE `localizacion` SET `latitud`="+latitud+",`longitud`="+longitud+" WHERE dni='"+dni+"'";
            updateCorrecta=false;
            try {
                st.execute(sql);
                updateCorrecta=true;
            } catch (SQLException e) {}
        }
    }

    public boolean actualizarLocalizacion(){
        Thread_actualizarLoc tAL=new Thread_actualizarLoc();
        tAL.start();
        try {
            tAL.join();
        } catch (InterruptedException e) {
            return false;
        }
        return updateCorrecta;
    }
}
