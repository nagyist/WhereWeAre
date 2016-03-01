package com.dam.t07p02.Modelo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.dam.t07p02.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GpsIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient gAC;
    private Location currentLoc;
    private Location lastLoc;
    private String usuario;
    private boolean bucaLocalizacion;
    private Double minutos;
    private double diferencia;
    private SharedPreferences pref;
    private LocationRequest mLocationRequest;

    public GpsIntentService() {
        super("GpsIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.gAC= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        gAC.connect();
        pref= PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.usuario=intent.getExtras().getString("usuario");
        lanzaNotificacion();

        bucaLocalizacion=true;
        while(bucaLocalizacion){

            diferencia=Double.parseDouble(pref.getString("Diferencia", "0.0"));
            minutos=Double.parseDouble(pref.getString("tActualizacion", "0.0"));
            if(mLocationRequest!=null){
                mLocationRequest.setInterval((long)(minutos * 60 * 1000));
                mLocationRequest.setFastestInterval((long)(minutos * 60 * 1000));
            }
            Random r=new Random();
//            if (lastLoc!=null && currentLoc!=null && lastLoc.distanceTo(currentLoc) > diferencia) {
            if (lastLoc!=null && currentLoc!=null) {
//                new Localizacion(usuario, r.nextInt(10)-15, r.nextInt(10)-15,new Date()).actualizarLocalizacion();
                new Localizacion(usuario, currentLoc.getLatitude(), currentLoc.getLongitude(),new Date()).actualizarLocalizacion();
                Log.i("infoooo", "LAST_LOC   " + usuario + "  La: " + lastLoc.getLatitude() + "          Lo: " + lastLoc.getLongitude());
                Log.i("infoooo", "CURRENT_LOC" + usuario + "  La: " + currentLoc.getLatitude() + "       Lo: " + currentLoc.getLongitude());
            }
            try {
                Thread.sleep((long) (minutos*60*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
    private void lanzaNotificacion(){
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_p)
                        .setContentTitle(getString(R.string.registrandoSuposicion));
        nManager.notify(12345, builder.build());
        nManager.cancel(12346);
    }

    @Override
    public void onDestroy() {
        try{
            if (gAC.isConnected()) {
                stopLocationUpdates();
                gAC.disconnect();
            }
            lanzarNotifParada();
            bucaLocalizacion=false;
            super.onDestroy();
        }catch (SecurityException e){}
        super.onDestroy();
    }
    private void lanzarNotifParada(){

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_logo)
                        .setContentTitle(getString(R.string.pEegistrandoSuposicion))
                        .setContentText(getString(R.string.dPRegistrandoSuposicion));
        nManager.cancel(12345);
        nManager.notify(12346, builder.build());
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                gAC, this);
        if (gAC.isConnected())
            gAC.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Math.round(minutos) * 60 * 1000);
        mLocationRequest.setFastestInterval(Math.round(minutos) * 60 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(gAC, mLocationRequest, this);
            lastLoc = LocationServices.FusedLocationApi.getLastLocation(gAC);
        } catch (SecurityException e ){
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            lastLoc=currentLoc;
            currentLoc=location;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}