package com.dergoogler.phh.flashlight.util;

import android.content.Context;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

import com.dergoogler.phh.flashlight.R;

public class Link {

    private final Context mContext;

    public Link(Context context) {
        mContext = context;
    }

    /**
     * Open link in custom tab
     *
     * @param link Given link
     */
    public void open(String link) {
        Uri uriUrl = Uri.parse(link);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(mContext.getColor(R.color.status_bar_color))
                .build();
        intentBuilder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(mContext, uriUrl);
    }
}
