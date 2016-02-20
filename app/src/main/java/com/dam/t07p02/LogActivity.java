package com.dam.t07p02;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dam.t07p02.Modelo.Usuario;

import org.w3c.dom.Text;

public class LogActivity extends AppCompatActivity {

    private Button bEntrar;
    private EditText eTNombre,eTPassWord;
    private TextView tDarseDeAlta;


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
    }

    View.OnClickListener lisEntrar=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(eTNombre.getText().toString().equals("") && eTPassWord.getText().toString().equals(""))
                Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaNombreYContrasenia, Snackbar.LENGTH_SHORT).show();
            else if(eTNombre.getText().toString().equals("") && !eTPassWord.getText().toString().equals(""))
                Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaNombre, Snackbar.LENGTH_SHORT).show();
            else if(!eTNombre.getText().toString().equals("") && eTPassWord.getText().toString().equals(""))
                Snackbar.make(findViewById(android.R.id.content), R.string.indroduzcaContrasenia, Snackbar.LENGTH_SHORT).show();
            else{
                Usuario u=new Usuario(eTNombre.getText().toString(),eTPassWord.getText().toString());
                u
            }
        }
    };
    View.OnClickListener listDarseDeAlta=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
