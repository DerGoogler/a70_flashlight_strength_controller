package com.dergoogler.a70q.flashlight.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.widget.Toast;

import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

public class TileDialog {
    private final static int[] checkedItem = {-1};
    private final Context ctx;

    private CameraManager cm;
    private Camera cam;

    private boolean flashOn = false;

    public TileDialog(Context ctx) {
        this.ctx = ctx;
    }

    public AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);
        final String[] listItems = new String[]{"1", "2", "3", "4", "5"};
        Shell.isAppGrantedRoot();
        builder.setTitle("Strength");
        builder.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {
            ShellUtils.fastCmd("setprop persist.sys.phh.flash_strength " + listItems[which]);
            dialog.dismiss();
        });

        builder.setNeutralButton("Flash on/off", (dialog, which) -> {
            if (flashOn) {
                flashLightOff();
            } else {
                flashLightOn();
            }
            flashOn = !flashOn;
        });
        return builder.create();
    }

    private void flashLightOn() {
        if (cm == null) {
            cm = (CameraManager) this.ctx.getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            // Usually back camera is at 0 position.
            String cameraId = cm.getCameraIdList()[0];
            cm.setTorchMode(cameraId, true);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void flashLightOff() {
        try {
            String cameraId = cm.getCameraIdList()[0];
            cm.setTorchMode(cameraId, false);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        String message = "Error: " + e.getMessage();
        Toast.makeText(this.ctx, message, Toast.LENGTH_SHORT).show();
    }
}