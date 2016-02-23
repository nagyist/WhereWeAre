package com.dam.t07p02.Modelo;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocalizacionGPS extends IntentService implements LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{

    private LocationManager locM;
    private Location loc;
    private GoogleApiClient gAC;
    private boolean internetConected;
    private ConexionBD bd;
    public static String modificar="MODIF";
    private String usuario;

    public LocalizacionGPS() {
        super("LocalizacionGPS");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.gAC= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.locM= (LocationManager)getSystemService(LOCATION_SERVICE);
        bd=ConexionBD.getInstancia();
        if(bd.isConected())
            getLocation();
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public Location getLocation(){
        try{
            if(!locM.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                internetConected=true;
                this.loc=locM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                this.locM.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,0,this);
            }
        }catch (SecurityException e){}

        return  this.loc;
    }

    @Override
    public void onDestroy() {

        try{
            locM.removeUpdates(this);
        }catch (SecurityException e){}
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getLocation();
        this.usuario=intent.getExtras().getString("usuario");
        bd.actualizarLocalizacion(new Localizacion(usuario,loc.getLatitude(),loc.getLongitude()));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}


