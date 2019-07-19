package com.mkhelif.clipboardsync;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;

import org.bson.BsonValue;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends AppCompatActivity {
    StitchAppClient client = Stitch.getDefaultAppClient();


    String currentClipItem = "";
    private ClipboardManager mClipboardManager;

    TextView clipContentPlaceholder;
    Button logoutButton;
    Button stopClipboardButton;
    Button startClipboardButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        clipContentPlaceholder = findViewById(R.id.clipContent);
        logoutButton = findViewById(R.id.logoutButton);
        stopClipboardButton = findViewById(R.id.stopClipboardService);
        startClipboardButton = findViewById(R.id.startClipboardService);
        Log.d("stitch", "Getting settings data");

        stopClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),ClipboardManagerService.class);
                stopService(intent);
            }
        });

        startClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClipboardService();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),ClipboardManagerService.class);
                stopService(intent);
                client.getAuth().logout();
            }
        });

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
            Toast.makeText(getBaseContext(), "Killing Clipboard Service", Toast.LENGTH_LONG).show();
        }
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {

                    ClipData clip = mClipboardManager.getPrimaryClip();
                    String currentClipString = (String) clip.getItemAt(0).getText();
                    if(!currentClipItem.equals(currentClipString)){
                        clipContentPlaceholder.setText(currentClipString);
                        Log.d("stitch", "Updated clipboard: " + currentClipString);
                    }

                }
            };

    private void startClipboardService(){
        Log.d("stitch", "Starting clipboard service");
        startForegroundService(new Intent(getApplicationContext(), ClipboardManagerService.class));
    }


}