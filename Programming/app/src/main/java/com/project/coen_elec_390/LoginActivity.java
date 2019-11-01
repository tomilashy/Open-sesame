package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username;
    private EditText password;
    private Button login;
    private Button signUp;
    private Toast toast;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        username = findViewById(R.id.lUsername);
        password = findViewById(R.id.lPassword);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.lSignUp);

        databaseHelper = new DatabaseHelper();
        sharedPreference = getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String sUsername = username.getText().toString();
                final String sPassword = password.getText().toString();
                if (isValidInputs(sUsername, sPassword)) {
                    FirebaseFirestore database = databaseHelper.getDatabase();
                    database.collection("profiles").document(sUsername).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            if (sPassword.equals(document.getData().get("password").toString())) {
                                                int doorID =  Integer.parseInt(document.getData().get("doorID").toString());
                                                Log.d("Login", sUsername);
                                                Log.d("Login", Integer.toString(doorID));

                                                databaseHelper.setDoorID(doorID);

                                                SharedPreferences.Editor editor = sharedPreference.edit();
                                                editor.putString("username", sUsername);
                                                editor.putInt("doorID", doorID);
                                                editor.commit();

                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                toast = Toast.makeText(LoginActivity.this, "Wrong password or username!", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        } else {
                                            toast = Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    } else {
                                        Log.d("Login", "get() failed with ", task.getException());
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

                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() { }

    private boolean isValidInputs(String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            if (username.length() < 16) {
                if (password.length() > 5 && password.length() < 16) {
                    return true;
                } else {
                    toast = Toast.makeText(this, "Length of the password should be between 6 and 16 characters!", Toast.LENGTH_SHORT);
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
