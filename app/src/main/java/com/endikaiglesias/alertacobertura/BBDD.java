package com.endikaiglesias.alertacobertura;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by endikaig on 1/12/14.
 */
public class BBDD extends SQLiteOpenHelper {
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE historial (estado TEXT, fecha TEXT)";
    String sqlCreateO = "CREATE TABLE opciones (id TEXT, value TEXT)";

    public BBDD(Context context,CursorFactory factory) {
        super(context, "DBalerta_cobertura", factory, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creaci\u00f3n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreateO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aqu\u00ed utilizamos directamente la opci\u00f3n de
        //      eliminar la tabla anterior y crearla de nuevo vac\u00eda con el nuevo formato.
        //      Sin embargo lo normal ser\u00e1 que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este m\u00e9todo deber\u00eda ser m\u00e1s elaborado.

        //Se elimina la versi\u00f3n anterior de la tabla
        //db.execSQL("DROP TABLE IF EXISTS historial");
        db.execSQL("DROP TABLE IF EXISTS opciones");

        //Se crea la nueva versi\u00f3n de la tabla
        //db.execSQL(sqlCreate);
        db.execSQL(sqlCreateO);

        db.execSQL("INSERT INTO opciones (id, value) VALUES ('sonido', '1')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('vibrar', '1')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('tosta', '1')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('barra', '1')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('sonidoTipo', '0')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('autoinicio', '1')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('volumen', '20')");
        db.execSQL("INSERT INTO opciones (id, value) VALUES ('repetir', '0')");
    }

}