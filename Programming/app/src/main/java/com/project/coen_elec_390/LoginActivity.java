package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreference = getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE );
        String username = sharedPreference.getString("username", null);
        Integer doorID = sharedPreference.getInt("dooID", 0);
        if (username == null && doorID == null ) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        else if (username.equals("") || doorID < 1) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }
}
