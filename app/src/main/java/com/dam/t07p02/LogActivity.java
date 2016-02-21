package com.dam.t07p02;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dam.t07p02.Modelo.ConexionBD;
import com.dam.t07p02.Modelo.Usuario;

import java.sql.SQLException;

public class LogActivity extends AppCompatActivity {

    private Button bEntrar;
    private EditText eTNombre,eTPassWord;
    private TextView tDarseDeAlta;
    private boolean entrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        bEntrar= (Button) findViewById(R.id.bEntrar);
        eTNombre= (EditText) findViewById(R.id.eTNombre);
        eTPassWord= (EditText) findViewById(R.id.eTPasswod);
        tDarseDeAlta= (TextView) findViewById(R.id.tRegistrarse);
        bEntrar.setOnClickListener(lisEntrar);
        tDarseDeAlta.setOnClickListener(listDarseDeAlta);
        this.entrar =true;
        ConexionBD bd= ConexionBD.getInstancia();
        if(bd.abrirConexion(this)){
            Snackbar.make(findViewById(android.R.id.content),"Conexión abierta!",Snackbar.LENGTH_SHORT).show();

        }else{
            Snackbar.make(findViewById(android.R.id.content),"Error en la conexión!",Snackbar.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener lisEntrar=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(entrar){
                if(eTNombre.getText().toString().equals("") && eTPassWord.getText().toString().equals(""))
                    Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaNombreYContrasenia, Snackbar.LENGTH_SHORT).show();
                else if(eTNombre.getText().toString().equals("") && !eTPassWord.getText().toString().equals(""))
                    Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaNombre, Snackbar.LENGTH_SHORT).show();
                else if(!eTNombre.getText().toString().equals("") && eTPassWord.getText().toString().equals(""))
                    Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaContrasenia, Snackbar.LENGTH_SHORT).show();
                else{
                    Usuario u=new Usuario(eTNombre.getText().toString(),eTPassWord.getText().toString());
                    try {
                        if(u.existeUsuario()){
                            if(u.passWordCorrecta()){
                                Intent i=new Intent();
                                i.putExtra("dni",u.getDni());
                                setResult(Activity.RESULT_OK,i);
                                finish();
                            }
                            else
                                Snackbar.make(findViewById(android.R.id.content), R.string.errorPsIncorrecta, Snackbar.LENGTH_SHORT).show();
                        }
                        else
                            Snackbar.make(findViewById(android.R.id.content), R.string.errorNoExisteUsuario, Snackbar.LENGTH_SHORT).show();

                    } catch (SQLException e) {
                        Snackbar.make(findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                if(eTNombre.getText().toString().equals("") && eTPassWord.getText().toString().equals(""))
                    Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaNombreYContrasenia, Snackbar.LENGTH_SHORT).show();
                else if(eTNombre.getText().toString().equals("") && !eTPassWord.getText().toString().equals(""))
                    Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaNombre, Snackbar.LENGTH_SHORT).show();
                else if(!eTNombre.getText().toString().equals("") && eTPassWord.getText().toString().equals(""))
                    Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaContrasenia, Snackbar.LENGTH_SHORT).show();
                else{
                    Usuario u=new Usuario(eTNombre.getText().toString(),eTPassWord.getText().toString());
                    try {
                        if(u.existeUsuario())
                            Snackbar.make(findViewById(android.R.id.content), R.string.errorExisteUsuario, Snackbar.LENGTH_SHORT).show();
                        else {
                            if(u.altaUsuario()){
                                Snackbar.make(findViewById(android.R.id.content), R.string.altaUsuarioCorrecta, Snackbar.LENGTH_SHORT).show();
                                tDarseDeAlta.setText(R.string.registrarse);
                                bEntrar.setText(R.string.entrar);
                                entrar =true;
                            }
                            else
                                Snackbar.make(findViewById(android.R.id.content), R.string.altaUsuarioInCorrecta, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        Snackbar.make(findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
    View.OnClickListener listDarseDeAlta=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(entrar){
                tDarseDeAlta.setText(R.string.entrar);
                bEntrar.setText(R.string.registrarse);
                entrar =false;
            }
            else{
                tDarseDeAlta.setText(R.string.registrarse);
                bEntrar.setText(R.string.entrar);
                entrar =true;
            }


        }
    };
}
