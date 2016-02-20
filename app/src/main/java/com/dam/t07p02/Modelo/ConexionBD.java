package com.dam.t07p02.Modelo;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ConexionBD {

    private Context context;
    private Connection conn;
    private Statement st;
    private boolean errorMySQL;

    // Patrón Singleton (instancia única)
    private static ConexionBD instancia=null;

    // Constructor private para evitar acceso
    private ConexionBD() {
    }

    // Método para obtener la única instancia de la clase
    public static ConexionBD getInstancia() {
        if (instancia==null) {
//            synchronized (ConexionBD.class) {
//                if (instancia==null)
//                    instancia=new ConexionBD();
//            }
            instancia=new ConexionBD();
        }
        return instancia;
    }


    public boolean abrirConexion(Context context) {
        this.context = context;
        return abrirConexionMySQL();

    }

    public boolean cerrarConexion() {
        return cerrarConexionMySQL();

    }

    private void crearTablasMySQL() throws Exception {
        String sql="";
        ResultSet rs=null;
        try {
            sql="SELECT count(*) FROM usuarios;";
            rs=st.executeQuery(sql);
        } catch (Exception e1) {
            try {
                sql="CREATE TABLE usuarios ("+
                        "dni CHAR(9) NOT NULL,"+
                        "contraseña VARCHAR(100) NOT NULL,"+
                        "CONSTRAINT pk_usuarios PRIMARY KEY (dni));";
                st.executeUpdate(sql);
                sql="CREATE TABLE localizacion ("+
                        "dni CHAR(9) NOT NULL,"+
                        "latitud FLOAT NOT NULL,"+
                        "longitud FLOAT NOT NULL,"+
                        "CONSTRAINT pk_localizacion PRIMARY KEY (dni));";
                st.executeUpdate(sql);
            } catch (Exception e2) {
                throw new SQLException("Error crearTablas()!!",e2);
            }
        }
    }

    private class Thread_abrirConexionMySQL extends Thread
    {
        public void run() {
            SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
            String user=pref.getString("pUSerBD", "sql2106998");
            String password=pref.getString("pPSBD", "wR7!vS4%");
            String url= pref.getString("pURLBD","jdbc:mysql://sql2.freesqldatabase.com:3306/");
            url+=user;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                //conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/test");
                conn=DriverManager.getConnection(url, user, password);
                st=conn.createStatement();
                crearTablasMySQL();
            } catch (Exception e) {
                errorMySQL=true;
            }
        }
    }


    private boolean abrirConexionMySQL() {
        errorMySQL=false;
        Thread_abrirConexionMySQL tac=new Thread_abrirConexionMySQL();
        tac.start();
        try {
            tac.join(10000);
        } catch (InterruptedException e) {
            return false;
        }

        return !errorMySQL;
    }

    private class Thread_cerrarConexionMySQL extends Thread
    {
        public void run() {
            try {
                if (st!=null) st.close();
                if (conn!=null) conn.close();
            } catch (Exception e) {
                errorMySQL=true;
            }
        }
    }


    private boolean cerrarConexionMySQL() {
        errorMySQL=false;
        Thread_cerrarConexionMySQL tcc=new Thread_cerrarConexionMySQL();
        tcc.start();
        try {
            tcc.join(10000);
        } catch (InterruptedException e) {
            return false;
        }

        return !errorMySQL;
    }

}



