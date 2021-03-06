package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private TextView username;
    private EditText phoneNumber;
    private EditText password;
    private TextView phone_text;
    private TextView password_text;
    private TextView doorID;
    private Button saveButton;
    private CircularImageView circularImageView;
    private MenuItem itemProfile;
    private MenuItem itemImage;

    private SharedPreferences sharedPreference;
    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;
    private Uri imageUri;
    private StorageReference storageRef;
    private StorageTask uploadTask;
    private DocumentReference docRef;
    private DocumentReference docRef1;

    private String profileName;
    private String profilePhoneNumber;
    private String profileUrl;
    private int profileDoorID;
    private List<String> phoneNumbers;

    private Handler handler;

    private final String TAG = "ProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Toast toast;
    private int editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Edit Profile");
        editMode = 0;

        username = findViewById(R.id.text_user);
        phoneNumber = findViewById(R.id.edit_phone);
        password = findViewById(R.id.edit_password);
        phone_text = findViewById(R.id.text_phone);
        password_text = findViewById(R.id.text_password);
        doorID = findViewById(R.id.text_doorID);
        saveButton = findViewById(R.id.saveButton);
        circularImageView = findViewById(R.id.circular_view);

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper();
        db = databaseHelper.getDatabase();
        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        profileName = sharedPreference.getString("username", "");
        profileDoorID = sharedPreference.getInt("doorID", 0);
        docRef = db.collection("profiles").document(profileName);
        docRef1 = db.collection("doors").document(String.valueOf(profileDoorID));
        storageRef = FirebaseStorage.getInstance().getReference("door_" + profileDoorID + "/profiles");

        getListOfPhoneNumbers();
        getDataFromFirestore();

        handler = new Handler();

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editMode == 1) {
                    updateProfileInfo();
                } else if (editMode == 2) {
                    deletePreviousPicture();
                    uploadImage();
                    saveButton.setVisibility(View.INVISIBLE);
                    circularImageView.setEnabled(false);
                }
            }
        });

        handler.postDelayed(networkCheck, 0);
    }

    Runnable networkCheck = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(ProfileActivity.this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onStop() {
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemProfile = menu.findItem(R.id.editInfo);
        itemImage = menu.findItem(R.id.editPicture);
        if (editMode == 1) {
            itemImage.setVisible(false);
        } else if (editMode == 2) {
            itemProfile.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish(); // When home button in clicked, end the activity and return to MainActivity
            case R.id.editInfo:
                editMode = 1;
                phone_text.setVisibility(View.INVISIBLE);
                password_text.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                phoneNumber.setFocusableInTouchMode(true);
                password.setFocusableInTouchMode(true);
                getListOfPhoneNumbers();
                getDataFromFirestore();
                return true;
            case R.id.editPicture:
                editMode = 2;
                saveButton.setVisibility(View.VISIBLE);
                circularImageView.setEnabled(true);
                circularImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFileChooser();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void updateProfileInfo() {
        final String editPhoneNumber = phoneNumber.getText().toString();
        final String editPassword = password.getText().toString();

        if (isValidInputs(editPhoneNumber, editPassword)) {
            if (!isPhoneNumberTaken(editPhoneNumber) || editPhoneNumber.equals(profilePhoneNumber)) {
                docRef.update("phoneNum", editPhoneNumber,
                        "password", databaseHelper.get_SHA_512_SecurePassword(editPassword))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveButton.setVisibility(View.INVISIBLE);
                        phoneNumber.setFocusable(false);
                        password.setFocusable(false);

                        phone_text.setText(editPhoneNumber);

                        phoneNumber.setVisibility(View.INVISIBLE);
                        password.setVisibility(View.INVISIBLE);
                        phone_text.setVisibility(TextView.VISIBLE);
                        password_text.setVisibility(TextView.VISIBLE);

                        itemImage.setVisible(true);
                        itemProfile.setVisible(true);
                        editMode = 0;

                        toast = Toast.makeText(ProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT);
                        toast.show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
            } else {
                toast = Toast.makeText(ProfileActivity.this, "Phone number has been taken!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void getDataFromFirestore () {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username.setText(profileName);
                        phoneNumber.setText(document.getData().get("phoneNum").toString());
                        phone_text.setText(document.getData().get("phoneNum").toString());
                        doorID.setText(document.getData().get("doorID").toString());
                        profilePhoneNumber = document.getData().get("phoneNum").toString();
                        profileUrl = document.getData().get("imageUrl").toString();
                        Picasso.with(ProfileActivity.this)
                                .load(profileUrl)
                                .fit()
                                .centerCrop()
                                .into(circularImageView);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getListOfPhoneNumbers () {
        phoneNumbers = new ArrayList<String>();
        db.collection("profiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        phoneNumbers.add(document.getData().get("phoneNum").toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void deletePreviousPicture () {
        StorageReference fileReference = storageRef.child(String.valueOf(profileName));
        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "File successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Error occurred while deleting!");
            }
        });
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child((profileName + ".jpg"));
            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    String url = downloadUrl.toString();
                    docRef.update("imageUrl", url).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            itemImage.setVisible(true);
                            itemProfile.setVisible(true);
                            editMode = 0;

                            toast = Toast.makeText(ProfileActivity.this, "Image Updated!", Toast.LENGTH_SHORT);
                            toast.show();
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                    docRef1.update("adminChanged", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "adminChanged set to true!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            itemImage.setVisible(true);
            itemProfile.setVisible(true);
            editMode = 0;

            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(circularImageView);
        }
    }

    private boolean isPhoneNumberTaken(final String sPhoneNum) {
        for (String pN : phoneNumbers) {
            if (pN.equals(sPhoneNum)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidInputs(String phoneNum, String password) {
        if (!TextUtils.isEmpty(phoneNum) && Pattern.matches("[a-zA-Z]+", phoneNum) == false && phoneNum.length() == 10) {
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
                toast = Toast.makeText(this, "Password contains invalid characters or is empty!", Toast.LENGTH_SHORT);
            }
        } else {
            toast = Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT);
        }
        toast.show();
        return false;
    }
}
