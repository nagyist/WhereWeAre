package com.dam.t07p02.Modelo;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ConexionBD {

    private Context context;
    private Connection conn;
    private ArrayList lLocalizaciones;
    private boolean consultaCorrecta;
    private boolean conected;
    private boolean updateCorrecta;
    private Localizacion u;

    // Patrón Singleton (instancia única)
    private static ConexionBD instancia=null;
    private static Statement st;

    // Constructor private para evitar acceso
    private ConexionBD() {
    }

    // Método para obtener la única instancia de la clase
    public static ConexionBD getInstancia() {
        if (instancia==null) {
            synchronized (ConexionBD.class) {
                if (instancia==null)
                    instancia=new ConexionBD();
            }
            instancia=new ConexionBD();
            if (st==null){
                st= instancia.getSt();
            }
        }
        return instancia;
    }

    public static Statement getSt() {
        return st;
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
            String url= pref.getString("pURLBD", "jdbc:mysql://sql2.freesqldatabase.com:3306/");
            url+=user;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn=DriverManager.getConnection(url, user, password);
                st=conn.createStatement();
                crearTablasMySQL();
            } catch (Exception e) {
                conected =true;
            }
        }
    }


    private boolean abrirConexionMySQL() {
        conected =false;
        Thread_abrirConexionMySQL tac=new Thread_abrirConexionMySQL();
        tac.start();
        try {
            tac.join(10000);
        } catch (InterruptedException e) {
            return false;
        }

        return !conected;
    }

    private class Thread_cerrarConexionMySQL extends Thread
    {
        public void run() {
            try {
                if (st!=null) st.close();
                if (conn!=null) conn.close();
            } catch (Exception e) {
                conected =true;
            }
        }
    }


    private boolean cerrarConexionMySQL() {
        conected =false;
        Thread_cerrarConexionMySQL tcc=new Thread_cerrarConexionMySQL();
        tcc.start();
        try {
            tcc.join(10000);
        } catch (InterruptedException e) {
            return false;
        }

        return !conected;
    }


    private class Thread_localizacionUsuarios extends Thread
    {
        public void run() {
            String sql="SELECT * \n" +
                        "FROM  `localizacion` ";
            consultaCorrecta=false;
            try {
                ResultSet rs=st.executeQuery(sql);
                while(rs.next()){
                    Localizacion l=new Localizacion(rs.getString(1),rs.getDouble(2),rs.getDouble(3));
                    lLocalizaciones.add(l);
                }
                consultaCorrecta=true;
            } catch (SQLException e) {}
        }
    }

    public boolean localizacionUsuarios(ArrayList l){
        lLocalizaciones=l;
        Thread_localizacionUsuarios t=new Thread_localizacionUsuarios();
        t.start();
        try {
            t.join(90000);
        } catch (InterruptedException e) {
            return false;
        }

        return consultaCorrecta;
    }

    public boolean isConected() {
        return conected;
    }


    private class Thread_actualizarLoc extends Thread
    {
        public void run() {
            String sql="UPDATE `localizacion` SET `latitud`="+u.getLatitud()+",`longitud`="+u.getLongitud()+" WHERE dni='"+u.getDni()+"'";
            updateCorrecta=false;
            try {
                st.execute(sql);
                updateCorrecta=true;
            } catch (SQLException e) {}
        }
    }

    public boolean actualizarLocalizacion(Localizacion u){
        this.u=u;
        Thread_actualizarLoc tAL=new Thread_actualizarLoc();
        tAL.start();
        try {
            tAL.join(90000);
        } catch (InterruptedException e) {
            return false;
        }
        return updateCorrecta;
    }
}



