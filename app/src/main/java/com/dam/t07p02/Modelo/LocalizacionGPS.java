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
import java.util.List;

import static java.lang.Thread.sleep;

public class LocalizacionGPS extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient gAC;
    private LocationManager locM;
    private Location lastLoc,currentLoc;
    private ConexionBD bd;
    private String usuario;
    private boolean bucaLocalizacion;
    private String tMin;
    private String dMin;
    private double lastLa;
    private double lastLo;
    private Double minutos;
    private double diferencia;
    private SharedPreferences pref;
    private LocationRequest mLocationRequest;
    private String lastUpdateTime;

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
        gAC.connect();
        this.locM= (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        bd=ConexionBD.getInstancia();
        tMin="10";
        dMin="10";
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        diferencia=Double.parseDouble(pref.getString("Diferencia", "0.001"));
        minutos=10.0;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.usuario=intent.getExtras().getString("usuario");
        lanzaNotificacion();

        bucaLocalizacion=true;
        while(bucaLocalizacion){

//            if (lastLoc!=null && lastLoc.distanceTo(currentLoc) > diferencia) {
            if (lastLoc!=null) {
                Localizacion l=new Localizacion(usuario, lastLoc.getLatitude(), lastLoc.getLongitude());
                Log.i("info",usuario+"  La: "+lastLa+"    Lo: "+lastLo);
                l.actualizarLocalizacion();
            }
            try {
                Thread.sleep(5000);
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
                        .setContentTitle(getString(R.string.registrandoSuposicion))
                        .setGroup(getString(R.string.dRegistrandoSuposicion)+"  "+getString(R.string.nTActualizacion)+tMin+"    "+getString(R.string.nDisActualizacion)+dMin);


        nManager.notify(12345, builder.build());
        nManager.cancel(12346);
    }

    private boolean worth(double la,double lo){
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        diferencia=Double.parseDouble(pref.getString("Diferencia", "0.001"));
        Log.i("info","  La: "+la+"    Lo: "+lo+"    dif : "+diferencia);
        if(la-lastLa>diferencia){
            lastLa=la;
            lastLo=lo;
            return true;
        }
        if(lo-lastLo>diferencia){
            lastLa=la;
            lastLo=lo;
            return true;
        }
        lastLa=la;
        lastLo=lo;
        return false;
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
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                gAC, (com.google.android.gms.location.LocationListener) this);
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
    public void onConnectionSuspended(int i) {

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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}


