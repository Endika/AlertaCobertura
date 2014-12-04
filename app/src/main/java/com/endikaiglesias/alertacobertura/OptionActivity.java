package com.endikaiglesias.alertacobertura;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class OptionActivity extends Activity {
    CheckBox cTosta,
            cBarra,
            cVibrar,
            cSonido,
            cBoot;
    SeekBar bVol;
    Spinner cTipoSonido,
            cRepe;
    TextView cLabelSonido,
            cLabelVol,
            cLabelRepe;
    BBDD usdbh;
    SQLiteDatabase db;
    String[] mili = new String[] {"0","5000","15000","30000","60000","120000"};
    String[] mili2 = new String[] {"No","5","15","30","60","120"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        cTosta = (CheckBox) findViewById(R.id.cTosta);
        cBarra = (CheckBox) findViewById(R.id.cBarra);
        cVibrar = (CheckBox) findViewById(R.id.cVibrar);
        cSonido = (CheckBox) findViewById(R.id.cSonido);
        cBoot = (CheckBox) findViewById(R.id.cBoot);
        cTipoSonido = (Spinner) findViewById(R.id.cTipoSonido);
        cLabelSonido = (TextView) findViewById(R.id.cLabelTipo);
        cLabelVol = (TextView) findViewById(R.id.cLabelTipo2);
        bVol = (SeekBar) findViewById(R.id.bVolumen);
        cRepe = (Spinner) findViewById(R.id.temporizador);
        cLabelRepe = (TextView) findViewById(R.id.textoTemporizador);

        usdbh = new BBDD(this, null);
        db = usdbh.getWritableDatabase();

        cTosta.setOnClickListener(cOption);
        cBarra.setOnClickListener(cOption);
        cVibrar.setOnClickListener(cOption);
        cSonido.setOnClickListener(cOption);
        cBoot.setOnClickListener(cOption);
        bVol.setOnSeekBarChangeListener(cOptionBar);
        cTipoSonido.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition()==1){
                    db.execSQL("UPDATE opciones SET value='1' WHERE id='sonidoTipo'");
                }else{
                    db.execSQL("UPDATE opciones SET value='0' WHERE id='sonidoTipo'");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cRepe.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.execSQL("UPDATE opciones SET value='"+mili[adapterView.getSelectedItemPosition()]+"' WHERE id='repetir'");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] campos = new String[] {"id", "value"};

        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);

        Boolean iniciar=true;
        int aux=0;
        //if (c.moveToFirst())iniciar=false;
        c.moveToFirst();
        do {
            aux++;
        } while(c.moveToNext());

        if(c != null)c.close();

        if(aux == 8)iniciar=false;

        if(iniciar){//iniciamos los valores por defecto
            usdbh.onUpgrade(db,1,1);
        }

        //cargamos los datos de la BBDD
        cTosta.setChecked(valueOpcion("tosta"));
        cBarra.setChecked(valueOpcion("barra"));
        cVibrar.setChecked(valueOpcion("vibrar"));
        cSonido.setChecked(valueOpcion("sonido"));
        cBoot.setChecked(valueOpcion("autoinicio"));
        bVol.setProgress(valueOpcionInt("volumen"));
        cLabelVol.setText(getString(R.string.ConfiguraLabelTipo2)+": "+bVol.getProgress());

        if (cSonido.isChecked()) {
            cTipoSonido.setEnabled(true);
            cTipoSonido.setVisibility(View.VISIBLE);
            cLabelSonido.setEnabled(true);
            cLabelSonido.setVisibility(View.VISIBLE);
            cLabelVol.setVisibility(View.VISIBLE);
            cLabelVol.setEnabled(true);
            bVol.setVisibility(View.VISIBLE);
            bVol.setEnabled(true);
        }else{
            cTipoSonido.setEnabled(false);
            cTipoSonido.setVisibility(View.GONE);
            cLabelSonido.setEnabled(false);
            cLabelSonido.setVisibility(View.GONE);
            cLabelVol.setVisibility(View.GONE);
            cLabelVol.setEnabled(false);
            bVol.setVisibility(View.GONE);
            bVol.setEnabled(false);
        }

        final String[] datos = new String[]{"Defecto","Notificaci\u00f3n"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, datos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cTipoSonido.setAdapter(adaptador);
        if(!valueOpcion("sonidoTipo")){
            cTipoSonido.setSelection(0);
        }else{
            cTipoSonido.setSelection(1);
        }

        ArrayAdapter<String> adaptador2 = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mili2);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cRepe.setAdapter(adaptador2);
        String valorRepe=valueOpcionString("repetir");
        for(int i=mili2.length-1;i>=0;i--){
            if(mili[i].equals(valorRepe)){
                cRepe.setSelection(i);
            }
        }
    }

    OnClickListener cOption=new OnClickListener() {
        public void onClick(View v) {
            //Comprobar las opciones marcadas y desmarcadas y actualizar opciones
            if (cTosta.isChecked()) {
                db.execSQL("UPDATE opciones SET value='1' WHERE id='tosta'");
            }else{
                db.execSQL("UPDATE opciones SET value='0' WHERE id='tosta'");
            }

            if (cVibrar.isChecked()) {
                db.execSQL("UPDATE opciones SET value='1' WHERE id='vibrar'");
            }else{
                db.execSQL("UPDATE opciones SET value='0' WHERE id='vibrar'");
            }

            if (cSonido.isChecked()) {
                db.execSQL("UPDATE opciones SET value='1' WHERE id='sonido'");
                cTipoSonido.setEnabled(true);
                cTipoSonido.setVisibility(View.VISIBLE);
                cLabelSonido.setEnabled(true);
                cLabelSonido.setVisibility(View.VISIBLE);
                cLabelVol.setEnabled(true);
                cLabelVol.setVisibility(View.VISIBLE);
                bVol.setEnabled(true);
                bVol.setVisibility(View.VISIBLE);
            }else{
                db.execSQL("UPDATE opciones SET value='0' WHERE id='sonido'");
                cTipoSonido.setEnabled(false);
                cTipoSonido.setVisibility(View.GONE);
                cLabelSonido.setEnabled(false);
                cLabelSonido.setVisibility(View.GONE);
                cLabelVol.setEnabled(false);
                cLabelVol.setVisibility(View.GONE);
                bVol.setEnabled(false);
                bVol.setVisibility(View.GONE);
            }

            if (cBarra.isChecked()) {
                db.execSQL("UPDATE opciones SET value='1' WHERE id='barra'");
            }else{
                db.execSQL("UPDATE opciones SET value='0' WHERE id='barra'");
            }

            if (cBoot.isChecked()) {
                db.execSQL("UPDATE opciones SET value='1' WHERE id='autoinicio'");
            } else {
                db.execSQL("UPDATE opciones SET value='0' WHERE id='autoinicio'");
            }
        }
    };

    SeekBar.OnSeekBarChangeListener cOptionBar= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int vol = bVol.getProgress();
            if (vol >= 0 && vol <= 100) {
                db.execSQL("UPDATE opciones SET value='"+vol+"' WHERE id='volumen'");
                cLabelVol.setText(getString(R.string.ConfiguraLabelTipo2)+": "+vol);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    boolean valueOpcion(String opcion){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                if(opcion.equals(c.getString(0))){
                    return "1".equals(c.getString(1));
                }
            } while(c.moveToNext());
        }
        if(c != null)c.close();

        return true;}

    String valueOpcionString(String opcion){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                if(opcion.equals(c.getString(0))){
                    return c.getString(1);
                }
            } while(c.moveToNext());
        }
        if(c != null)c.close();

        return "";}

    int valueOpcionInt(String opcion){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                if(opcion.equals(c.getString(0))){
                    return c.getInt(1);
                }
            } while(c.moveToNext());
        }
        if(c != null)c.close();

        return 20;}
}