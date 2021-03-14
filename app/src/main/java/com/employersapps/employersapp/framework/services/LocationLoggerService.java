package com.employersapps.employersapp.framework.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.icu.util.TimeUnit;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.work.Logger;

import com.employersapps.core.data.UserCoordsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.UserCoords;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.NotificationHelper;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.framework.broadcast_receviers.LocationLoggerReceiver;
import com.employersapps.employersapp.framework.util.AndroidNotificationHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.inject.Inject;

public class LocationLoggerService extends Service {

    private LocationManager locationManager;
    private UserCoordsDataSource userCoordsDataSource;
    private UserDataSource userDataSource;
    private AndroidNotificationHelper notificationManager;

    private LoggerLocationListener gpsListener = new LoggerLocationListener();
    private LoggerLocationListener networkListener = new LoggerLocationListener();


    private class LoggerLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if(userDataSource.isLogged()) {

                long userId = userDataSource.getCurrentUser().getId();

                UserCoords coords = new UserCoords(
                        userId,
                        LocalDateTime.now(ZoneId.of("EET")),
                        location.getLongitude(),
                        location.getLatitude()
                );

                userCoordsDataSource.sendLocation(coords)
                        .onComplete(new Deferrable.OnCompleteCallback<Void>() {
                            @Override
                            public void onComplete(Void result) {
                                Log.i("LocationInfo", "User coords added");
                            }
                        })
                        .onException(new Deferrable.OnExceptionCallback<Void>() {
                            @Override
                            public void onException(Throwable t) {
                                Log.i("LocationInfo", t.getMessage());
                            }
                        });

            }

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.i("LocationInfo", "Provider enabled");

            if(provider.equals(LocationManager.GPS_PROVIDER)) {
                disableNetworkProvider();
            }
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.i("LocationInfo", "Provider disabled");

            if(provider.equals(LocationManager.GPS_PROVIDER)) {
                registerNetworkProvider();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        EmployersApp employersApp = (EmployersApp) getApplication();
        employersApp.getAppComponent().inject(this);

    }

    @Inject
    public void setNotificationManager(NotificationHelper notificationManager) {
        this.notificationManager = (AndroidNotificationHelper) notificationManager;
    }


    @Inject
    public void setUserCoordsDataSource(UserCoordsDataSource userCoordsDataSource) {
        this.userCoordsDataSource = userCoordsDataSource;
    }


    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("No permissions");
        }


        registerGPSProvider();
        return START_STICKY;
    }

    private void disableNetworkProvider() {
        locationManager.removeUpdates(networkListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @SuppressLint("MissingPermission")
    private void registerNetworkProvider() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                60000 * 15,
                100,
                networkListener);
    }

    @SuppressLint("MissingPermission")
    private void registerGPSProvider() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                60000 * 15,
                100,
                gpsListener);
    }
}
