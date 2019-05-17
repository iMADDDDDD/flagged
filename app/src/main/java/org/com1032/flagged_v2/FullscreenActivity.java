package org.com1032.flagged_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * This will be only shown ONCE when user has installed the application for the first time
 */
public class FullscreenActivity extends AppCompatActivity {

    private Button mNextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        /** Inflating the button */
        mNextActivity = (Button)findViewById(R.id.button_next_slide);

        mNextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullscreenActivity.this, FullscreenActivity2.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        /** Just check if the activity hasn't been launched */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean hasStarted = prefs.getBoolean("hasAlreadyStarted", false);

        if (!hasStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("hasAlreadyStarted", Boolean.TRUE);
            edit.commit();

        } else {
            Intent intent = new Intent(FullscreenActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }



}
