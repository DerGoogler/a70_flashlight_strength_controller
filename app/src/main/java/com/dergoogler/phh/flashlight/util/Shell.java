package com.dergoogler.phh.flashlight.util;

import android.os.Process;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Runs an superuser command
 */
public class Shell {
    private static int currentRootState = -1;

    static {
        System.loadLibrary("native");
    }

    public static native void exec(String command);

    public static native String resultOf(String command);

    public static Boolean isAppGrantedRoot() {
        if (currentRootState < 0) {
            if (Process.myUid() == 0) {
                // The current process is a root service
                currentRootState = 2;
                return true;
            }
            // noinspection ConstantConditions
            for (String path : System.getenv("PATH").split(":")) {
                File su = new File(path, "su");
                if (su.canExecute()) {
                    // We don't actually know whether the app has been granted root access.
                    // Do NOT set the value as a confirmed state.
                    currentRootState = 1;
                    return null;
                }
            }
            currentRootState = 0;
            return false;
        }
        switch (currentRootState) {
            case 0 : return false;
            case 2 : return true;
            default: return null;
        }
    }

    public static boolean rootAccess() {
        return Boolean.TRUE.equals(Shell.isAppGrantedRoot());
    }
}