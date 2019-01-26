package mateata.example.fbmessenger.util;

import android.util.Log;

import mateata.example.fbmessenger.AppControl;

public class Dlog {
    static final String TAG = "ERROR";

    /** Log Level Error **/
    public static final void e(String message) {
        if (AppControl.DEBUG) Log.e(TAG, buildLogMsg(message));
    }

    /** Log Level Warning **/
    public static final void w(String message) {
        if (AppControl.DEBUG)Log.w(TAG, buildLogMsg(message));
    }

    /** Log Level Information **/
    public static final void i(String message) {
        if (AppControl.DEBUG)Log.i(TAG, buildLogMsg(message));
    }

    /** Log Level Debug **/
    public static final void d(String message) {
        if (AppControl.DEBUG)Log.d(TAG, buildLogMsg(message));
    }

    /** Log Level Verbose **/
    public static final void v(String message) {
        if (AppControl.DEBUG)Log.v(TAG, buildLogMsg(message));
    }

    public static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder(); sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName()); sb.append("]");
        sb.append(message); return sb.toString();
    }
}