package com.example.mmdumessenger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText emailEt, passwordEt;
    Button loginBtn;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    TextView forgetPasswordTv,notRegisterTv;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        forgetPasswordTv=findViewById(R.id.forgetPasswordTv);
        notRegisterTv=findViewById(R.id.notRegisterTv);
        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        //forget password listener
        forgetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordRecoverDialogBox();
            }
        });

        //loginButton listener

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Invalid Email");
                    emailEt.setFocusable(true);
                } else if (password.length() < 8) {
                    passwordEt.setError("Invalid Email");
                    passwordEt.setFocusable(true);
                } else {
                    progressDialog.setMessage("Logging");

                    progressDialog.show();
                    SignIn(email, password);
                }

            }


        });

        notRegisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
        private void SignIn(String email, String password) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");

                                FirebaseUser user = mAuth.getCurrentUser();
                               final String uid=user.getUid();
                               //firebase Working code
                                //FirebaseDatabase databse= FirebaseDatabase.getInstance();
                                //DatabaseReference referenceStudent=databse.getReference("Students");
                                //DatabaseReference referenceTeacher=databse.getReference("Teachers");
//                                referenceStudent.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
//                                            try {
//                                                if (uid.equals(ds.child("uid").getValue())) {
//                                                    progressDialog.dismiss();
//                                                    Intent intent = new Intent(LoginActivity.this, studentDashboard.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//                                            } catch (Exception e) {
//                                                progressDialog.dismiss();
//                                                    e.printStackTrace();
//                                            }
//                                        }}
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progressDialog.dismiss();
//                                    }
//                                });


                              // for teachers
//                                referenceTeacher.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
//                                         try{
//                                            if(uid.equals(ds.child("uid").getValue())){
//                                                progressDialog.dismiss();
//                                                Intent intent = new Intent(LoginActivity.this,teacherDashboard.class);
//                                                startActivity(intent);
//                                                finish();
//                                            }
//                                        }catch (Exception e){
//                                             progressDialog.dismiss();
//                                         e.printStackTrace();}
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progressDialog.dismiss();
//                                    }
//                                });

                                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                rootRef.collection("Users").document(uid)
                                 .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document=task.getResult();
                                            if(document.exists()){
                                                String userType=document.getString("userType");
                                                if(userType.equals("Students")){

                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, studentDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else if(userType.equals("Teachers")){
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, teacherDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                });

//                                 rootRef.collection("Users").orderBy(uid).whereEqualTo("userType","Teachers")
//                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        progressDialog.dismiss();
//                                    }
//                                });

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TaG", "signInWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }

    private  void PasswordRecoverDialogBox(){

        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Recover password");
        LinearLayout linearLayout= new LinearLayout(LoginActivity.this);

        final EditText email=new EditText(LoginActivity.this);
        email.setHint("Email");
        email.setMinEms(10);
        linearLayout.addView(email);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailcheck=email.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(emailcheck).matches()){
                    email.setError("Email is empty");
                    email.setFocusable(true);
                }
                else{

                    passwordRecovery(emailcheck);
                }

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //null
            }
        });
        builder.show();
    }

    private void passwordRecovery(String s) {
        mAuth.sendPasswordResetEmail(s)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}


