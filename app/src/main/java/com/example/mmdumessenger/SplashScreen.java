package com.example.mmdumessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth =FirebaseAuth.getInstance();


    }
    protected void onStart() {
        super.onStart();
        checkUser();

    }

    private void checkUser(){
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseUser user = mAuth.getCurrentUser();
                    final String uid=user.getUid();



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


                                        Intent intent = new Intent(SplashScreen.this, studentDashboard.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(userType.equals("Teachers")){

                                        Intent intent = new Intent(SplashScreen.this, teacherDashboard.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        }
                    });




                    // Firebase working code
//                    FirebaseDatabase databse= FirebaseDatabase.getInstance();
//                    DatabaseReference referenceStudent=databse.getReference("Students");
//                    DatabaseReference referenceTeacher=databse.getReference("Teachers");
//                    referenceStudent.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for(DataSnapshot ds:dataSnapshot.getChildren()) {
//                                try {
//                                    if (uid.equals(ds.child("uid").getValue())) {
//
//                                        Intent intent = new Intent(SplashScreen.this, studentDashboard.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                } catch (Exception e) {
//
//                                    e.printStackTrace();
//                                }
//                            }}
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                    // for teachers
//                    referenceTeacher.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for(DataSnapshot ds:dataSnapshot.getChildren()){
//                                try{
//                                    if(uid.equals(ds.child("uid").getValue())){
//
//                                        Intent intent = new Intent(SplashScreen.this,teacherDashboard.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }catch (Exception e){
//
//                                    e.printStackTrace();}
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });


                }
            },100);

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            },1500);
        }

    }
}
