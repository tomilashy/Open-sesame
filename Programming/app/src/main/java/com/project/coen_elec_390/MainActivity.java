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

    private void goToCredits() {
        Intent intent = new Intent(this, Credits.class);
        startActivity(intent);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /*@Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }*/
}
