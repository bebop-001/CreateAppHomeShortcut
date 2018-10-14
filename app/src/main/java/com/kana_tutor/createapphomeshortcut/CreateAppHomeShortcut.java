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
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import static android.support.v4.content.pm.ShortcutManagerCompat.*;

/*
 * install a shortcut from your app on the user's home screen.
 */
public class CreateAppHomeShortcut extends AppCompatActivity {
    private static final String TAG = CreateAppHomeShortcut.class.getSimpleName();

private void CreateShortcut(final Context c) {
    final String appName = c.getString(R.string.app_name);
    // Ask the user if he actually wants the shortcut.
    new AlertDialog.Builder(c)
        .setTitle("Install Desktop Shortcut")
        .setMessage("Install a shortcut for this app on your home page?")
        .setNegativeButton("NO", (final DialogInterface dialog, int which)
            -> CreateAppHomeShortcut.this.finish())
        .setPositiveButton("YES", (final DialogInterface dialog, int which) -> {
                // Android 8+ -- use the shortcut manager to install a pinned shortcut.
            if (isRequestPinShortcutSupported(c)) {

                // Create an inner-inner broadcast receiver so we have access
                // to the alert dialog passed in to the button so we can kill
                // the dialog.
                final String broadcastName = "com.kana_tutor.shortcutBroadcast";
                class ShortcutBroadcastReceiver extends BroadcastReceiver {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d(TAG, "ShortcutBroadcastReceiver received");
                        CreateAppHomeShortcut.this.finish();
                    }
                }
                // associate the intent with the broadcast receiver.
                CreateAppHomeShortcut.this.registerReceiver(
                    new ShortcutBroadcastReceiver(), new IntentFilter(broadcastName)
                );
                Intent intent = new Intent(broadcastName);

                ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat
                        .Builder(c,"pinned-shortcut")
                        .setIcon(IconCompat.createWithResource(c, R.mipmap.ic_launcher_round))
                        .setIntent(intent)
                        .setShortLabel(appName)
                        .build();

                PendingIntent successCallback = PendingIntent.getBroadcast(
                        c, 0
                        , intent, 0);
                requestPinShortcut(c, pinShortcutInfo
                        , successCallback.getIntentSender());
            }
        })
        .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_app_home_shortcut);
        findViewById(R.id.create_shortcut).setOnClickListener(
            (View v) -> CreateShortcut(CreateAppHomeShortcut.this)
        );
    }
}
