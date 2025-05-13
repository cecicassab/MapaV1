package com.cecilia.cassab.mapav1;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cecilia.cassab.mapav1.Util.BancoDadosSingleton;

import java.time.Instant;

public class MainActivity extends ListActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Tela1","CriandoMapa");
        super.onCreate(savedInstanceState);


        String[] menu = new String [] {"Minha casa em Juiz de Fora","Minha casa em Viçosa", "Meu departamento","Relatório","Fechar aplicação"};
        ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,menu);
        setListAdapter(arrayadapter);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        String aux = l.getItemAtPosition(position).toString();

        //intenção de navegação para a tela de mapa
        Intent it = new Intent(getBaseContext(),Mapa.class);

        Toast.makeText(this, aux, Toast.LENGTH_SHORT).show();
        switch (position){
            case 0: //casa em juiz de fora
                //salva valores lat long em um bundle e os envia para proxima activity
                Bundle bundle = new Bundle();
                bundle.putDouble("v1",-21.770055644522355);
                bundle.putDouble("v2",-43.37009436496119);
                it.putExtras(bundle);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String timestamp = sdf.format(Calendar.getInstance().getTime());

                //cria um log e o insere no bd
                ContentValues contentValues = new ContentValues();
                contentValues.put("msg","JF");
                contentValues.put("timestamp",timestamp);
                contentValues.put("id_location",0);

                BancoDadosSingleton.getInstance().inserir("Logs",contentValues);
                startActivity(it);
                break;
            case 1: //casa viçosa
                Bundle bundle2 = new Bundle();
                bundle2.putDouble("v1",-20.7612573916946);
                bundle2.putDouble("v2",-42.88164221445456);
                it.putExtras(bundle2);

                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String timestamp2 = sdf2.format(Calendar.getInstance().getTime());

                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("msg","Vicosa");
                contentValues2.put("timestamp",timestamp2);
                contentValues2.put("id_location",2);

                BancoDadosSingleton.getInstance().inserir("Logs",contentValues2);
                startActivity(it);
                break;
            case 2: //departamento
                Bundle bundle3 = new Bundle();
                bundle3.putDouble("v1",-20.764849360229782);
                bundle3.putDouble("v2",-42.86847692249015);
                it.putExtras(bundle3);
                SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String timestamp3 = sdf3.format(Calendar.getInstance().getTime());

                ContentValues contentValues3 = new ContentValues();
                contentValues3.put("msg","DPI");
                contentValues3.put("timestamp",timestamp3);
                contentValues3.put("id_location",3);

                BancoDadosSingleton.getInstance().inserir("Logs",contentValues3);
                startActivity(it);
                break;
            case 3: //pagina de relatorio
                Intent it2 = new Intent(getBaseContext(),Report.class);
                startActivity(it2);
                break;
            case 4: //fecha a aplicação
                finish();
                break;
        }
    }
}
