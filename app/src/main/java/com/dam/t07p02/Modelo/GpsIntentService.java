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

public class GpsIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient gAC;
    private LocationManager locM;
    private Location lastLoc,currentLoc;
    private ConexionBD bd;
    private String usuario;
    private boolean bucaLocalizacion;
    private double lastLa;
    private double lastLo;
    private Double minutos;
    private double diferencia;
    private SharedPreferences pref;
    private LocationRequest mLocationRequest;
    private String lastUpdateTime;

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
        this.locM= (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        bd=ConexionBD.getInstancia();
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
//            if (lastLoc!=null && lastLoc.distanceTo(currentLoc) > diferencia) {
            if (lastLoc!=null && currentLoc!=null) {
                Localizacion l=new Localizacion(usuario, lastLoc.getLatitude(), lastLoc.getLongitude());
                Log.i("infoooo","LAST_LOC   "+usuario+"  La: "+lastLoc.getLatitude()+"          Lo: "+lastLoc.getLongitude());
                Log.i("infoooo","CURRENT_LOC"+usuario+"  La: "+currentLoc.getLatitude()+"       Lo: "+currentLoc.getLongitude());
                l.actualizarLocalizacion();
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
            bd.cerrarConexion();
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
            currentLoc=location;
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
            Date now = new Date();
            lastUpdateTime= sdfDate.format(now);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}