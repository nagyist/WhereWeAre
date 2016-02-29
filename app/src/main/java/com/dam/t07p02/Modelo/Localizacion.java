package com.dam.t07p02.Modelo;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Localizacion {
    private String dni;
    private double latitud;
    private double longitud;
    private Date fechaHora;
    private boolean updateCorrecta;
    private Statement st;

    public Localizacion(String dni,double longitud, double latitud , Date fechaHora) {
        this.longitud = longitud;
        this.dni = dni;
        this.fechaHora = fechaHora;
        this.latitud = latitud;
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

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Date getFechaHora() {

        return fechaHora;
    }

    private class Thread_actualizarLoc extends Thread
    {
        public void run() {
            String sql="INSERT INTO `ilocalizacion`(`dni`, `latitud`, `longitud`, `fecha_hora`) VALUES ('"+dni+"',"+latitud+","+longitud+",now())";
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
