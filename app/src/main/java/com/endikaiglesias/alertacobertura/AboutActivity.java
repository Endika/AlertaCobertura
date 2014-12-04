package com.endikaiglesias.alertacobertura;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // | Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();

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
