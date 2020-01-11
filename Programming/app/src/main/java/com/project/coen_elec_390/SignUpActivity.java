package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText username;
    private EditText phoneNumber;
    private EditText password;
    private EditText doorID;
    private Button picture;
    private Button signUp;
    private Button login;
    private Toast toast;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private Profile profile;
    private ArrayList<String> phoneNumbers;
    private ArrayList<String> usernames;

    private int invalidCount;
    private long timeout;
    private boolean locked;

    private final String TAG = "SIGNUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign up");

        username = findViewById(R.id.sUsername);
        phoneNumber = findViewById(R.id.sPhoneNum);
        password = findViewById(R.id.sPassword);
        doorID = findViewById(R.id.sDoorID);
        picture = findViewById(R.id.sPicture);
        signUp = findViewById(R.id.signUp);
        login = findViewById(R.id.sLogin);

        databaseHelper = new DatabaseHelper();
        sharedPreference = this.getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);

        profile = new Profile();
        phoneNumbers = new ArrayList<>();
        usernames = new ArrayList<>();

        invalidCount = 0;
        long seconds = (new Date().getTime()) / 1000;
        timeout = sharedPreference.getLong("time", seconds - 1L);
        if (seconds < timeout) {
            locked = true;
        } else {
            locked = false;
        }

        final FirebaseFirestore database = databaseHelper.getDatabase();

        database.collection("profiles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                phoneNumbers.add(document.getData().get("phoneNum").toString());
                                usernames.add(document.getData().get("username").toString());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                } else {
                    toast = Toast.makeText(SignUpActivity.this, "No network connection!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    final String sDoorID = doorID.getText().toString();
                    final String sUsername = username.getText().toString();
                    final String sPhoneNum = phoneNumber.getText().toString();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (!locked) {
                        if (isValidInputs(sUsername,
                                sPhoneNum,
                                password.getText().toString(),
                                sDoorID)) {
                            if (!isUsernameTaken(sUsername)) {
                                if (!isPhoneNumberTaken(sPhoneNum)) {
                                    if (filePath != null) {
                                        final StorageReference storageReference = databaseHelper.getStorageReference("door_" + sDoorID + "/profiles");
                                        database.collection("doors")
                                                .document(sDoorID)
                                                .update("adminChanged", true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    public void onSuccess(Void aVoid) {
                                                        if (filePath != null) {
                                                            storageReference.child(profile.getUsername() + ".jpg").putFile(filePath)
                                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                            while (!urlTask.isSuccessful());
                                                                            Uri downloadUrl = urlTask.getResult();

                                                                            SharedPreferences.Editor editor = sharedPreference.edit();
                                                                            editor.putString("username", profile.getUsername());
                                                                            editor.putInt("doorID", profile.getDoorID());
                                                                            editor.commit();

                                                                            HashMap<String, Object> user = new HashMap<>();
                                                                            user.put("doorID", profile.getDoorID());
                                                                            user.put("imageUrl", downloadUrl.toString());
                                                                            user.put("phoneNum", profile.getPhoneNumber());
                                                                            user.put("password", databaseHelper.get_SHA_512_SecurePassword(profile.getPassword()));
                                                                            user.put("username", profile.getUsername());

                                                                            database.collection("profiles")
                                                                                    .document(profile.getUsername())
                                                                                    .set(user)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            toast = Toast.makeText(SignUpActivity.this, "Unable to sign up, please try again.", Toast.LENGTH_SHORT);
                                                                                            toast.show();
                                                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                        }
                                                                                    });
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ++invalidCount;
                                                if (invalidCount > 2) {
                                                    locked = true;

                                                    timeout = ((new Date().getTime()) / 1000) + 3600L;
                                                    SharedPreferences.Editor editor = sharedPreference.edit();
                                                    editor.putLong("time", timeout);
                                                    editor.apply();

                                                    Log.d(TAG, "Timeout: " + timeout);

                                                    toast = Toast.makeText(SignUpActivity.this, "Timeout! Please wait for 1 hour!", Toast.LENGTH_SHORT);
                                                    ;
                                                } else {
                                                    toast = Toast.makeText(SignUpActivity.this, "Wrong door identifier!", Toast.LENGTH_SHORT);
                                                }
                                                toast.show();
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            }
                                        });
                                    } else {
                                        toast = Toast.makeText(SignUpActivity.this, "A picture has not been chosen!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }
                                } else {
                                    toast = Toast.makeText(SignUpActivity.this, "Phone number has been taken!", Toast.LENGTH_SHORT);
                                    toast.show();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            } else {
                                toast = Toast.makeText(SignUpActivity.this, "Username has been taken!", Toast.LENGTH_SHORT);
                                toast.show();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        } else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    } else {
                        toast = Toast.makeText(SignUpActivity.this, "You have been timed out!", Toast.LENGTH_SHORT);
                        toast.show();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                } else {
                    toast = Toast.makeText(SignUpActivity.this, "No network connection!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                BitmapDrawable drawable = new BitmapDrawable(this.getResources(), bitmap);
                picture.setText("");
                picture.setBackground(drawable);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPhoneNumberTaken(final String sPhoneNumber) {
        for (String e : phoneNumbers) {
            if (e.equals(sPhoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameTaken(final String sUsername) {
        for (String u : usernames) {
            if (u.equals(sUsername)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidInputs(String username, String phoneNum, String password, String doorID) {
        if (!username.isEmpty() && !phoneNum.isEmpty() && !password.isEmpty() && !doorID.isEmpty()) {
            if (username.matches("[a-zA-Z0-9]+")) {
                if (username.length() < 16) {
                    if (!TextUtils.isEmpty(phoneNum) && Pattern.matches("[a-zA-Z]+", phoneNum) == false && phoneNum.length() == 10) {
                        if (doorID.matches("[0-9]+")) {
                            int id = Integer.parseInt(doorID);
                            if (id > 0) {
                                if (password.matches("[a-zA-Z0-9]+")) {
                                    if (password.length() > 5 && password.length() < 16) {
                                        if (!password.contains(" ")) {
                                            //Save valid information
                                            profile.setUsername(username);
                                            profile.setPhoneNumber(phoneNum);
                                            profile.setPassword(password);
                                            profile.setDoorID(id);

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
                                toast = Toast.makeText(this, "Door ID should be bigger than 0!", Toast.LENGTH_SHORT);
                            }
                        } else {
                            toast = Toast.makeText(this, "Door ID should only contain numbers!", Toast.LENGTH_SHORT);
                        }
                    } else {
                        toast = Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT);
                    }
                } else {
                    toast = Toast.makeText(this, "Maximum length for user names is 16 characters!", Toast.LENGTH_SHORT);
                }
            } else {
                toast = Toast.makeText(this, "Username contains invalid characters!", Toast.LENGTH_SHORT);
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
