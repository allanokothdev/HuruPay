package com.hurupay.android.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

    Context context;
    DisplayMetrics displayMetrics;

    public ScreenUtils(Context context){
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
    }

    public int getHeight(){
        return displayMetrics.heightPixels;
    }

    public int getWidth(){
        return displayMetrics.widthPixels;
    }

    public int getRealHeight(){
        return displayMetrics.heightPixels / displayMetrics.densityDpi;
    }

    public int getRealWidth(){
        return displayMetrics.widthPixels /displayMetrics.densityDpi;
    }

    public int getDensity(){
        return displayMetrics.densityDpi;
    }

    public int getScale(int picWidth){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width) / new Double(picWidth);
        val = val * 100d;
        return val.intValue();
    }
}
