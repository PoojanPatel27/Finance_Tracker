package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {
    
    TextInputEditText name,contact,email,password,confirmPassword;
    Button registerBtn;
    ProgressBar progressBar;
    TextView goToLogin;

    DatabaseReference reference;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        
        name = findViewById(R.id.nameETRegister);
        contact = findViewById(R.id.contactETRegister);
        email = findViewById(R.id.emailETRegister);
        password = findViewById(R.id.passwordETRegister);
        confirmPassword = findViewById(R.id.confirmPasswordETRegister);
        registerBtn = findViewById(R.id.registerBtn);
        progressBar = findViewById(R.id.progressBarRegister);
        goToLogin = findViewById(R.id.goToLogin1TV);


        
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String txtName = name.getText().toString().trim();
               String txtContact = contact.getText().toString().trim();
               String txtEmail = email.getText().toString().trim();
               String txtPassword = password.getText().toString().trim();
               String txtConfirmPassword = confirmPassword.toString().trim();
               
               if (txtName.isEmpty()){
                   name.setError("Required");
                   name.requestFocus();
               } else if (txtContact.isEmpty()) {
                   contact.setError("Required");
                   contact.requestFocus();
               } else if (txtEmail.isEmpty()) {
                   email.setError("Required");
                   email.requestFocus();
               } else if (txtPassword.isEmpty()) {
                   password.setError("Required");
                   password.requestFocus();
               } else if (txtConfirmPassword.isEmpty()) {
                   confirmPassword.setError("Required");
                   confirmPassword.requestFocus();
               }
//               else if (!password.equals(confirmPassword)) {
//                   password.setError("Password did't match!");
//                   confirmPassword.setError("Password did't match!");
//                   password.requestFocus();
//                   confirmPassword.requestFocus();
//               }
               else {
                   RegUser(txtName,txtContact,txtEmail,txtPassword);
                   progressBar.setVisibility(View.VISIBLE);
               }

            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginUser.class));
            }
        });
    }

    private void RegUser(String txtName, String txtContact, String txtEmail, String txtPassword) {

        auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterUser.this, "Registered Success!!!", Toast.LENGTH_SHORT).show();
                    String UID = auth.getUid();
                    uploadUserData(txtName,txtContact,txtEmail,UID);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterUser.this, "something went wrong !!! " +e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadUserData(String txtName, String txtContact, String txtEmail, String uid) {

        reference = FirebaseDatabase.getInstance().getReference("Users");

        UserDetailsModel model = new UserDetailsModel(txtName,txtContact,txtEmail,uid);

        reference.child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterUser.this, "User Data Uploaded!!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterUser.this, "error uploading data due to "+e, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


    }
}