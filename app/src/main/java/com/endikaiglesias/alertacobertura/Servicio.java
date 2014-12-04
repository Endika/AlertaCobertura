package com.endikaiglesias.alertacobertura;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import java.util.Calendar;
import java.util.GregorianCalendar;
import static android.widget.Toast.*;

/**
 * Created by endikaig on 1/12/14.
 */

public class Servicio extends Service{
    protected TelephonyManager telephonyManager;
    protected PhoneStateListener listener;
    private boolean notificacionSonido=true;
    static long puntoInicio=0;
    Calendar miliseg=new GregorianCalendar();
    Handler timer = new Handler();

    Runnable crono=new Runnable() {
        @Override
        public void run() {
            miliseg=new GregorianCalendar();
            long ahora=miliseg.getTimeInMillis();
            long espera= valueOpcionLong("repetir");
            if (espera>1000){
                if (((ahora-puntoInicio)) >espera && estadoAnterior()==0){
                    alerta(false);
                    tosta(getString(R.string.AlertNoCobertura));
                    puntoInicio=miliseg.getTimeInMillis();
                }
            }
            if(estadoAnterior()==0){
                timer.postDelayed(this, 1000);
            }
        }
    };

    public void onCreate(){
        super.onCreate();
        // Iniciamos el servicio
        this.iniciarServicio();
        //Log.i(getClass().getSimpleName(), "Servicio iniciado");
    }

    public void onDestroy(){
        super.onDestroy();
        // Detenemos el servicio
        this.finalizarServicio();
        //Log.i(getClass().getSimpleName(), "Servicio detenido");
    }

    public IBinder onBind(Intent intent){
        // No usado de momento, s\u00f3lo se usa si se va a utilizar IPC
        // (Inter-Process Communication) para comunicarse entre procesos
        return null;
    }

    public void iniciarServicio(){
        ejecutarTarea();
    }

    public void finalizarServicio(){
/*        try{
            //Log.i(getClass().getSimpleName(), "Finalizando servicio...");
        }
        catch(Exception e){
            //Log.i(getClass().getSimpleName(), e.getMessage());
        }*/
    }

    private void ejecutarTarea(){
        //Log.i(getClass().getSimpleName(), "Ejecutando tarea...");
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // creamos el listener
        listener = new PhoneStateListener(){
            @Override
            public void onServiceStateChanged(ServiceState state){
                int serv = state.getState();
                if(serv==0 && estadoAnterior() !=1){
                    alerta(true);
                    tosta(getString(R.string.AlertSiCobertura));
                    registrar("conCobertura");
                }else if(serv!=0 && estadoAnterior() != 0){
                    alerta(false);
                    tosta(getString(R.string.AlertNoCobertura));
                    registrar("sinCobertura");
                    miliseg=new GregorianCalendar();
                    puntoInicio=miliseg.getTimeInMillis();
                    timer.postDelayed(crono, 100);
                }
            }
        };

        // ejecutamos el listener para que nos notifique el estado del telefono
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    private void tosta(String texto){
        if(valueOpcion("tosta")) makeText(getApplicationContext(), texto, LENGTH_LONG).show();

        if(valueOpcion("barra")){
            //LANZAMOS LA notificacion en la barra de estado
            //Obtenemos una referencia al servicio de notificaciones
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notManager = (NotificationManager) getSystemService(ns);

            //Configuramos la notificaci\u00f3n
            int icono = R.drawable.ic_launcher;
            if(texto.equals(getString(R.string.AlertNoCobertura)))icono = R.drawable.ic_launcher_off;
            CharSequence textoEstado = "Alerta Cobertura!";
            long hora = System.currentTimeMillis();
            Context contexto = getApplicationContext();

            //Configuramos el Intent
            CharSequence descripcion = "Alerta Cobertura!";
            Intent notIntent = new Intent(contexto,Service.class);
            assert contexto != null;
            PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, notIntent, 0);
            Notification noti = new Notification.Builder(contexto)
                    .setContentTitle(descripcion)
                    .setContentText(texto)
                    .setSmallIcon(icono)
                    .setContentIntent(contIntent)
                    .build();

            //AutoCancel: cuando se pulsa la notificaión ésta desaparece
            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            //Añadir sonido, vibración y luces
            //notif.defaults |= Notification.DEFAULT_SOUND;
            if(valueOpcion("vibrar")) noti.defaults |= Notification.DEFAULT_VIBRATE;
            //notif.defaults |= Notification.DEFAULT_LIGHTS;

            //Enviar notificación
            notManager.notify(1, noti);
        }
    }

    private void alerta(boolean cobertura){

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int vNotification = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
                vMusic = audio.getStreamVolume(AudioManager.STREAM_MUSIC),
                VOLUMEN = valueOpcionInt("volumen");
        try {/* ... */
            if(notificacionSonido && valueOpcion("sonido")){
                boolean tipoSonido = valueOpcion("sonidoTipo");
                if(!tipoSonido){
                    MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.nocobertura);
                    if(cobertura)mp = MediaPlayer.create(getBaseContext(), R.raw.sicobertura);
                    long rVol = (long) ((audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/100.0)*VOLUMEN);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,(int) rVol,AudioManager.FLAG_SHOW_UI);
                    mp.start();
                    while(mp.isPlaying()){

                    }
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,vMusic,AudioManager.FLAG_SHOW_UI);
                }else if(tipoSonido){
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    long rVol = (long) ((audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)/100.0)*VOLUMEN);
                    audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION,(int) rVol,AudioManager.FLAG_SHOW_UI);
                    r.play();
                    while(r.isPlaying()){

                    }
                    audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION,vNotification,AudioManager.FLAG_SHOW_UI);
                }
            }
            //tosta("Suenaaaaaaa");
        } catch (Exception e) {/* YA ERES MIO TE CAPTURE! */
            if(notificacionSonido){
                tosta(getString(R.string.noSonido));
                notificacionSonido=false;
            }
        }

    }

    private void registrar(String estado){
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null){
            java.util.Date utilDate = new java.util.Date(); //fecha actual
            long lnMilisegundos = utilDate.getTime();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(lnMilisegundos);
            //Insertamos los datos en la tabla Usuarios
            db.execSQL("INSERT INTO historial (estado, fecha) " + "VALUES ('" + estado + "', '" + sqlTimestamp +"')");
            //Cerramos la base de datos
            db.close();
        }
    }

    private int estadoAnterior(){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"estado", "fecha"};
        assert db != null;
        Cursor c = null;
        String estado=null;

        try {
            c = db.query("historial", campos, null, null, null, null,null,null);
            if (c.moveToLast()){
                estado=c.getString(0);
            }
        } catch(Exception e) {
            // Forzamos dos veces
            if(c != null)c.close();

        } finally {
            // this gets called even if there is an exception somewhere above
            if(c != null)c.close();
        }

        if(estado != null && estado.equals("conCobertura"))return 1;
        else if(estado != null && estado.equals("sinCobertura"))return 0;
        return -1;}

    private long fechaAnterior(){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"estado", "fecha"};
        assert db != null;
        Cursor c = db.query("historial", campos, null, null, null, null,null,null);

        if (c.moveToLast()){
            return c.getLong(1);
        }
        return 0;}

    private boolean valueOpcion(String opcion){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(opcion.equals(c.getString(0))){
                    return "1".equals(c.getString(1));
                }
            } while(c.moveToNext());
        }
        return true;
    }

    private int valueOpcionInt(String opcion){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(opcion.equals(c.getString(0))){
                    return c.getInt(1);
                }
            } while(c.moveToNext());
        }
        return 20;}

    long valueOpcionLong(String opcion){
        BBDD usdbh = new BBDD(this, null);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        String[] campos = new String[] {"id", "value"};
        assert db != null;
        Cursor c = db.query("opciones", campos, null, null, null, null,null,null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m\u00e1s registros
            do {
                if(opcion.equals(c.getString(0))){
                    return c.getLong(1);
                }
            } while(c.moveToNext());
        }
        return -1;}

}