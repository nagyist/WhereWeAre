package com.dam.t07p02.Modelo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.dam.t07p02.R;
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
    private boolean bucaLocalizacion;
    private int tMin;
    private int dMin;


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
        tMin=10;
        dMin=10;
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

    private void getLocation(){
        try{
            if(locM.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
                tMin=pref.getInt("tActualizacion", 10);
                dMin=pref.getInt("dActualizacion", 10);

                this.locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, tMin, dMin, this);
                this.loc=getLastKnownLocation();

            }
        }catch (SecurityException e){e.printStackTrace();}
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.usuario=intent.getExtras().getString("usuario");

        // se obtiene el objeto que gestiona las notificaciones del sistema
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //se crea un objeto que es la notificacion en si
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_p)
                        .setContentTitle(getString(R.string.registrandoSuposicion))
                        .setGroup(getString(R.string.dRegistrandoSuposicion)+"  "+getString(R.string.nTActualizacion)+tMin+"    "+getString(R.string.nDisActualizacion)+dMin);

        // se lanza la notificacion con un id en la barra de notificacioness
        nManager.notify(12345, builder.build());
        nManager.cancel(12346);
        bucaLocalizacion=true;
        while(bucaLocalizacion){
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
    public void onDestroy() {
        try{
            locM.removeUpdates(this);
            // se obtiene el objeto que gestiona las notificaciones del sistema
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //se crea un objeto que es la notificacion en si
            NotificationCompat.Builder builder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_logo)
                            .setContentTitle(getString(R.string.pEegistrandoSuposicion))
                            .setContentText(getString(R.string.dPRegistrandoSuposicion));

            // se lanza la notificacion con un id en la barra de notificacioness
            nManager.cancel(12345);
            nManager.notify(12346, builder.build());
            bucaLocalizacion=false;
        }catch (SecurityException e){}
        super.onDestroy();
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


