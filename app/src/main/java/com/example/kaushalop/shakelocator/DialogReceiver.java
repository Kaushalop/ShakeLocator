package com.example.kaushalop.shakelocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by kaushalop on 07/09/16.
 */
public class DialogReceiver extends BroadcastReceiver {


    public static String YES_ACTION = "YES";
    public static String NO_ACTION = "NO";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm worked!!", Toast.LENGTH_LONG).show();
        String action = intent.getAction();

        if(YES_ACTION.equals(action)) {

            Toast.makeText(context, "YES !", Toast.LENGTH_LONG).show();
        } else if(NO_ACTION.equals(action)) {
            Toast.makeText(context, "No !", Toast.LENGTH_LONG).show();

        }
}
}
