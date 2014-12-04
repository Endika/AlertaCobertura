package com.endikaiglesias.alertacobertura;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends ActionBarActivity {
    /*private Handler mHandler = new Handler() {
        @Override
        public void close() {

        }

        @Override
        public void flush() {

        }

        @Override
        public void publish(LogRecord record) {

        }
    };*/
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(startService(new Intent(getApplicationContext(), Servicio.class)) == null) tosta(getString(R.string.noServicio));
        estadoActual();
        //mHandler.removeCallbacks(mMuestraMensaje);
        //mHandler.postDelayed(mMuestraMensaje, 4000);

        //LinearLayout lytMain = (LinearLayout) findViewById(R.id.lytMain);
        //lytMain.addView(adView);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            Intent svc = new Intent(this, Servicio.class);
            stopService(svc);
            this.onDestroy();
            finish();
        }
    }

/*
    private Runnable mMuestraMensaje = new Runnable() {
        public void run() {
            estadoActual();
            mHandler.removeCallbacks(mMuestraMensaje);
            mHandler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mMuestraMensaje);
        if(adView != null)
            adView.destroy();
    }*/

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
                return true;
            */
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

    private void tosta(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    private void estadoActual(){
        BBDD usdbh = new BBDD(this, null);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        String[] campos = new String[] {"estado", "fecha"};

        assert db != null;
        Cursor c = db.query("historial", campos, null, null, null, null,null,null);

        String estado=null;
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                estado=c.getString(0);
            } while(c.moveToNext());
        }
        if(c != null)c.close();

        TextView t = (TextView)findViewById(R.id.estado);
        ImageView i = (ImageView)findViewById(R.id.viewstate);
        i.setImageResource(R.drawable.ic_launcher_off);
        t.setText(getText(R.string.noCobertura));
        if(estado !=null && estado.equals("conCobertura")){i.setImageResource(R.drawable.ic_launcher);t.setText(getText(R.string.siCobertura));}
        if(estado == null)t.setText(getText(R.string.none));

    }

/*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ad, container, false);
            return rootView;
        }
    }
*/
    public static class AdFragment extends Fragment {
        private AdView mAdView;

        public AdFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
}
