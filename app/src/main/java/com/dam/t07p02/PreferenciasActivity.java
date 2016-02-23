package com.dam.t07p02;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class PreferenciasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Preferencias())
                .commit();
    }
}
