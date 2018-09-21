/*
 *  Copyright 2018 Steven Smith kana-tutor.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied.
 *
 *  See the License for the specific language governing permissions
 *  and limitations under the License.
 */

package com.kana_tutor.createapphomeshortcut;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

/*
 * install a shortcut from your app on the user's home screen.
 */
public class CreateAppHomeShortcut extends AppCompatActivity {

    public static void CreateShortcut(final Context c) {
        final String appName = c.getString(R.string.app_name);
        SharedPreferences prefs = c.getSharedPreferences(
                "myPrefs", Context.MODE_PRIVATE);

        // If the shortcut was already installed, just let the user know and continue.
        long shortcutCreationTime = prefs.getLong("shortcutCreationTime", 0);
        if (shortcutCreationTime > 0) {
            String creationTime = new SimpleDateFormat(
                    "MMM d, yyyy", Locale.getDefault())
                    .format(new java.util.Date(shortcutCreationTime));
            Toast.makeText(c
                    , String.format("Shortcut for %s was created on %s"
                        , appName, creationTime)
                    , Toast.LENGTH_LONG)
                 .show();
            return;
        }
        // No shortcut yet.  Mark as done.
        prefs.edit()
             .putLong("shortcutCreationTime", System.currentTimeMillis())
             .apply();
        // Ask the user if he actually wants the shortcut.
        new AlertDialog.Builder(c)
            .setTitle("Install Desktop Shortcut")
            .setMessage("Install a shortcut for this app on your home page?")
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(Build.VERSION.SDK_INT < 26) {
                        // pre android 8 -- just do it.
                        Intent intent = new Intent(
                                c.getApplicationContext(), c.getClass());
                        intent.setAction(Intent.ACTION_MAIN);
                        Intent action = new Intent();
                        action.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
                        action.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
                        action.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE
                                , Intent.ShortcutIconResource.fromContext(
                                        c, R.mipmap.ic_launcher_round));
                        // don't install if app is already present.
                        action.putExtra("duplicate", false);
                        action.setAction(
                                "com.android.launcher.action.INSTALL_SHORTCUT");
                        c.sendBroadcast(action);
                        Toast.makeText(c
                                , String.format(
                                        "Creating a desktop shortcut to app:%s", appName)
                                , Toast.LENGTH_LONG)
                             .show();
                    }
                    else {
                        // Android 8+ -- use the shortcut manager to install a pinned shortcut.
                        ShortcutManager shortcutManager = c.getSystemService(ShortcutManager.class);
                        if (shortcutManager != null
                                && shortcutManager.isRequestPinShortcutSupported()) {
                            Intent intent = new Intent(
                                    c.getApplicationContext(), c.getClass());
                            intent.setAction(Intent.ACTION_MAIN);
                            ShortcutInfo pinShortcutInfo = new ShortcutInfo
                                    .Builder(c,"pinned-shortcut")
                                    .setIcon(Icon.createWithResource(c, R.mipmap.ic_launcher_round))
                                    .setIntent(intent)
                                    .setShortLabel(appName)
                                    .build();
                            Intent pinnedShortcutCallbackIntent =
                                    shortcutManager.createShortcutResultIntent(pinShortcutInfo);
                            //Get notified when a shortcut is pinned successfully//
                            PendingIntent successCallback = PendingIntent.getBroadcast(c, 0,
                                    pinnedShortcutCallbackIntent, 0);
                            shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
                        }
                    }
                }
            })
            .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_app_home_shortcut);
        findViewById(R.id.create_shortcut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateShortcut(v.getContext());
            }
        });
    }
}
