package com.mkhelif.clipboardsync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;


public class MainActivity extends AppCompatActivity {
    final StitchAppClient client = Stitch.getDefaultAppClient();
    EditText emailInput;
    EditText passwordInput;
    Button submit;
    Switch toggleSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("stitch", "login state" + client.getAuth().isLoggedIn());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.FOREGROUND_SERVICE},1);
        }
        if(client.getAuth().isLoggedIn()){

            redirectToSettings();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         emailInput = findViewById(R.id.email);
         passwordInput =  findViewById(R.id.password);
         submit =  findViewById(R.id.submit);
         toggleSubmit = findViewById(R.id.toggleAuth);

        toggleSubmit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    toggleSubmit.setText(R.string.register);
                    submit.setText(R.string.register);
                }else{
                    toggleSubmit.setText(R.string.login);
                    submit.setText(R.string.login);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailInput.setEnabled(false);
                passwordInput.setEnabled(false);
                submit.setEnabled(false);
                toggleSubmit.setEnabled(false);
                submit.setText("Logging in");
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                signIn(email,password);
            }
        });




    }

    private void redirectToSettings(){
        Intent openSettings = new Intent(this,SettingsActivity.class);
        startActivity(openSettings);
    }
    protected void signIn(String email, String password){
        UserPasswordCredential credential = new UserPasswordCredential(email, password);
       client.getAuth().loginWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<StitchUser>() {
                                           @Override
                                           public void onComplete(@NonNull final Task<StitchUser> task) {
                                               if (task.isSuccessful()) {
                                                   Log.d("stitch", "Successfully logged in as user " + task.getResult().getId());
                                                   redirectToSettings();
                                               } else {
                                                   Log.e("stitch", "Error logging in with email/password auth:", task.getException());
                                                   emailInput.setEnabled(true);
                                                   passwordInput.setEnabled(true);
                                                   submit.setEnabled(true);
                                                   toggleSubmit.setEnabled(true);
                                               }
                                           }
                                       }
                );
    }
}
