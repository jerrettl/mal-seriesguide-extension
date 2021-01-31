package com.malseriesguideextension.helpers;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    public static void makeToast(String text, Context context)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
