package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username;
    private EditText password;
    private Button login;
    private Button signUp;
    private Toast toast;
    private TextView forgotPassword;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreference;

    private final String TAG = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        username = findViewById(R.id.lUsername);
        password = findViewById(R.id.lPassword);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.lSignUp);
        forgotPassword = findViewById(R.id.forgot_pass);

        FirebaseApp.initializeApp(this);
        databaseHelper = new DatabaseHelper();
        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    final String sUsername = username.getText().toString();
                    final String sPassword = password.getText().toString();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    if (isValidInputs(sUsername, sPassword)) {
                        final FirebaseFirestore database = databaseHelper.getDatabase();
                        database.collection("profiles").document(sUsername).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                final Object data = document.getData().get("temporary");
                                                final int doorID = Integer.parseInt(document.getData().get("doorID").toString());
                                                if (databaseHelper.get_SHA_512_SecurePassword(sPassword)
                                               .equals(document.getData().get("password").toString())) {

                                                    Log.d(TAG, sUsername);
                                                    Log.d(TAG, Integer.toString(doorID));

                                                    SharedPreferences.Editor editor = sharedPreference.edit();
                                                    editor.putString("username", sUsername);
                                                    editor.putInt("doorID", doorID);
                                                    editor.commit();

                                                    database.collection("profiles").document(sUsername)
                                                            .update("temporary", FieldValue.delete())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    Log.d(TAG, sUsername);
                                                                    Log.d(TAG, Integer.toString(doorID));

                                                                    SharedPreferences.Editor editor = sharedPreference.edit();
                                                                    editor.putString("username", sUsername);
                                                                    editor.putInt("doorID", doorID);
                                                                    editor.commit();

                                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error resetting temporary password!", e);
                                                                }
                                                            });

                                                } else if (data != null) {
                                                    if ((databaseHelper.get_SHA_512_SecurePassword(sPassword)
                                                            .equals(data.toString()))) {

                                                        database.collection("profiles").document(sUsername)
                                                                .update("temporary", FieldValue.delete())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        Log.d(TAG, sUsername);
                                                                        Log.d(TAG, Integer.toString(doorID));

                                                                        SharedPreferences.Editor editor = sharedPreference.edit();
                                                                        editor.putString("username", sUsername);
                                                                        editor.putInt("doorID", doorID);
                                                                        editor.commit();

                                                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error resetting temporary password!", e);
                                                                    }
                                                                });

                                                    } else {
                                                        toast = Toast.makeText(LoginActivity.this, "Wrong password or username!", Toast.LENGTH_SHORT);
                                                        toast.show();

                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    }
                                                } else {
                                                    toast = Toast.makeText(LoginActivity.this, "Wrong password or username!", Toast.LENGTH_SHORT);
                                                    toast.show();

                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                }
                                            } else {
                                                toast = Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT);
                                                toast.show();

                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            }
                                        } else {
                                            Log.d(TAG, "get() failed with ", task.getException());

                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        }
                                    }
                                });
                    } else {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                } else {
                    toast = Toast.makeText(LoginActivity.this, "No network connection!", Toast.LENGTH_SHORT);
                    toast.show();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                } else {
                    toast = Toast.makeText(LoginActivity.this, "No network connection!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() { }

    private boolean isValidInputs(String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            if (username.length() < 16) {
                if (password.length() > 5) {
                    return true;
                } else {
                    toast = Toast.makeText(this, "Password length must be at least 6 characters!", Toast.LENGTH_SHORT);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
