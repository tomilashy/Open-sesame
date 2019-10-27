package com.project.coen_elec_390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button logIn;
    private Button signUp;
    private Toast toast;

    private FirebaseAuth auth;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        email = findViewById(R.id.sEmail);
        password = findViewById(R.id.sPassword);
        logIn = findViewById(R.id.logIn);
        signUp = findViewById(R.id.lSignUp);

        auth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper();
        sharedPreference = getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE );
        String username = sharedPreference.getString("username", null);
        if (username == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        else if (username.equals("")) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String sEmail = email.getText().toString();
                final String sPassword = password.getText().toString();
                if (isValidInputs(sEmail, sPassword)) {
                    auth.signInWithEmailAndPassword(sEmail, sPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        toast = Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(),
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    } else {
                                        String username = databaseHelper.getProfile(sEmail).getUsername();
                                        int doorID = databaseHelper.getProfile(sEmail).getDoorID();

                                        SharedPreferences.Editor editor = sharedPreference.edit();
                                        editor.putString("username", username);
                                        editor.putInt("dooID", doorID);
                                        editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidInputs(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (password.length() < 16) {
                        return true;
                    } else {
                    toast = Toast.makeText(this, "Maximum length for passwords is 16 characters!", Toast.LENGTH_SHORT);
                }
            } else {
                toast = Toast.makeText(this, "Invalid email!", Toast.LENGTH_SHORT);
            }
        } else {
            toast = Toast.makeText(this, "One or more fields are empty!", Toast.LENGTH_SHORT);
        }
        toast.show();
        return false;
    }
}
