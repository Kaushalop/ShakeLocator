package com.example.kaushalop.shakelocator;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static String YES_ACTION = "YES";
    public static String NO_ACTION = "NO";

    public GoogleApiClient client;
    public Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_access)
                        .setContentTitle("Shake Located")
                        .setContentText("Marking your location of awesomeness!");
        Intent resultIntent = new Intent(this, MapsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Yes intent
        Intent yesReceive = new Intent();
        yesReceive.setAction(YES_ACTION);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        //No intent
        Intent noReceive = new Intent();
        noReceive.setAction(NO_ACTION);
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action actionyes = new NotificationCompat.Action.Builder(R.drawable.ic_done, "Yes", pendingIntentYes).build();
        NotificationCompat.Action actionno = new NotificationCompat.Action.Builder(R.drawable.ic_no, "No", pendingIntentNo).build();
        mBuilder.addAction(actionyes);
        mBuilder.addAction(actionno);

        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                Toast.makeText(getApplicationContext(), "Device shaken!", Toast.LENGTH_SHORT).show();
                mNotificationManager.notify(1, mBuilder.build());
                mBuilder.setAutoCancel(true);
               saveLocation();
            }
        });

        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName("kaushalop7@gmail.com")
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
        if (client.isConnected()) {
            client.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void saveLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        if (lastKnownLocation != null) {
            Toast.makeText(getApplicationContext(), "Lat : "+lastKnownLocation.getLatitude()+" : Long : "+ lastKnownLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences("Location", MODE_PRIVATE).edit();
            editor.putString(""+lastKnownLocation.getLatitude(), ""+lastKnownLocation.getLongitude());
            editor.apply();
        }
    }
}
