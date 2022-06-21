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
public class SuperUser {
    private static int currentRootState = -1;

    public static final String BINARY_SU = "su";
    public static final String BUSYBOX_BINARY = "busybox";

    // These must end with a /
    private static final String[] PATHS = {
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/su/bin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/",
            "/cache/",
            "/data/",
            "/dev/"
    };

    public static void exec(String ...strings) {
        try{
            java.lang.Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String resultOf(String ...strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try{
            java.lang.Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = SuperUser.readFully(response);
        } catch (IOException e){
            e.printStackTrace();
        }
        return res;
    }

    private boolean checkBinary(String filename) {
        for (String path : PATHS) {
            String completePath = path + filename;
            File f = new File(completePath);
            boolean fileExists = f.exists();
            if (fileExists) {
                return true;
            }
        }
        return false;
    }

    private static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

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
        return Boolean.TRUE.equals(SuperUser.isAppGrantedRoot());
    }
}