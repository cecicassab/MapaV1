package com.cecilia.cassab.mapav1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.cecilia.cassab.mapav1.Util.BancoDadosSingleton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;

public class Mapa extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap map;
    private LatLng Marcador;
    public LocationManager lm;
    public Criteria criteria;
    public String provider;
    public int dist = 0;
    public double distancia;
    public LatLng ATUAL;
    private final int GPSPERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //informações para obter localização em tempo real
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        PackageManager pc = getPackageManager();
        boolean gps = pc.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if(gps){ //verifica se tem gps e define o criterio como fine
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        }
        else{ //se não, usa wifi
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }

        Intent it = getIntent(); //pega intent da classe anterior e extrai os valores do bundle
        Bundle bundle = it.getExtras();
        assert bundle != null;
        Marcador = new LatLng(bundle.getDouble("v1"),bundle.getDouble("v2")); //cria um marcador com os valores passados
        Log.i("Marcador","latitude: "+ bundle.getDouble("v1")+" longitude: "+bundle.getDouble("v2"));

        //cria um fragmento do tipo mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //pega o provedor e chama metodo para achar localização em tempo real
        provider = lm.getBestProvider(criteria,true);
        findlocation();
    }

    @Override
    protected void onDestroy(){
        lm.removeUpdates(this); //remove os updates da localização
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location){ //atualiza a localização
        if(location!=null){
            //cria uma localização no ponto casa em viçosa
            Location casa = new Location(provider);
            casa.setLatitude(-20.7612573916946);
            casa.setLongitude(-42.88164221445456);
            //pega latitude e longitude do ponto atual e cria um objeto do tipo latlong
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            ATUAL = new LatLng(lat,lng);
            distancia = location.distanceTo(casa); //define distancia ate a casa
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("mapa assincrono","iniciou mapa");
        map = googleMap; //pega o mapa e adiciona marcador de acordo com botao apertado na tela anterior
        map.addMarker(new MarkerOptions()
                .position(Marcador)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(Marcador)
                        .tilt(60) //inclinação da camera
                        .zoom(19)
                        .build());
        map.animateCamera(c);
    }

    public void marca_loc(View v){
        requestGPSPermission(); //pede permissão de uso de gps
        Toast.makeText(this, "Distancia ate minha casa: "+distancia+"m", Toast.LENGTH_SHORT).show();
        //adiciona marcador na localização atual do usuario e centraliza a camera
        map.addMarker(new MarkerOptions().position(ATUAL).title("Minha localização atual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(ATUAL)
                        .tilt(60) //inclinação da camera
                        .zoom(19)
                        .build());
        map.animateCamera(c);
    }

    public void requestGPSPermission(){
        //pede permissao de gps caso nao tenha
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,"Permita o uso da câmera para ler QR Code!",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        GPSPERMISSION);
                Log.i("Permission","Devo dar explicação");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        GPSPERMISSION);
                Log.i("Permission","Pede a permissão");

            }
        }else{
            findlocation(); //se ja tiver permissao, acha localização
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //se permissao ja dada, acha localização atual
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findlocation();
            Log.i("Permission","Deu a permissão");
        } else {
            Log.i("Permission","Não permitiu");
        }
    }

    public void findlocation(){
        //se uso de gps permitido, acha loc atual
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (provider != null) {
                lm.requestLocationUpdates(provider, 5000, dist, this);
            }
        } else {
            // Permissão ainda não foi concedida, solicite novamente ou trate isso
            Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
        }
    }
    public void foca_mapa(View v) {
        String tag = v.getTag().toString();
        Log.i("botao", "tag=" + tag);
        if (tag.equals("1")) { //apertou botao de dpi
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            LatLng DPI; //define ponto para centralizar a camera
            //cria cursor para pesquisar dados no bd
            Cursor cursor = BancoDadosSingleton.getInstance().buscar("Location", new String[]{"latitude", "longitude"}, "descricao='DPI'", "");
            cursor.moveToFirst();
            do {
                //percorre o cursor para pegar os dados de latlong do ponto e os salva no objeto
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                Log.d("DB", "Latitude: " + latitude + ", Longitude: " + longitude);

                DPI = new LatLng(latitude, longitude);
            } while (cursor.moveToNext());
            cursor.close();
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                                .target(DPI)
                                .tilt(60) //inclinação da camera
                                .zoom(19)
                                .build());
            map.animateCamera(c);

        } else if (tag.equals("2")) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            LatLng JF; //define ponto para centralizar a camera
            //cria cursor para pesquisar dados no bd
            Cursor cursor = BancoDadosSingleton.getInstance().buscar("Location", new String[]{"latitude", "longitude"}, "descricao='JF'", "");
            cursor.moveToFirst();
            do {
                //percorre o cursor para pegar os dados de latlong do ponto e os salva no objeto
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                Log.d("DB", "Latitude: " + latitude + ", Longitude: " + longitude);

                JF = new LatLng(latitude, longitude);
            } while (cursor.moveToNext());

            cursor.close();
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(JF)
                                .tilt(60) //inclinação da camera
                                .zoom(19)
                                .build());
            map.animateCamera(c);

        } else {
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            LatLng VICOSA; //define ponto para centralizar a camera
            //cria cursor para pesquisar dados no bd
            Cursor cursor = BancoDadosSingleton.getInstance().buscar("Location", new String[]{"latitude", "longitude"}, "descricao='Vicosa'", "");
            cursor.moveToFirst();
            do {
                //percorre o cursor para pegar os dados de latlong do ponto e os salva no objeto
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                Log.d("DB", "Latitude: " + latitude + ", Longitude: " + longitude);

                VICOSA = new LatLng(latitude, longitude);
            } while (cursor.moveToNext());

            cursor.close();
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(VICOSA)
                            .tilt(60) //inclinação da camera
                            .zoom(19)
                            .build());
            map.animateCamera(c);
        }
    }
}
