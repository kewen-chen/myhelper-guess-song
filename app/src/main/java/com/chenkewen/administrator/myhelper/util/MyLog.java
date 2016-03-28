package com.chenkewen.administrator.myhelper.util;

import android.util.Log;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MyLog {
    public  static final boolean DEBUG = true;

    public static void d(String tag, String messgae) {
        if (DEBUG) {
            Log.d(tag, messgae);
        }
    }

    public static void i(String tag, String messgae) {
        if (DEBUG) {
            Log.i(tag, messgae);
        }
    }

    public static void w(String tag, String messgae) {
        if (DEBUG) {
            Log.w(tag, messgae);
        }
    }

    public static void e(String tag, String messgae) {
        if (DEBUG) {
            Log.e(tag, messgae);
        }
    }
}
