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

    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Open Sesame");

        sharedPreference = getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE );
        String username = sharedPreference.getString("profileName", null);
        if (username != null) {
            if (username.equals("")) {
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

        peek = findViewById(R.id.peek);
        unlock = findViewById(R.id.unlock);
        admins = findViewById(R.id.admins);
        history = findViewById(R.id.history);
        credits = findViewById(R.id.credits);

        credits.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCredits();
            }
        });
    }

    private void goToCredits() {
        Intent intent = new Intent(this, Credits.class);
        startActivity(intent);
    }
}
