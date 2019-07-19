package com.mkhelif.clipboardsync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;

import org.bson.BsonValue;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class ClipboardManagerService extends Service {
    private ClipboardManager mClipboardManager;
    StitchAppClient client = Stitch.getDefaultAppClient();
    String currentClipItem = "";
    @Override
    public void onCreate(){
        super.onCreate();


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Clipboard Sync Service")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(1996, notification);


        Log.d("stitch", "Starting Clipboard Service");
        Toast.makeText(getApplicationContext(), "Starting Clipboard service", Toast.LENGTH_LONG).show();


        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);
        currentClipItem = mClipboardManager.getPrimaryClip().toString();
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                getClipBoardItem();
            }
        },0,5000);


    }
    public void getClipBoardItem() {
        client.callFunction("getCurrentClip", Arrays.asList(), BsonValue.class)
                .addOnCompleteListener(new OnCompleteListener<BsonValue>() {
                    @Override
                    public void onComplete(@NonNull final Task<BsonValue> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null){
//                                Log.d("stitch", "string" + task.getResult());
                                String characters = task.getResult().asDocument().getString("clipData").getValue();
//                                Log.d("stitch", "characters " + task.getResult());
                                if(!currentClipItem.equals(characters)){
                                    ClipData newText = ClipData.newPlainText(getApplicationContext().getPackageName(),characters);
                                    mClipboardManager.setPrimaryClip(newText);
                                    currentClipItem = characters;
                                    Toast.makeText(getApplicationContext(), "Updated Clipboard", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Log.e("stitch", "task.getResult() returned null");
                            }

                        } else {
                            Log.e("stitch", "Error calling function:", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
            Toast.makeText(getApplicationContext(), "Killing Clipboard Service", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {

                    ClipData clip = mClipboardManager.getPrimaryClip();
                    String currentClipString = (String) clip.getItemAt(0).getText();
                    Log.d("stitch", "currentClipItem: " + currentClipItem + " currentClipString: " + currentClipString);
                    if(!currentClipItem.equals(currentClipString)){
                        syncClipboard(currentClipString);
                        Log.d("stitch", "Updated clipboard: " + currentClipString);
//                        Toast.makeText(getApplicationContext(),"Synced Clipboard", Toast.LENGTH_SHORT).show();
                    }

                }
            };

    private void syncClipboard(String content){
        client.callFunction("syncClipboard", Arrays.asList(content), BsonValue.class)
                .addOnCompleteListener(new OnCompleteListener<BsonValue>() {
                    @Override
                    public void onComplete(@NonNull final Task<BsonValue> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null){
                                Log.d("stitch", "string" + task.getResult());
                                Toast.makeText(getApplicationContext(), "Synced text with ClipBoard", Toast.LENGTH_LONG).show();
                            }else {
                                Log.e("stitch", "task.getResult() returned null");
                            }

                        } else {
                            Log.e("stitch", "Error calling function:", task.getException());
                        }
                    }
                });
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

