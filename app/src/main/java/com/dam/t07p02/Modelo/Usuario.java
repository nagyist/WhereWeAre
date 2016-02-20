package com.dam.t07p02.Modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Usuario {
    private String dni;
    private String passWord;
    private Statement st;

    public Usuario(String dni, String passWord) {
        this.dni = dni;
        this.passWord = passWord;
        this.st =ConexionBD.getInstancia().getSt();
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
        String sql="insert into usuarios values("+this.dni+","+this.passWord+")";
        st.execute(sql);
        return true;
    }
    public boolean bajaUsuario() throws SQLException {
        String sql="delete from usuarios where dni="+this.dni;
        st.execute(sql);
        return true;
    }
    public boolean existeUsuario() throws SQLException {
        String sql="select count(*) from usuarios where dni="+this.dni;
        ResultSet rs=st.executeQuery(sql);
        return rs.next();
    }
}
