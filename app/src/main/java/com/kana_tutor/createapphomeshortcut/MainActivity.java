package com.kana_tutor.createapphomeshortcut;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int CREATE_SHORTCUT = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.create_shortcut).setOnClickListener((View v) -> {
            Intent i = new Intent(MainActivity.this, CreateShortcut.class);
            i.putExtra("shortcutId", getString(R.string.app_name));
            startActivityForResult(i, CREATE_SHORTCUT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_SHORTCUT) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Log.d(TAG, "CreateShortcut result:" + result);
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
