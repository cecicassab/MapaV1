package com.cecilia.cassab.mapav1.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public final class BancoDadosSingleton {
    protected SQLiteDatabase db;
    private final String NOME_BANCO = "bd_mapa";
    private static BancoDadosSingleton INSTANCE;
    private final String[] SCRIPT_DATABASE_CREATE = new String[] {"CREATE TABLE Location (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    descricao TEXT," +
            "    latitude REAL," +
            "    longitude REAL);",
            "CREATE TABLE Logs (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    msg TEXT," +
                    "    timestamp TEXT," +
                    "    id_location INTEGER," +
                    "    FOREIGN KEY (id_location) REFERENCES Location(id) ON DELETE SET NULL" +
                    ");",
            "INSERT INTO Location (descricao,latitude,longitude) VALUES ('JF',-21.770055644522355,43.37009436496119);",
            "INSERT INTO Location (descricao,latitude,longitude) VALUES ('DPI',-20.764849360229782,-42.86847692249015);",
            "INSERT INTO Location (descricao,latitude,longitude) VALUES ('Vicosa',-20.7612573916946,-42.88164221445456);"
            };

    private BancoDadosSingleton() {
        //obtem contexto da aplicação usando a classe criada para essa finalidade
        Context ctx = MyApp.getAppContext();
        // abre o banco de dados já existente ou então cria um banco novo
        db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        Cursor c = buscar("sqlite_master", null, "type = 'table'", "");
        if(c.getCount() == 1){ //se bd ja nao existia, o cria e o popula
            for (String s : SCRIPT_DATABASE_CREATE) {
                db.execSQL(s);
            }
            Log.i("BANCO_DADOS", "Criou tabelas do banco e as populou.");
        }
        c.close();
        Log.i("BANCO_DADOS", "Abriu conexão com o banco.");
    }

    public long inserir(String tabela, ContentValues valores) { //insert()
        long id = db.insert(tabela, null, valores);
        Log.i("BANCO_DADOS", "Cadastrou registro com o id [" + id + "]");
        return id;
    }

    public int atualizar(String tabela, ContentValues valores, String where) { //update()
        int count = db.update(tabela, valores, where, null);
        Log.i("BANCO_DADOS", "Atualizou [" + count + "] registros");
        return count;
    }

    public int deletar(String tabela, String where) { //delete()
        int count = db.delete(tabela, where, null);
        Log.i("BANCO_DADOS", "Deletou [" + count + "] registros");
        return count;
    }

    public Cursor buscar(String tabela, String colunas[], String where, String orderBy) { //select
        Cursor c;
        if(!where.equals(""))
            c = db.query(tabela, colunas, where, null, null, null, orderBy);
        else
            c = db.query(tabela, colunas, null, null, null, null, orderBy);
        Log.i("BANCO_DADOS", "Realizou uma busca e retornou [" + c.getCount() + "] registros.");
        return c;
    }

    private void abrir() {
        Context ctx = MyApp.getAppContext();
        if(!db.isOpen()){
            // abre o banco de dados já existente
            db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
            Log.i("BANCO_DADOS", "Abriu conexão com o banco.");
        }
    }

    public static BancoDadosSingleton getInstance(){
        if(INSTANCE == null)
            INSTANCE = new BancoDadosSingleton();
        INSTANCE.abrir(); //abre conexão se estiver fechada
        return INSTANCE;
    }

    public void fechar() {
        if (db != null && db.isOpen()) {
            db.close();
            Log.i("BANCO_DADOS", "Fechou conexão com o Banco.");
        }
    }
}
