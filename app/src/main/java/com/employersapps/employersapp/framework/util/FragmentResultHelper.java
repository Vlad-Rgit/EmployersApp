package com.employersapps.employersapp.framework.util;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.employersapps.core.domain.News;

public class FragmentResultHelper {

    public static <T> void saveResult(Activity activity,
                                      int viewId,
                                      String key,
                                      T value) {

        Navigation.findNavController(activity, viewId)
                .getPreviousBackStackEntry()
                .getSavedStateHandle()
                .set(key, value);
    }

    public static <T> LiveData<T> getResultLiveData(Activity activity, int viewId, String key) {
       return Navigation.findNavController(activity, viewId)
               .getCurrentBackStackEntry()
               .getSavedStateHandle()
               .getLiveData(key);
    }
}
