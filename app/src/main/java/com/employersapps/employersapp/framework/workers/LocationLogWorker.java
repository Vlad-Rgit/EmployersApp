package com.employersapps.employersapp.framework.workers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.DashPathEffect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.employersapps.core.data.UserCoordsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.UserCoords;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.LocalDateTime;

public class LocationLogWorker extends ListenableWorker {

    private final LocationManager locationManager;
    private final UserCoordsDataSource userCoordsDataSource;
    private final UserDataSource userDataSource;

    public LocationLogWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        EmployersApp employersApp = (EmployersApp) context.getApplicationContext();
        userCoordsDataSource = employersApp.getAppComponent().getUserCordsDataSource();
        userDataSource = employersApp.getAppComponent().getUserDataSource();
    }

    @NonNull
    @Override
    @SuppressLint("MissingPermission")
    public ListenableFuture<Result> startWork() {

        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<Result> completer) throws Exception {

                if(userDataSource.isLogged()) {

                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);

                    String provider = locationManager.getBestProvider(criteria, true);

                    Location location = locationManager.getLastKnownLocation(provider);

                    long userId = userDataSource.getCurrentUser().getId();

                    UserCoords userCoords = new UserCoords(
                            userId,
                            location.getLongitude(),
                            location.getLatitude()
                    );

                    Deferrable<Void> deferrable = userCoordsDataSource.sendLocation(userCoords);

                    deferrable.onComplete(new Deferrable.OnCompleteCallback<Void>() {
                        @Override
                        public void onComplete(Void result) {
                            completer.set(Result.success());
                        }
                    }).onException(new Deferrable.OnExceptionCallback<Void>() {
                                @Override
                                public void onException(Throwable t) {
                                    completer.setException(t);
                                }
                            });


                    return deferrable;
                }
                else {
                    completer.set(Result.success());
                    return "Not logged";
                }
            }
        });
    }

}
