package com.dergoogler.a70q.flashlight.services;

import android.service.quicksettings.TileService;

import com.dergoogler.a70q.flashlight.components.TileDialog;

import java.io.IOException;


public class A70TileService extends TileService {
    @Override
    public void onClick() {
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isSecure()) {
                showDialog();
            } else {
                unlockAndRun(this::showDialog);
            }
    }

    private void showDialog() {
        showDialog(new TileDialog(this).getDialog());
    }
}