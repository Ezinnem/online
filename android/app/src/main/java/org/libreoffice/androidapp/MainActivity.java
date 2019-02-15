/* -*- Mode: C++; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4; fill-column: 100 -*- */
/*
 * This file is part of the LibreOffice project.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.libreoffice.androidapp;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager assetManager = getResources().getAssets();

        ApplicationInfo applicationInfo = getApplicationInfo();
        String dataDir = applicationInfo.dataDir;
        Log.i(TAG, String.format("Initializing LibreOfficeKit, dataDir=%s\n", dataDir));

        //redirectStdio(true);

        String cacheDir = getApplication().getCacheDir().getAbsolutePath();
        String apkFile = getApplication().getPackageResourcePath();

        /* TODO
        // If there is a fonts.conf file in the apk that can be extracted, automatically
        // set the FONTCONFIG_FILE env var.
        InputStream inputStream;
        try {
            inputStream = activity.getAssets().open("unpack/etc/fonts/fonts.conf");
        } catch (java.io.IOException exception) {
            inputStream = null;
        }

        if (inputStream != null) {
            putenv("FONTCONFIG_FILE=" + dataDir + "/etc/fonts/fonts.conf");
        }
        */

        String urlToLoad = "file:///android_asset/dist/hello-world.odt";

        createLOOLWSD(dataDir/*"/assets"*/, cacheDir, apkFile, assetManager, urlToLoad);

        final WebView browser = findViewById(R.id.browser);
        browser.setWebViewClient(new WebViewClient());

        WebSettings browserSettings = browser.getSettings();
        browserSettings.setJavaScriptEnabled(true);
        browser.addJavascriptInterface(this, "LOOLMessageHandler");

        browser.loadUrl("file:///android_asset/dist/loleaflet.html?file_path=" +
                urlToLoad +
                "&closebutton=1&permission=edit" +
                "&debug=true"); // TODO remove later?

        Button jsButton = findViewById(R.id.js_button);
        jsButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            browser.loadUrl("javascript:helloFromJavascript()");
                                        }
                                    }
        );
    }

    static {
        System.loadLibrary("androidapp");
    }

    /** Initialize the LOOLWSD to load 'loadFileURL'. */
    public native void createLOOLWSD(String dataDir, String cacheDir, String apkFile, AssetManager assetManager, String loadFileURL);

    /** Passing messages from JS (instead of the websocket communication). */
    @JavascriptInterface
    public native void postMobileMessage(String message);

    /** Passing messages from JS (instead of the websocket communication). */
    @JavascriptInterface
    public void postMobileError(String message)
    {
        // TODO handle this
        Log.d(TAG, "postMobileError: " + message);
    }

    /** Passing messages from JS (instead of the websocket communication). */
    @JavascriptInterface
    public void postMobileDebug(String message)
    {
        // TODO handle this
        Log.d(TAG, "postMobileDebug: " + message);
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab cinoptions=b1,g0,N-s cinkeys+=0=break: */
