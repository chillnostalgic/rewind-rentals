package com.rewindrentals.tv;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

/**
 * Rewind Rentals for Android TV, Fire TV and phones.
 * Shows the web app (app/src/main/assets/index.html) full screen.
 *
 * On a TV the screen is always landscape and the page uses its remote-control layout.
 * On a phone or tablet you browse whichever way up you hold it, and the screen turns
 * sideways by itself while a tape or channel is playing.
 */
public class MainActivity extends Activity {

    private static final String HOST = "appassets.androidplatform.net";
    private static final String START = "https://" + HOST + "/assets/index.html";
    private WebView web;
    private boolean tv;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tv = isTelevision();
        setRequestedOrientation(tv
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_USER);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .setDomain(HOST)
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#130a2b"));

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);                 // My tapes, resume points, settings
        s.setMediaPlaybackRequiresUserGesture(false); // TV channels move to the next show by themselves
        s.setAllowFileAccess(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        // Tells the page it is inside the app, and whether this is a TV or a phone
        s.setUserAgentString(s.getUserAgentString() + " RewindRentalsApp " + (tv ? "RewindTV" : "RewindPhone"));

        // Lets the page ask for the screen to turn sideways while something is playing
        web.addJavascriptInterface(new Bridge(), "RewindApp");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Stay inside the app: never navigate the main view away from Rewind Rentals
                Uri url = request.getUrl();
                return !HOST.equals(url.getHost());
            }
        });

        setContentView(web);
        hideSystemBars();
        web.setFocusable(true);
        web.requestFocus(View.FOCUS_DOWN);

        if (state != null) web.restoreState(state);
        else web.loadUrl(START);
    }

    /** True on Android TV, Google TV and Fire TV. */
    private boolean isTelevision() {
        UiModeManager ui = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (ui != null && ui.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) return true;
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature("android.software.leanback")
                || pm.hasSystemFeature("android.software.leanback_only")
                || pm.hasSystemFeature("amazon.hardware.fire_tv");
    }

    /** What the page is allowed to ask the app to do: only turn the screen. */
    private class Bridge {
        @JavascriptInterface
        public void setLandscape(final boolean on) {
            runOnUiThread(() -> {
                if (tv) return; // TVs are always landscape
                setRequestedOrientation(on
                        ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_USER);
            });
        }
    }

    /** Back button: let the page close the TV, player, menu or box first. */
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        web.evaluateJavascript("(window.rrBack && window.rrBack()) ? 'yes' : 'no'", value -> {
            if (!"\"yes\"".equals(value)) finish();
        });
    }

    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemBars();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override protected void onPause() { web.onPause(); super.onPause(); }
    @Override protected void onResume() { super.onResume(); web.onResume(); }
    @Override protected void onDestroy() { web.destroy(); super.onDestroy(); }
}
