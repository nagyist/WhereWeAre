package com.dam.t07p02;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.dam.t07p02.Modelo.ConexionBD;
import com.dam.t07p02.Modelo.GpsIntentService;
import com.dam.t07p02.Modelo.Localizacion;
import com.dam.t07p02.Modelo.Usuario;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback{

    private Usuario usu;
    private GoogleMap googleMapMA;
    private MapFragment mapFragment;
    private boolean enviandoGps;
    private SharedPreferences pref;
    private CameraUpdate cu;
    private LatLngBounds.Builder builder;
    private boolean zoomActualizado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Intent i=new Intent(MainActivity.this,LogActivity.class);
        startActivityForResult(i, 1);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
        zoomActualizado=true;
    }

    @Override
    protected void onDestroy() {
        Intent i=new Intent(MainActivity.this,GpsIntentService.class);
        stopService(i);
        enviandoGps=false;
        ConexionBD.getInstancia().cerrarConexion();
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(12345);
        nManager.cancel(12346);
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(usu!=null){
            if(!usu.getPassWord().equals(pref.getString("uPass", usu.getPassWord())) && !pref.getString("uPass", usu.getPassWord()).equals("")){
                usu.setPassWord(pref.getString("uPass", ""));
                if(usu.cambioDeContraseña()){
                    Snackbar.make(findViewById(android.R.id.content), R.string.psActualizada, Snackbar.LENGTH_SHORT).show();
                    if(pref.edit().putString("uPass","").commit())
                        ;
                }
            }
            setupMap();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                usu=new Usuario(data.getStringExtra("dni"),data.getStringExtra("ps"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mActualizarPosciones) {
            mapFragment.getMapAsync(MainActivity.this);
            Snackbar.make(findViewById(android.R.id.content),R.string.iMapaActualizado,Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mPreferencias) {
            Intent i = new Intent(MainActivity.this,PreferenciasActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.mCerrarSesion) {
            usu.setDni("");
            usu.setPassWord("");
            Intent i=new Intent(MainActivity.this,LogActivity.class);
            startActivityForResult(i, 1);
            if(enviandoGps){
                Intent ii=new Intent(MainActivity.this,GpsIntentService.class);
                i.putExtra("usuario",usu.getDni());
                stopService(ii);
                enviandoGps=false;
            }
        }
        else if (id == R.id.mActiGPS) {
            if(!enviandoGps){
                Intent i=new Intent(MainActivity.this,GpsIntentService.class);
                i.putExtra("usuario",usu.getDni());
                startService(i);
                enviandoGps=true;
            }else{
                Intent i=new Intent(MainActivity.this,GpsIntentService.class);
                stopService(i);
                enviandoGps=false;
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapMA=googleMap;
        googleMapMA.setOnCameraChangeListener(oCC_listener);
        setupMap();
    }

    GoogleMap.OnCameraChangeListener oCC_listener=new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if(!zoomActualizado){
                googleMapMA.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
                cu = CameraUpdateFactory.newCameraPosition(googleMapMA.getCameraPosition());
                googleMapMA.moveCamera(cu);
                zoomActualizado=true;
            }
        }
    };


    private void setupMap() {
        zoomActualizado=false;
        ArrayList l=new ArrayList();
        if (googleMapMA != null )  {
            googleMapMA.clear();
            ConexionBD bd= ConexionBD.getInstancia();

            if(!bd.isConected())
                bd.abrirConexion(this);

            bd.localizacionUsuarios(l);
            builder = new LatLngBounds.Builder();
            for(Object ll:l){
                LatLng point = new LatLng(((Localizacion) ll).getLatitud(), ((Localizacion) ll).getLongitud());
                googleMapMA.addMarker(new MarkerOptions().position(new LatLng(((Localizacion) ll).getLatitud(), ((Localizacion) ll).getLongitud()))
                        .title(String.valueOf(((Localizacion) ll).getDni() + " - " + ((Localizacion) ll).getFechaHora().toString())));
                builder.include(point);
            }
            }
            else
                Snackbar.make(findViewById(android.R.id.content),R.string.eRConexion,Snackbar.LENGTH_SHORT).show();

        setMapType();
    }


    private void setMapType(){
        String tipo=pref.getString("mapType","Normal");
        if(tipo.equals("None"))
            googleMapMA.setMapType(GoogleMap.MAP_TYPE_NONE);
        else if(tipo.equals("Normal"))
            googleMapMA.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if(tipo.equals("Hybrid"))
            googleMapMA.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else if(tipo.equals("Satelite"))
            googleMapMA.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else if(tipo.equals("Terrain"))
            googleMapMA.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }
}
