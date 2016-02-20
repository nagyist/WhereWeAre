package com.dam.t07p02;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Preferencias extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
