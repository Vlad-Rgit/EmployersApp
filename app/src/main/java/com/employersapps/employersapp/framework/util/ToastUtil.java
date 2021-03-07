package com.employersapps.employersapp.framework.util;

import android.content.Context;
import android.widget.Toast;

import com.employersapps.core.utils.IllegalRequestException;
import com.employersapps.employersapp.R;

import java.net.SocketTimeoutException;

public class ToastUtil {
    public static void showLongToast(Context context, String message) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    public static void showLongToast(Context context, int resString) {
        Toast.makeText(
                context,
                resString,
                Toast.LENGTH_LONG
        ).show();
    }

    public static void showExceptionMessage(Context context, Throwable t) {
        if(t instanceof SocketTimeoutException) {
            ToastUtil.showLongToast(
                    context,
                    R.string.server_timeout
            );
        }
        else if (t instanceof IllegalRequestException) {
            IllegalRequestException ex = (IllegalRequestException)t;
            ToastUtil.showLongToast(
                    context,
                    ex.getMessage()
            );
        }
        else {
            ToastUtil.showLongToast(
                    context,
                    t.getLocalizedMessage()
            );
        }
    }
}
