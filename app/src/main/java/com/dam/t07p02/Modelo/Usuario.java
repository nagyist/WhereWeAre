package com.dam.t07p02.Modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Usuario {
    private String dni;
    private String passWord;
    private Statement st;
    private boolean alta;
    private boolean baja;
    private boolean existeUsuario;
    private boolean pSCorrecta;

    public Usuario(String dni, String passWord) {
        this.dni = dni;
        this.passWord = passWord;
        this.st =ConexionBD.getSt();
    }
    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDni() {

        return dni;
    }

    public String getPassWord() {
        return passWord;
    }

    public boolean altaUsuario() throws SQLException {
        TAltaUsuario t=new TAltaUsuario();
        t.start();
        try {
            t.join(10000);
        }
        catch (InterruptedException e) {return false;}
        return alta;
    }
    private class TAltaUsuario extends Thread {
        public void run() {
            alta =true;
            String sql="insert into usuarios values('"+dni+"','"+passWord+"')";
            try {
                st.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                alta =false;
            }
        }
    }
    public boolean bajaUsuario() throws SQLException {
        TBajaUsuario t=new TBajaUsuario();
        t.start();
        try {
            t.join(10000);
        }
        catch (InterruptedException e) {return false;}
        return baja;
    }
    private class TBajaUsuario extends Thread {
        public void run() {
            baja=true;
            String sql="delete from usuarios where dni='"+dni+"'";
            try {
                st.execute(sql);
            } catch (SQLException e) {
                baja=false;
            }
        }
    }
    public boolean existeUsuario() throws SQLException {
        TExisteUsuario t=new TExisteUsuario();
        t.start();
        try {
            t.join(10000);
        }
        catch (InterruptedException e) {return false;}
        return existeUsuario;
    }
    private class TExisteUsuario extends Thread {
        public void run() {
            String sql="select count(*) from usuarios where dni='"+dni+"'";
            ResultSet rs= null;
            try {
                rs = st.executeQuery(sql);
                if(rs==null){
                    existeUsuario=false;
                }
                existeUsuario=true;
            } catch (SQLException e) {
                existeUsuario=false;
            }

        }
    }
    public boolean passWordCorrecta() throws SQLException {
        TPassWordCorrecta t=new TPassWordCorrecta();
        t.start();
        try {
            t.join(10000);
        }
        catch (InterruptedException e) {return false;}
        return pSCorrecta;
    }
    private class TPassWordCorrecta extends Thread {
        public void run() {
            String sql="select contraseña from usuarios where dni='"+dni+"'";
            try {
                ResultSet rs=st.executeQuery(sql);
                pSCorrecta= rs.getString(1).equals(passWord);
                st.execute(sql);
            } catch (SQLException e) {
                pSCorrecta=false;
            }
        }
    }
}
