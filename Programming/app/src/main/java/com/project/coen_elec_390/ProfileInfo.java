package com.project.coen_elec_390;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProfileInfo extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText doorID;
    private Button saveButton;
    private Button editButton;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;

    private String profileName;
    private String profileEmail;
    private List<String> emails;

    private final String TAG = "ProfileInfo";

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        setTitle("Profile Info");

        username = findViewById(R.id.edit_user);
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_password);
        doorID = findViewById(R.id.edit_doorID);
        saveButton = findViewById(R.id.saveButton);
        editButton = findViewById(R.id.editButton);

        username.setFocusable(false);
        email.setFocusable(false);
        password.setFocusable(false);
        doorID.setFocusable(false);

        username.setGravity(Gravity.CENTER_HORIZONTAL);
        email.setGravity(Gravity.CENTER_HORIZONTAL);
        password.setGravity(Gravity.CENTER_HORIZONTAL);
        doorID.setGravity(Gravity.CENTER_HORIZONTAL);
        saveButton.setVisibility(View.INVISIBLE);

        databaseHelper = new DatabaseHelper();
        emails = new ArrayList<String>();
        FirebaseFirestore db = databaseHelper.getDatabase();
        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        profileName = sharedPreference.getString("username", "");
        final DocumentReference docRef = db.collection("profiles").document(profileName);

        db.collection("profiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        emails.add(document.getData().get("email").toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username.setText(profileName);
                        email.setText(document.getData().get("email").toString());
                        password.setText(document.getData().get("password").toString());
                        doorID.setText(document.getData().get("doorID").toString());
                        profileEmail = document.getData().get("email").toString();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveButton.setVisibility(View.VISIBLE);
                email.setFocusableInTouchMode(true);
                password.setFocusableInTouchMode(true);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String editEmail = email.getText().toString();
                if (isValidInputs(editEmail, password.getText().toString())) {
                    if (!isEmailTaken(editEmail) || editEmail.equals(profileEmail)) {
                        docRef.update("email", editEmail, "password", password.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        resetSetup();
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    } else {
                        toast = Toast.makeText(ProfileInfo.this, "Email has been taken!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }
    private boolean isEmailTaken(final String sEmail) {
        for (String e : emails) {
            if (e.equals(sEmail)) {
                return true;
            }
        }
        return false;
    }

    private void resetSetup() {
        saveButton.setVisibility(View.INVISIBLE);
        email.setFocusable(false);
        password.setFocusable(false);
    }

    private boolean isValidInputs(String email, String password) {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (password.matches("[a-zA-Z0-9]+")) {
                if (password.length() > 5 && password.length() < 16) {
                    if (!password.contains(" ")) {
                        return true;
                    } else {
                        toast = Toast.makeText(this, "Password contains whitespace!", Toast.LENGTH_SHORT);
                    }
                } else {
                    toast = Toast.makeText(this, "Length of password should be between 6 and 16!", Toast.LENGTH_SHORT);
                }
            } else {
                toast = Toast.makeText(this, "Password contains invalid characters!", Toast.LENGTH_SHORT);
            }
        } else {
            toast = Toast.makeText(this, "Invalid email!", Toast.LENGTH_SHORT);
        }
        toast.show();
        return false;
    }
}
