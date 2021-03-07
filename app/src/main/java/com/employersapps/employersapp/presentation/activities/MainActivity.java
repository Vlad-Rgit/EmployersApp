package com.employersapps.employersapp.presentation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.employersapps.employersapp.R;
import com.employersapps.employersapp.framework.broadcast_receviers.LocationLoggerReceiver;
import com.employersapps.employersapp.framework.services.LocationLoggerService;
import com.employersapps.employersapp.framework.util.ToastUtil;

public class MainActivity extends AppCompatActivity {

    private final int CODE_LOCATION_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    CODE_LOCATION_PERMISSIONS
            );
        }
        else {
            startLoggerService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CODE_LOCATION_PERMISSIONS) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showLongToast(
                        this,
                        R.string.require_permisson_for_work
                );
                finish();
            }
            else {
                startLoggerService();
            }
        }
    }

    private void startLoggerService() {
        Intent intent = new Intent(this, LocationLoggerService.class);
        startService(intent);
    }

  /*  @SuppressLint("MissingPermission")
    private void registerLocationReceiver() {

        Intent intent = new Intent(this, LocationLoggerReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                2,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                pendingIntent
        );
    }
*/

}