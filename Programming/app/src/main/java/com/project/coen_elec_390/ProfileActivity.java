package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

public class ProfileActivity extends AppCompatActivity {

    private TextView username;
    private EditText email;
    private EditText password;
    private TextView email_text;
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

    private String profileName;
    private String profileEmail;
    private String profileUrl;
    private int profileDoorID;
    private List<String> emails;

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
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_password);
        email_text = findViewById(R.id.text_email);
        password_text = findViewById(R.id.text_password);
        doorID = findViewById(R.id.text_doorID);
        saveButton = findViewById(R.id.saveButton);
        circularImageView = findViewById(R.id.circular_view);

        databaseHelper = new DatabaseHelper();
        db = databaseHelper.getDatabase();
        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        profileName = sharedPreference.getString("username", "");
        profileDoorID = sharedPreference.getInt("doorID", 0);
        docRef = db.collection("profiles").document(profileName);
        storageRef = FirebaseStorage.getInstance().getReference("door_" + profileDoorID + "/profiles");

        getListOfEmails();
        getDataFromFirestore();

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editMode == 1){
                    email.setVisibility(View.INVISIBLE);
                    password.setVisibility(View.INVISIBLE);
                    email_text.setVisibility(View.VISIBLE);
                    password_text.setVisibility(View.VISIBLE);
                    updateProfileInfo();
                } else if (editMode == 2) {
                    deletePreviousPicture();
                    uploadImage();
                    saveButton.setVisibility(View.INVISIBLE);
                    circularImageView.setEnabled(false);
                }
                itemImage.setVisible(true);
                itemProfile.setVisible(true);
                editMode = 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemProfile = menu.findItem(R.id.item2);
        itemImage = menu.findItem(R.id.item3);
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
            case R.id.item2:
                editMode = 1;
                email_text.setVisibility(View.INVISIBLE);
                password_text.setVisibility(View.INVISIBLE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                email.setFocusableInTouchMode(true);
                password.setFocusableInTouchMode(true);
                getListOfEmails();
                getDataFromFirestore();
                return true;
            case R.id.item3:
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
        final String editEmail = email.getText().toString();
        if (isValidInputs(editEmail, password.getText().toString())) {
            if (!isEmailTaken(editEmail) || editEmail.equals(profileEmail)) {
                docRef.update("email", editEmail, "password", password.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveButton.setVisibility(View.INVISIBLE);
                        email.setFocusable(false);
                        password.setFocusable(false);
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
            } else {
                toast = Toast.makeText(ProfileActivity.this, "Email has been taken!", Toast.LENGTH_SHORT);
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
                        email.setText(document.getData().get("email").toString());
                        password.setText(document.getData().get("password").toString());
                        email_text.setText(document.getData().get("email").toString());
                        password_text.setText(document.getData().get("password").toString());
                        doorID.setText(document.getData().get("doorID").toString());
                        profileEmail = document.getData().get("email").toString();
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

    private void getListOfEmails () {
        emails = new ArrayList<String>();
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
                    Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    String url = downloadUrl.toString();
                    docRef.update("imageUrl", url).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
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
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
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

    private boolean isEmailTaken(final String sEmail) {
        for (String e : emails) {
            if (e.equals(sEmail)) {
                return true;
            }
        }
        return false;
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
