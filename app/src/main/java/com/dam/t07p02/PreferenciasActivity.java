package com.dam.t07p02;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by yo on 20/02/2016.
 */
public class PreferenciasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Preferencias())
                .commit();
    }
}
