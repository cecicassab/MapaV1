package com.cecilia.cassab.mapav1;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cecilia.cassab.mapav1.Util.BancoDadosSingleton;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class Report extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = getIntent();

        List<String> menu = new ArrayList<>();
        Cursor cursor = BancoDadosSingleton.getInstance().buscar("Logs", new String[]{"msg", "timestamp"}, "", "");

        // verifica se o cursor tem dados antes de mover
        if (cursor.moveToFirst()) {
            do {
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("msg"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                menu.add(msg + " - " + timestamp);
            } while (cursor.moveToNext());
        }
        cursor.close();


        ArrayAdapter<String> arrayadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        setListAdapter(arrayadapter);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String aux = l.getItemAtPosition(position).toString();
        String[] text = aux.split(" ");
        String msg = text[0];
        String timestamp = text[2]+" "+text[3];
        Log.i("relatorio",msg+" "+timestamp);

        String where = "L.id=Ls.id and Ls.msg='" + msg + "' and Ls.timestamp='" + timestamp + "'";
        Cursor cursor = BancoDadosSingleton.getInstance().buscar("Location L, Logs Ls", new String[]{"L.latitude", "L.longitude"},where, "");
        cursor.moveToFirst();
        do {
            //percorre o cursor para pegar os dados de latlong do ponto e os salva no objeto
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            Log.i("relatorio", "Latitude: " + latitude + ", Longitude: " + longitude);
            aux = "Latitude: "+latitude+" Longitude: "+longitude;

        } while (cursor.moveToNext());
        cursor.close();
        Toast.makeText(this, aux, Toast.LENGTH_SHORT).show();
    }
}