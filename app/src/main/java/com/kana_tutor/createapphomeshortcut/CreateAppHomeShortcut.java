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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/*
 * install a shortcut from your app on the user's home screen.
 */
public class CreateAppHomeShortcut extends AppCompatActivity {
    private static final String TAG = CreateAppHomeShortcut.class.getSimpleName();

    public void CreateShortcut(final Context c) {
        final String appName = c.getString(R.string.app_name);
        SharedPreferences prefs = c.getSharedPreferences(
                "myPrefs", Context.MODE_PRIVATE);

        // Ask the user if he actually wants the shortcut.
        new AlertDialog.Builder(c)
            .setTitle("Install Desktop Shortcut")
            .setMessage("Install a shortcut for this app on your home page?")
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
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
                        dialog.dismiss();
                    }
                    else {
                        // Android 8+ -- use the shortcut manager to install a pinned shortcut.
                        ShortcutManager shortcutManager = c.getSystemService(ShortcutManager.class);
                        if (shortcutManager != null
                                && shortcutManager.isRequestPinShortcutSupported()) {

                            // Create an inner-inner broadcast receiver so we have access
                            // to the alert dialog passed in to the button so we can kill
                            // the dialog.
                            final String broadcastName = "com.kana_tutor.shortcutBroadcast";
                            class ShortcutBroadcastReceiver extends BroadcastReceiver {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    Log.d(TAG, "ShortcutBroadcastReceiver received");
                                    if (dialog != null)
                                        dialog.dismiss();
                                }
                            }
                            // associate the intent with the broadcast receiver.
                            CreateAppHomeShortcut.this.registerReceiver(
                                new ShortcutBroadcastReceiver(), new IntentFilter(broadcastName)
                            );
                            Intent intent = new Intent(broadcastName);

                            Context c = CreateAppHomeShortcut.this;

                            ShortcutInfo pinShortcutInfo = new ShortcutInfo
                                    .Builder(c,"pinned-shortcut")
                                    .setIcon(Icon.createWithResource(c, R.mipmap.ic_launcher_round))
                                    .setIntent(intent)
                                    .setShortLabel(appName)
                                    .build();

                            PendingIntent successCallback = PendingIntent.getBroadcast(
                                    c, 0
                                    , intent, 0);
                            shortcutManager.requestPinShortcut(pinShortcutInfo
                                    , successCallback.getIntentSender());


                            /*
                            Intent intent = new Intent(
                                    c.getApplicationContext(), ShortcutReceiver.class);
                            intent.setAction(Intent.ACTION_MAIN);
                            ShortcutManagerCompat.requestPinShortcut(c
                                , new ShortcutInfoCompat.Builder(c, "id")
                                     .setIcon(createWithResource(c, R.drawable.qmark))
                                     .setShortLabel(appName)
                                     .build()
                                , scr.getPinRequestAcceptedIntent(c).getIntentSender()
                            );
                            PendingIntent successCallback = scr.getPinRequestAcceptedIntent(c);
                            */

                            /*
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
                            */
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
                CreateShortcut(CreateAppHomeShortcut.this);
            }
        });
    }
}
