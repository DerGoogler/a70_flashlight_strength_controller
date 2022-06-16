package com.dergoogler.a70q.flashlight.components;

import android.app.AlertDialog;
import android.content.Context;

import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

public class TileDialog {
    private final static int[] checkedItem = {-1};
    private final Context ctx;

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
        return builder.create();
    }
}