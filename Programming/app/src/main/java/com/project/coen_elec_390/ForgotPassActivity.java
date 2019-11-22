package com.project.coen_elec_390;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText username;
    private Button sendSMS;
    private List<String> usernames;

    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;
    private DocumentReference docRef;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String TAG = "FORGOTPASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        setTitle("Forgot Password");

        username = findViewById(R.id.editUsername);
        sendSMS = findViewById(R.id.sms);

        databaseHelper = new DatabaseHelper();
        db = databaseHelper.getDatabase();
        getListOfUsernames();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }

        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String usernameInput = username.getText().toString();
                if(checkValidUsername(usernameInput)) {
                    docRef = db.collection("profiles").document(usernameInput);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String phoneNum = document.getData().get("phoneNum").toString();
                                    String password = document.getData().get("password").toString();
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    if(!phoneNum.isEmpty() && !password.isEmpty()) {
                                        if(checkPermission()) {
                                            String sms = usernameInput + ": Your password is " + password;
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(phoneNum, null, sms, null, null);
                                            Toast.makeText(ForgotPassActivity.this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(ForgotPassActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgotPassActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ForgotPassActivity.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(ForgotPassActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPassActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                    Button sendSMS1 = findViewById(R.id.sms);
                    sendSMS1.setEnabled(false);
                }
                break;
        }
    }

    private void getListOfUsernames () {
        usernames = new ArrayList<String>();
        db.collection("profiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        usernames.add(document.getData().get("username").toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private boolean checkValidUsername (String user) {
        for (String userN: usernames) {
            if(userN.equals(user)){
                return true;
            }
        }
        return false;
    }
}