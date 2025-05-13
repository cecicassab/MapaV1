package com.cecilia.cassab.mapav1.Util;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static Context context;
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
    }
    public static Context getAppContext() {
//método usado para recuperar o context do app
//de qualquer parte do programa
        return MyApp.context;
    }
}
