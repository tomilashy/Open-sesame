package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private Button instructions;
    private Button credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        instructions = findViewById(R.id.instructions);
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
