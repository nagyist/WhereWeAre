package com.dam.t07p02;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dam.t07p02.Modelo.ConexionBD;
import com.dam.t07p02.Modelo.Localizacion;
import com.dam.t07p02.Modelo.LocalizacionGPS;
import com.dam.t07p02.Modelo.Usuario;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback{

    private Usuario usu;
    private GoogleMap googleMapMA;
    private MapFragment mapFragment;
    private boolean enviandoGps;

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
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
        }
        else if (id == R.id.mActiGPS) {
            if(!enviandoGps){
                Intent i=new Intent(MainActivity.this,LocalizacionGPS.class);
                i.putExtra("usuario",usu.getDni());
                startService(i);
                enviandoGps=true;
            }else{
                Intent i=new Intent(MainActivity.this,LocalizacionGPS.class);
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
        setupMap();
    }


    private void setupMap() {
        if (googleMapMA != null )  {
            googleMapMA.clear();
            ArrayList l=new ArrayList();
            ConexionBD bd= ConexionBD.getInstancia();
            if(bd.isConected()){
                bd.localizacionUsuarios(l);
                for(Object ll:l){
                    googleMapMA.addMarker(new MarkerOptions().position(new LatLng(((Localizacion)ll).getLatitud(),
                            ((Localizacion)ll).getLongitud())).title(((Localizacion)ll).getDni()));
                }
            }
            else if(bd.abrirConexion(this)){
                bd.localizacionUsuarios(l);
                for(Object ll:l){
                    googleMapMA.addMarker(new MarkerOptions().position(new LatLng(((Localizacion)ll).getLatitud(),
                            ((Localizacion)ll).getLongitud())).title(((Localizacion)ll).getDni()));
                }
            }else{
                Snackbar.make(findViewById(android.R.id.content),R.string.eRConexion,Snackbar.LENGTH_SHORT).show();
            }

        }
    }


}
