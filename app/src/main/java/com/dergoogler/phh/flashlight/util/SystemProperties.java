package com.dergoogler.phh.flashlight.util;

import androidx.annotation.Keep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Keep
public class SystemProperties {
    /**
     * Getting system props with root and without root
     *
     * @param key Your given key
     * @return An string
     */
    @Keep
    public static String get(String key) {
        Process process = null;
        BufferedReader bufferedReader = null;
        if (Shell.rootAccess()) {
            String prop = Shell.resultOf("getprop " + key).trim();
            if (prop.endsWith("\n"))
                prop = prop.substring(0, prop.length() - 1).trim();
            return prop;
        } else {
            try {
                process = new ProcessBuilder().command("/system/bin/getprop", key).redirectErrorStream(true).start();
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = bufferedReader.readLine();
                if (line == null) {
                    line = ""; //prop not set
                }
                return line;
            } catch (Exception e) {
                return "";
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ignored) {
                    }
                }
                if (process != null) {
                    process.destroy();
                }
            }
        }

    }

    @Keep
    public static void set(String prop, String key) {
        Shell.exec("setprop " + prop + " " + key);
    }
}