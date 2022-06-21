package com.dergoogler.phh.flashlight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dergoogler.phh.flashlight.controllers.FlashlightController;

public class FlashlightReceiver extends BroadcastReceiver {
    private FlashlightController fc;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        fc = new FlashlightController(context);
        if (hasFlashlight()) {
            boolean state = extras.getBoolean("enable");
            if (isAvailable()) {
                setFlashlight(state);
            }
        }
    }

    private boolean isAvailable() {
        return fc.isAvailable();
    }

    private boolean hasFlashlight() {
        return fc.hasFlashlight();
    }

    private boolean isEnabled() {
        return fc.isEnabled();
    }

    private void setFlashlight(boolean enabled) {
        fc.setFlashlight(enabled);
    }

}