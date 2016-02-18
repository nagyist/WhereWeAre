package com.dam.t07p02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        }
    };
    View.OnClickListener listDarseDeAlta=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
