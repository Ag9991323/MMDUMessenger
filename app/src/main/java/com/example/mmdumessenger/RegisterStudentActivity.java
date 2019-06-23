package com.example.mmdumessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterStudentActivity extends AppCompatActivity {
    EditText nameEt,rollNoEt,passwordEt,emailEt;
    Button registerBtn;
    ProgressDialog progressDialog;
    TextView alreadyRegisteredTv;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        //init
        nameEt = findViewById(R.id.nameET);
        rollNoEt = findViewById(R.id.rollNoEt);
        passwordEt = findViewById(R.id.passwordEt);
        emailEt = findViewById(R.id.emailEt);
        registerBtn = findViewById(R.id.registerBtn);
        alreadyRegisteredTv=findViewById(R.id.alreadyRegisteredTv);


        //Progress Bar
        progressDialog = new ProgressDialog(this);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//
//
        alreadyRegisteredTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterStudentActivity.this,LoginActivity.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=nameEt.getText().toString().trim();
                String rollno=rollNoEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    nameEt.setError("please enter your name");
                    nameEt.setFocusable(true);
                }


                else if(TextUtils.isEmpty(rollno)){
                    rollNoEt.setError("please enter your RollNo");
                    rollNoEt.setFocusable(true);
                }


                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Invalid Email");
                    emailEt.setFocusable(true);
                } else if (password.length() < 8) {
                    passwordEt.setError("Password Length must be of 8 character");
                    passwordEt.setFocusable(true);
                } else {
                    progressDialog.setMessage("Registering....");
                    progressDialog.show();
                    SignUp(email, password);
                }
            }
        });
    }

    private void SignUp(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("issuccesful", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email= user.getEmail();
                            final String uid= user.getUid();
                            //firebase working code
//                            HashMap<String,Object>hashMap =new HashMap<>();
//                            hashMap.put("email",email);
//                            hashMap.put("uid",uid);
//                            hashMap.put("rollNo",rollNoEt.getText().toString());
//                            hashMap.put("name",nameEt.getText().toString());
//                            hashMap.put("image","");

                            //try firestore
                               final Map<String,Object> users=new HashMap<>();
                            users.put("userType","Students");
                            users.put("uid",uid);
                            users.put("email",email);
                            users.put("image","");
                            users.put("online",false);
                            users.put("last_seen",0l);
                            users.put("rollNo",rollNoEt.getText().toString());
                            users.put("name",nameEt.getText().toString());
                            db.collection("Users").document(uid).set(users)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                //firebase working code
                                                FirebaseDatabase database=FirebaseDatabase.getInstance();
                                                DatabaseReference reference=database.getReference("Users");

                                                reference.child(uid).setValue(users);
                                                Intent intent =new Intent(RegisterStudentActivity.this,studentDashboard.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(getApplicationContext(),"Account created successfully",Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"error "+e.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            //firebase working code
//                            FirebaseDatabase database=FirebaseDatabase.getInstance();
//                            DatabaseReference reference=database.getReference("Users");
//
//                            reference.child(uid).setValue(users);




                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w("isFailure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterStudentActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }
}


