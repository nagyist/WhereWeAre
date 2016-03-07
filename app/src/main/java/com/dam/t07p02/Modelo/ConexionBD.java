package com.dam.t07p02.Modelo;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dam.t07p02.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static android.provider.Settings.System.getString;


public class ConexionBD {

    private Context context;
    private Connection conn;
    private ArrayList lLocalizaciones;
    private boolean consultaCorrecta;
    private boolean conected;

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
            sql="SELECT count(*) FROM iusuarios;";
            rs=st.executeQuery(sql);
        } catch (Exception e1) {
            try {
                sql="CREATE TABLE iusuarios (\n" +
                        "\t\tdni CHAR(9) NOT NULL,\n" +
                        "\t\tcontraseña VARCHAR(100) NOT NULL,\n" +
                        "\t\tCONSTRAINT pk_usuarios PRIMARY KEY (dni))";
                st.executeUpdate(sql);
                sql="CREATE TABLE ilocalizacion (\n" +
                        "\tdni CHAR(9) NOT NULL,\n" +
                        "\tlatitud FLOAT NOT NULL,\n" +
                        "\tlongitud FLOAT NOT NULL,\n" +
                        "\tfecha_hora datetime NOT NULL,\n" +
                        "\tCONSTRAINT PRIMARY KEY (dni,fecha_hora),\n" +
                        "\tCONSTRAINT FK_IUSUARIOS FOREIGN KEY(dni) REFERENCES iusuarios(dni))";
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

            String user=pref.getString("pUSerBD","");
            String password=pref.getString("pPSBD","");
            String url= pref.getString("pURLBD","");
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
            tac.join();
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
            tcc.join();
        } catch (InterruptedException e) {
            return false;
        }

        return !conected;
    }


    private class Thread_localizacionUsuarios extends Thread
    {
        public void run() {
            String sql="SELECT * \n" +
                        "FROM  `ilocalizacion` ";
            consultaCorrecta=false;
            try {
                ResultSet rs=st.executeQuery(sql);
                while(rs.next()){
                    Localizacion l=new Localizacion(rs.getString(1),rs.getDouble(2),rs.getDouble(3),rs.getDate(4));
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
            t.join();
        } catch (InterruptedException e) {
            return false;
        }

        return consultaCorrecta;
    }

    public boolean isConected() {
        return conected;
    }



}



