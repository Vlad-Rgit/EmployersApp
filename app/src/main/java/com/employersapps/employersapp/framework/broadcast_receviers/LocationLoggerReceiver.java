package com.employersapps.employersapp.framework.broadcast_receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.employersapps.core.data.UserCoordsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserCoords;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.di.components.AppComponent;
import com.employersapps.employersapp.framework.services.LocationLoggerService;

public class LocationLoggerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        EmployersApp employersApp = (EmployersApp) context.getApplicationContext();
        AppComponent appComponent = employersApp.getAppComponent();

        UserCoordsDataSource userCoordsDataSource = appComponent.getUserCordsDataSource();
        UserDataSource userDataSource = appComponent.getUserDataSource();

        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        if(userDataSource.isLogged() && location != null) {
            UserCoords userCoords = new UserCoords(
                    userDataSource.getCurrentUser().getId(),
                    location.getLongitude(),
                    location.getLatitude()
            );

            userCoordsDataSource.sendLocation(userCoords)
                    .onComplete(new Deferrable.OnCompleteCallback<Void>() {
                        @Override
                        public void onComplete(Void result) {
                            Log.i("LocationInfo", "User coords added from recevier");
                        }
                    })
                    .onException(new Deferrable.OnExceptionCallback<Void>() {
                        @Override
                        public void onException(Throwable t) {
                            Log.e("LocationInfo", t.getMessage(), t);
                        }
                    });
        }

    }
}
