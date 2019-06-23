package com.example.mmdumessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button registerBtn, loginBtn;
    private RadioGroup radioGroup;
    private static  boolean isStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        radioGroup=findViewById(R.id.radioGroup);
        isStudent=true;




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioStudent:
                        isStudent=true;
                        Toast.makeText(getApplicationContext(),"student,",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioTeacher:
                        isStudent=false;
                        Toast.makeText(getApplicationContext(),"Teacher",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStudent==false){
                    Intent intent=new Intent(MainActivity.this,RegisterTeacherActivity.class);
                    startActivity(intent);



                }
                else{
                    Intent intent=new Intent(MainActivity.this,RegisterStudentActivity.class);
                    startActivity(intent);


                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

    }

}
