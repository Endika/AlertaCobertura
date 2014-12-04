package com.endikaiglesias.alertacobertura;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by endikaig on 1/12/14.
 */
public class Boot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BBDD usdbh = new BBDD(context, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                Log.d(c.getString(0), c.getString(1));

                if("autoinicio".equals(c.getString(0))){
                    if("1".equals(c.getString(1))){
                        Intent servicio = new Intent(context, Servicio.class);
                        context.startService(servicio);
                    }
                }
            } while(c.moveToNext());
        }
    }

}
