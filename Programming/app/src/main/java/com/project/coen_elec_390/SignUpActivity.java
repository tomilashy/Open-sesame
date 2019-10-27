package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText username;
    private EditText email;
    private EditText password;
    private EditText doorID;
    private Button picture;
    private Button signUp;
    private Toast toast;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databasehelper;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.sUsername);
        email = findViewById(R.id.sEmail);
        password = findViewById(R.id.sPassword);
        doorID = findViewById(R.id.sDoorID);
        picture = findViewById(R.id.sPicture);
        signUp = findViewById(R.id.signUp);
        sharedPreference = this.getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE );
        databasehelper = new DatabaseHelper();

        profile = new Profile();

        picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isValidInputs(username.getText().toString(), email.getText().toString(),
                        password.getText().toString(), doorID.getText().toString())) {
                    if (filePath != null) {
                        databasehelper.setDoorID(profile.getDoorID());
                        databasehelper.addProfile(profile, filePath, SignUpActivity.this);

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        toast = Toast.makeText(SignUpActivity.this, "A picture has not been chosen!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                BitmapDrawable bdrawable = new BitmapDrawable(this.getResources(),bitmap);
                picture.setBackground(bdrawable);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidInputs(String username, String email, String password, String doorID) {
        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !doorID.isEmpty()) {
            if (username.length() < 16) {
                if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (doorID.matches("[0-9]+")) {
                        int id = Integer.parseInt(doorID);
                        if (id > 0) {
                            if (password.length() < 16) {
                                if (!password.contains(" ")) {
                                    //Save valid information
                                    profile.setUsername(username);
                                    profile.setEmail(email);
                                    profile.setPassword(password);
                                    profile.setDoorID(id);

                                    SharedPreferences.Editor editor = sharedPreference.edit();
                                    editor.putString("username", username );
                                    editor.putInt("dooID", id );
                                    editor.commit();

                                    return true;
                                } else {
                                    toast = Toast.makeText(this, "Password contains whitespace!", Toast.LENGTH_SHORT);
                                }
                            } else {
                                toast = Toast.makeText(this, "Maximum length for passwords is 16 characters!", Toast.LENGTH_SHORT);
                            }
                        } else {
                            toast = Toast.makeText(this, "Door ID should be bigger than 0!", Toast.LENGTH_SHORT);
                        }
                    } else {
                        toast = Toast.makeText(this, "Door ID should only contain numbers!", Toast.LENGTH_SHORT);
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