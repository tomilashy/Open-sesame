package com.project.coen_elec_390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button peek;
    private Button unlock;
    private Button admins;
    private Button history;
    private Button credits;
    private Button logout;

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

        admins.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToAdministrators();
            }
        });
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
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                editProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void goToAdministrators() { startActivity(new Intent(this, AdminsActivity.class)); }

    private void goToHistory() { startActivity(new Intent(this, DisplayHistory.class)); }

    private void goToCredits() { startActivity(new Intent(this, Credits.class)); }

    private void editProfile() { startActivity(new Intent(this, ProfileActivity.class)); }
}
