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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
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
    private static final String TAG = "CreateAppHomeShortcut";
    private void finishActivity() {
        if(android.os.Build.VERSION.SDK_INT >= 21) {
           CreateAppHomeShortcut.this.finishAndRemoveTask();
        }
        else {
            CreateAppHomeShortcut.this.finish();
        }
    }

    // Create a shortcut and exit the activity.  If the shortcut already exists,
    // just exit.
    private void CreateShortcut(final Context c) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(c)) {
            final String shortcutId = "StartApp";
            boolean shortcutExists = false;
            if (Build.VERSION.SDK_INT >= 26) {
                // We create the shortcut multiple times if given the
                // opportunity.  If the shortcut exists, put up
                // a toast message and exit.
                ShortcutManager sm = getSystemService(ShortcutManager.class);
                List<ShortcutInfo> shortcuts = sm.getPinnedShortcuts();
                for (int i = 0; i < shortcuts.size() && !shortcutExists; i++)
                    shortcutExists = shortcuts.get(i).getId().equals(shortcutId);
            }
            if (shortcutExists) {
                Toast.makeText(c
                    , String.format("Shortcut %s already exists.", shortcutId)
                    , Toast.LENGTH_LONG
                ).show();
                finishActivity();
            }
            else {
                // this is the intent that actually creates the shortcut.
                Intent shortcutIntent = new Intent(c, CreateAppHomeShortcut.class);
                shortcutIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat
                    .Builder(c, shortcutId)
                    .setShortLabel(c.getString(R.string.app_name))
                    .setIcon(IconCompat.createWithResource(c, R.drawable.qmark))
                    .setIntent(shortcutIntent)
                    .build();
                // this intent is used to wake up the broadcast receiver.  I couldn't
                // get createShortcutResultIntent to work but just a simple intent
                // as used for a normal broadcast intent works fine.
                Intent broadcastIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                // create an anonymous broadcaster.  Unregister when done.
                registerReceiver(new BroadcastReceiver() {
                         @Override
                         public void onReceive(Context context, Intent intent) {
                             unregisterReceiver(this);
                             Log.d(TAG, String.format(
                                     "Anonymous class intent received: activity = \"$1%s\""
                                     , intent.getAction()));
                             finishActivity();
                         }
                     }
                    , new IntentFilter(Intent.ACTION_CREATE_SHORTCUT)
                );
                PendingIntent successCallback = PendingIntent.getBroadcast(
                    c, 99
                    , broadcastIntent, 0);
                // Shortcut gets created here.
                ShortcutManagerCompat.requestPinShortcut(c, shortcutInfo
                        , successCallback.getIntentSender());
            }
        }
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
