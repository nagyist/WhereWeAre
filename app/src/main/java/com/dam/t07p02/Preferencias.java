package com.dam.t07p02;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by yo on 20/02/2016.
 */
public class Preferencias extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
