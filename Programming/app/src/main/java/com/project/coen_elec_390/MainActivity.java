package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button peek;
    private Button unlock;
    private Button admins;
    private Button history;
    private Button credits;
    private Button logout;
    //private Button upload;

    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Open Sesame");

        peek = findViewById(R.id.peek);
        unlock = findViewById(R.id.unlock);
        admins = findViewById(R.id.admins);
        history = findViewById(R.id.history);
        credits = findViewById(R.id.credits);
        logout = findViewById(R.id.logout);
        //upload = findViewById(R.id.upload);

        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE );

        history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHistory();
            }
        });
        credits.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCredits();
            }
        });
        /*upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUpload();
            }
        });*/
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void goToHistory() {
        Intent intent = new Intent(this, DisplayHistory.class);
        startActivity(intent);
    }

    /*private void goToUpload() {
        Intent intent = new Intent(this, UploadImage.class);
        startActivity(intent);
    }*/

    private void goToCredits() {
        startActivity(new Intent(this, Credits.class));
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
