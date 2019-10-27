package com.project.coen_elec_390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username;
    private EditText email;
    private EditText password;
    private Button logIn;
    private Button signUp;
    private Toast toast;

    private FirebaseAuth auth;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreference;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        username = findViewById(R.id.lUsername);
        email = findViewById(R.id.sEmail);
        password = findViewById(R.id.sPassword);
        logIn = findViewById(R.id.logIn);
        signUp = findViewById(R.id.lSignUp);

        auth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper();
        sharedPreference = getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE );
        String sUsername = sharedPreference.getString("username", null);
        if (sUsername == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        else if (sUsername.equals("")) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String sEmail = email.getText().toString();
                final String sPassword = password.getText().toString();
                final String sUsername = username.getText().toString();
                if (isValidInputs(sUsername, sEmail, sPassword)) {
                    auth.signInWithEmailAndPassword(sEmail, sPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        toast = Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(),
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    } else {
                                        FirebaseFirestore database = databaseHelper.getDatabase();
                                        database.collection("profiles").document(sUsername).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            Log.d("getProfile", document.getId());
                                                            if (document.exists()) {
                                                                profile = new Profile(document.getData().get("username").toString(), document.getData().get("email").toString(),
                                                                        document.getData().get("password").toString(), Integer.parseInt(document.getData().get("doorID").toString()));

                                                                String username = profile.getUsername();
                                                                int doorID = profile.getDoorID();

                                                                SharedPreferences.Editor editor = sharedPreference.edit();
                                                                editor.putString("username", username);
                                                                editor.putInt("dooID", doorID);
                                                                editor.commit();

                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Log.d("getProfile", "No such document");
                                                            }
                                                        } else {
                                                            Log.d("getProfile", "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
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

    private boolean isValidInputs(String username, String email, String password) {
        if (!username.isEmpty() &&!email.isEmpty() && !password.isEmpty()) {
            if (username.length() < 16) {
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
            toast = Toast.makeText(this, "Maximum length for user names is 16 characters!", Toast.LENGTH_SHORT);
        }
    } else {
        toast = Toast.makeText(this, "One or more fields are empty!", Toast.LENGTH_SHORT);
    }
    toast.show();
        return false;
    }
}
