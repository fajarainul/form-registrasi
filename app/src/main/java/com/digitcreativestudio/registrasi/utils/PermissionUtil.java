package com.digitcreativestudio.registrasi.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by faqiharifian on 30/03/17.
 */

public class PermissionUtil {
    public static boolean isPermissionGranted(Context context, String[] permissions){
        boolean result = true;
        for(String permission : permissions){
            result = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            if(!result) break;
        }
        return result;
    }

    public static boolean shouldAskPermissions(Context context, String[] permissions) {
        boolean isPermissionGranted = isPermissionGranted(context, permissions);
        return !(isPermissionGranted(context, permissions)) &&
                (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
