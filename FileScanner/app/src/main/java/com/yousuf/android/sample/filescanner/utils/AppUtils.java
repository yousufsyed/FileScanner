package com.yousuf.android.sample.filescanner.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.yousuf.android.sample.filescanner.R;

import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

/**
 * @author Yousuf S. on 1/13/17.
 */
public class AppUtils {

    /**
     * Checks if external storage is available to at least read
     *
     * @return whether the external storage is readable or not.
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /**
     * Get extension from string.
     *
     * @param title file name with extention
     * @return extension of the file
     */
    public static String getExtension(String title) {
        String[] extTokens = title.split(Pattern.quote("."));

        if (extTokens.length > 1) {
            String ext = extTokens[extTokens.length - 1];
            if (TextUtils.isDigitsOnly(ext)) {
                ext = "Unknown";
            }
            return ext;
        }
        return "Unknown";
    }

    /**
     * Convert bytes into Kb or Mb for better readability
     *
     * @param size size of the file
     * @return convert meaningful size string.
     */
    public static String getConvertedSize(double size) {
        if (size > 1023) {
            size = size / 1024;
            if (size > 1023) {
                size = size / 1024;
                return String.format(Locale.US, "%.2f Mb", size);
            } else {
                return String.format(Locale.US, "%.2f Kb", size);
            }
        } else {
            return String.format(Locale.US, "%.2f bytes", size);
        }
    }

    /**
     * Launch intent to share.
     *
     * @param ctx     Activity context
     * @param title   Title text
     * @param subject Subject text
     * @param message Content text
     */
    public static void shareResults(Context ctx, String title, String subject, String message) {
        Intent sharingIntent = new Intent(ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(EXTRA_TEXT, message);
        ctx.startActivity(Intent.createChooser(sharingIntent, title));
    }

    public static void launchApplicationSettings(Context ctx){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", ctx.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ctx.startActivity(intent);
    }

}
