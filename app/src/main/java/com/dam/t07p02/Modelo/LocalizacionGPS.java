package com.dam.t07p02.Modelo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocalizacionGPS {
    private LocationManager locM;

    public LocalizacionGPS(LocationManager locM,Context c) {
        this.locM = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
    }
}
