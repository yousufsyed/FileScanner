package com.yousuf.android.sample.filescanner.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by u471637 on 1/14/17.
 */

public class PermissionsUtil {

    private static String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static boolean isPermissionRequired(Context ctx) {
        return (ContextCompat.checkSelfPermission(ctx, WRITE_PERMISSION)) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isRationaleRequired(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_PERMISSION);
    }
}
