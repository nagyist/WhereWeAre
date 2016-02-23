package com.dam.t07p02.Modelo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import static java.lang.Thread.sleep;

public class LocalizacionGPS extends IntentService implements LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient gAC;
    private LocationManager locM;
    private Location loc;
    private ConexionBD bd;
    private String usuario;

    private LocationRequest mLocationRequest;

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
        this.locM= (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        bd=ConexionBD.getInstancia();
        if(bd.isConected())
            createLocationRequest();
            getLocation();
    }



    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Location getLastKnownLocation() {
        locM = (LocationManager)getApplicationContext().
                getSystemService(LOCATION_SERVICE);
        List<String> providers = locM.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locM.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void getLocation(){
        try{
            if(locM.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                this.locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0, this);
                this.loc=getLastKnownLocation();

            }
        }catch (SecurityException e){e.printStackTrace();}
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

        this.usuario=intent.getExtras().getString("usuario");
        while(true){
            getLocation();
            if(this.loc!=null){
                Localizacion l=new Localizacion(usuario,loc.getLatitude(),loc.getLongitude());
                Log.i("info",usuario+"  La: "+loc.getLatitude()+"    Lo: "+loc.getLongitude());
                l.actualizarLocalizacion();
            }
            try {
                sleep(300);
            } catch (InterruptedException e) {
                ;
            }
        }


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


