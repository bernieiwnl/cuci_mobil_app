package com.example.cucimobilapp.LOADING;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import com.example.cucimobilapp.R;

public class LoadingAnimation {

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingAnimation(Activity myActivity){
        activity = myActivity;
    }

    public void startLoadingLoginDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_loading_animation, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    public void dismissDialog()
    {
        alertDialog.dismiss();
    }
}
