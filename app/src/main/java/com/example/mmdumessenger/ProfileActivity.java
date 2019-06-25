package com.example.mmdumessenger;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProfileActivity extends AppCompatActivity {
    private static final int verify_permission_request=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        if(checkPermissionArray(Permissions.Permissions)){
        }
        else {
            verifyPermissions(Permissions.Permissions);
        }
    }

    public  void verifyPermissions(String[] permissions){

        ActivityCompat.requestPermissions(ProfileActivity.this,permissions,verify_permission_request);
    }

    public boolean checkPermissionArray(String[] permissions){
        for(int i=0;i<permissions.length;i++){
            String check= permissions[i];
            if(!checkPermissions(check)){
                return  false;

            }
        }
        return true;
    }

    public  boolean checkPermissions(String permission){
        int PermissionRequest= ActivityCompat.checkSelfPermission(ProfileActivity.this,permission);
        if(PermissionRequest!= PackageManager.PERMISSION_GRANTED){
            return  false;
        }
        else{
            return  true;
        }
    }
}
