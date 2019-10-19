package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button admin_button;
    private Button open_door_button;
    private Button credits_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        admin_button = findViewById(R.id.admin_access);
        open_door_button = findViewById(R.id.open_door);
        credits_button = findViewById(R.id.credits);

        credits_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCredits();
            }
        });
    }

    private void goToCredits() {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }
}
