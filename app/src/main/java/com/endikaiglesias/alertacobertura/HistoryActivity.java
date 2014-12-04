package com.endikaiglesias.alertacobertura;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Vector;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        estadoActual();
    }

    private void estadoActual(){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"estado", "fecha"};
        assert db != null;
        Cursor c = db.query("historial", campos, null, null, null, null,null,null);
        ListView listView = (ListView) findViewById(R.id.lista);
        Vector<String> valores= new Vector<String>();
        String estado;
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                estado=c.getString(0);
                assert estado != null;
                if(estado.equals("conCobertura"))valores.add(getString(R.string.comunicado)+" "+c.getString(1));
                else valores.add(getString(R.string.incomunicado)+" "+c.getString(1));
            } while(c.moveToNext());
        }
        if(c != null)c.close();

        String[] values = new String[valores.size()];

        for(int i=valores.size()-1,j=0;i>=0;i--){values[j++]=valores.get(i);}
        if(values.length>0){

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,  android.R.id.text1 , values);

            // Assign adapter to ListView
            listView.setAdapter(adapter);
        }
    }

    private void vaciar(){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            try {
                //contamos el total que hay
                String[] campos = new String[] {"estado", "fecha"};
                Cursor c = db.query("historial", campos, null, null, null, null,null,null);
                int total = 0;
                if (c.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya m\u00e1s registros
                    do {
                        total++;
                    } while(c.moveToNext());
                }
                if(c != null)c.close();

                //eliminamos todos menos el \u00faltimo
                c = db.query("historial", campos, null, null, null, null,null,null);
                int contador = 1;
                if (c.moveToFirst()) {
                    do {
                        if(contador == total)break;
                        db.execSQL("DELETE FROM historial WHERE estado = '"+c.getString(0)+"' AND fecha = '"+c.getString(1)+"' ");
                        contador++;
                    } while(c.moveToNext());
                }

                //db.execSQL("CREATE TABLE historial (estado TEXT, fecha TEXT)");
                //Cerramos la base de datos
                db.close();

            } catch (Exception ignored) {
            }

        }
        estadoActual();
        tosta("Tabla vaciada");
    }

    private void tosta(String texto){
        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            /*case R.id.menu_cerrar:
                //cierra todos los activitis anteriores ejecutando el principal mandandole un EXIT el cual ejecuta el proceso para cerrarse autom\u00e1ticamente
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;*/
            case R.id.menu_about:
                //Nuevo activity con info
                Intent about = new Intent(this, AboutActivity.class );
                //pasando el parametro direccion con el valor de la caja de texto
                //i.putExtra("direccion", et1.getText().toString());
                startActivity(about);
                return true;
            case R.id.menu_history:
                //Nuevo activity con info
                Intent history = new Intent(this, HistoryActivity.class );
                //pasando el parametro direccion con el valor de la caja de texto
                //i.putExtra("direccion", et1.getText().toString());
                startActivity(history);
                return true;
            case R.id.menu_configurar:
                //Nuevo activity con info
                Intent configurar = new Intent(this, OptionActivity.class );
                //pasando el parametro direccion con el valor de la caja de texto
                //i.putExtra("direccion", et1.getText().toString());
                startActivity(configurar);
                return true;
            case R.id.menu_home:
                //Nuevo activity con info
                Intent main = new Intent(this, MainActivity.class );
                //pasando el parametro direccion con el valor de la caja de texto
                //i.putExtra("direccion", et1.getText().toString());
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
