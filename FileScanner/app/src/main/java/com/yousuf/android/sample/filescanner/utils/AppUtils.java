package com.yousuf.android.sample.filescanner.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

/**
 * Created by u471637 on 1/13/17.
 */

public class AppUtils {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Get extension from string.
     *
     * @param title file name with extention
     * @return extension of the file
     */
    public static String getExtension(String title){
        String[] extTokens = title.split(Pattern.quote("."));

        String ext = "";
        if (extTokens.length > 1) {
            ext = extTokens[extTokens.length - 1];
        }

        if (TextUtils.isEmpty(ext) || TextUtils.isDigitsOnly(ext)) {
            ext = "Unknown";
        }
        return ext;
    }

    /* Convert bytes into Kb or Mb for better readability */
    public static String getConvertedSize(double size) {
        String convertedSize = "";

        if(size > 1023) {
            size = size / 1024;
            if (size > 1023) {
                size = size/1024;

                convertedSize = String.format(Locale.US, "%.2f Mb", size);
            } else {
                convertedSize = String.format(Locale.US, "%.2f Kb", size);
            }
        } else {
            convertedSize = String.format(Locale.US,"%.2f bytes", size);
        }
        return convertedSize;
    }

    public static void shareResults(Context ctx, String title, String subject, String message) {
        Intent sharingIntent = new Intent(ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(EXTRA_TEXT, message);
        ctx.startActivity(Intent.createChooser(sharingIntent, title));
    }
}
