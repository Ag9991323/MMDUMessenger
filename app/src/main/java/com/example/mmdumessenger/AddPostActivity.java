package com.example.mmdumessenger;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbref;
    ActionBar actionBar;
    EditText postTitleEt,postDescriptionEt;
    Button updatePostbtn;
    ImageView postImageIv;
    private StorageTask uploadTask;


    //permission constant
    private static final int camera_request_code=100;
    private static final int storage_request_code=200;

    //imagePick constants
    private static final  int Image_pick_camera_code=300;
    private static  final int Image_pick_gallery_code=400;

    Uri images_uri=null;

    //progress bar
    ProgressDialog pd;
    //user info

    String name,email,uid,dp;
    //permission array
    String[] cameraPermissions;
    String[] storagePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        actionBar=getSupportActionBar();
        actionBar.setTitle("Add New Post");
        //enabled back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth =FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();

        dbref= FirebaseDatabase.getInstance().getReference("Users");

        Query query=dbref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    name=""+ds.child("name").getValue();
                    email=""+ds.child("email").getValue();
                    dp=""+ds.child("image").getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //permissions
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd=new ProgressDialog(this);



        //init

        postDescriptionEt=findViewById(R.id.postDescriptionEt);
        postImageIv=findViewById(R.id.postImageIv);
        postTitleEt=findViewById(R.id.postTitleEt);
        updatePostbtn=findViewById(R.id.postUploadBtn);


        postImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialogBox();
            }
        });


        updatePostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title= postTitleEt.getText().toString().trim();
                String Description=postDescriptionEt.getText().toString().trim();
                if(TextUtils.isEmpty(Title)){
                    postTitleEt.setFocusable(true);
                    postTitleEt.setError("please add Title");
                }
                else if(TextUtils.isEmpty(Description)){
                    postDescriptionEt.setError("please add some Description");
                    postDescriptionEt.setFocusable(true);
                }
                else{
                    if(images_uri == null){
                        uploadData(Title,Description,"noImage");
                    }
                    else{
                        uploadData(Title,Description,String.valueOf(images_uri));
                    }
                }
            }
        });
    }

    private void uploadData(final String title, final String description, String uri) {
        pd.setMessage("Publishing Post...");
        pd.show();

        Calendar calFor = Calendar.getInstance();
        java.text.SimpleDateFormat currentDate =new java.text.SimpleDateFormat("dd-MMMM-yyyy");
       final String saveCurrentDate = currentDate.format(calFor.getTime());


        java.text.SimpleDateFormat currentTime =new java.text.SimpleDateFormat("hh:mm:ss a");
       final String saveCurrentTime = currentTime.format(calFor.getTime());

       final String postRandomName = saveCurrentDate + saveCurrentTime;

       String FileAndPathName="post_"+postRandomName;
        if(!uri.equals("noImage")){
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), images_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();
            final StorageReference ref= FirebaseStorage.getInstance().getReference("PostImages").child(System.currentTimeMillis()+FileAndPathName+".jpg");
             uploadTask =ref.putBytes(fileInBytes);
                     uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                         @Override
                         public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                             if(!task.isSuccessful()){
                                 throw  task.getException();
                             }
                             else{
                                 return  ref.getDownloadUrl();
                             }
                         }
                     }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task) {
                     if (task.isSuccessful()) {
                         Uri downloadUri = task.getResult();
                         String mUri = downloadUri.toString();

                         // put image information
                         HashMap<String, Object> hashMap = new HashMap<>();
                         hashMap.put("name", name);
                         hashMap.put("uid", uid);
                         hashMap.put("email", email);
                         hashMap.put("dp", dp);
                         hashMap.put("postImage", mUri);
                         hashMap.put("title", title);
                         hashMap.put("description", description);
                         hashMap.put("date", saveCurrentDate);
                         hashMap.put("time", saveCurrentTime);

                         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                         reference.child(uid + postRandomName).setValue(hashMap)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         pd.dismiss();
                                         startActivity(new Intent(AddPostActivity.this,teacherDashboard.class));
                                         finish();
                                         Toast.makeText(getApplicationContext(), "Post Pubished", Toast.LENGTH_SHORT).show();
                                         //reset views
                                         postImageIv.setImageURI(null);
                                         postTitleEt.setText("");
                                         postDescriptionEt.setText("");
                                         images_uri = null;
                                     }

                                 }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 //failed adding post in database
                                 pd.dismiss();
                                 Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         });
                     } else {
                         Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                         pd.dismiss();
                     }
                 }}).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                             pd.dismiss();
                         }
                     });
//                     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                         @Override
//                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                             // image is posted on storage now get its url
//
//                             Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                             while(!uriTask.isSuccessful()){
//
//
//                                 if(uriTask.isSuccessful()){
//                                     String downloaduri=uriTask.getResult().toString();
//
//                                     // put image information
//                                     HashMap<String,Object> hashMap =new HashMap<>();
//                                     hashMap.put("name",name);
//                                     hashMap.put("uid",uid);
//                                     hashMap.put("email",email);
//                                     hashMap.put("dp",dp);
//                                     hashMap.put("postImage",downloaduri);
//                                     hashMap.put("title",title);
//                                     hashMap.put("description",description);
//                                     hashMap.put("date",saveCurrentDate);
//                                     hashMap.put("time",saveCurrentTime);
//
//                                     DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
//                                     reference.child(uid+postRandomName).setValue(hashMap)
//                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                 @Override
//                                                 public void onSuccess(Void aVoid) {
//                                                     pd.dismiss();
//                                                     Toast.makeText(getApplicationContext(),"Post Pubished",Toast.LENGTH_SHORT).show();
//                                                     //reset views
//                                                     postImageIv.setImageURI(null);
//                                                     postTitleEt.setText("");
//                                                     postDescriptionEt.setText("");
//                                                     images_uri=null;
//
//                                                 }
//                                             }).addOnFailureListener(new OnFailureListener() {
//                                         @Override
//                                         public void onFailure(@NonNull Exception e) {
//                                             //failed adding post in database
//                                             pd.dismiss();
//                                             Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
//                                         }
//                                     });
//                                 }
//                             }
//                         }
//                     }).addOnFailureListener(new OnFailureListener() {
//                 @Override
//                 public void onFailure(@NonNull Exception e) {
//                     pd.dismiss();
//                     Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
//                 }
//             });
        }
        else{
            //without image

            HashMap<String,Object> hashMap =new HashMap<>();
            hashMap.put("name",name);
            hashMap.put("uid",uid);
            hashMap.put("email",email);
            hashMap.put("dp",dp);
            hashMap.put("postImage","noImage");
            hashMap.put("title",title);
            hashMap.put("description",description);
            hashMap.put("date",saveCurrentDate);
            hashMap.put("time",saveCurrentTime);

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
            reference.child(uid+postRandomName).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            startActivity(new Intent(AddPostActivity.this,teacherDashboard.class));
                            finish();
                            Toast.makeText(getApplicationContext(),"Post Pubished",Toast.LENGTH_SHORT).show();

                            //reset views
                            postImageIv.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                            postTitleEt.setText("");
                            postDescriptionEt.setText("");
                            images_uri=null;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed adding post in database
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void showImagePickDialogBox() {

        String[] options ={"Camera","Gallery"};

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //camera
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else{
                        PickfromCamera();

                    }

                }
                if(which==1){
                    //gallery
                    if(!checkstoragePermission()){
                      requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();

    }

    private void pickFromGallery() {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent,Image_pick_gallery_code);
    }

    private void PickfromCamera() {
        ContentValues cv= new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        images_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,images_uri);
        startActivityForResult(intent,Image_pick_camera_code);
    }

    private boolean checkstoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(
                PackageManager.PERMISSION_GRANTED);
        return result;

        }

        private  void requestStoragePermission(){
            ActivityCompat.requestPermissions(this,storagePermissions,storage_request_code);

        }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(
                PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(
                PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    private  void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,camera_request_code);

    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // goto previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case camera_request_code :{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        PickfromCamera();
                    }
                    else{
                        Toast.makeText(this,"Camera and Storage Permission both are necessary...",Toast.LENGTH_SHORT).show();
                    }

                }
                else {

                }

            }
            break;
            case storage_request_code:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this,"Storage permission is necessary...",Toast.LENGTH_SHORT).show();
                    }

                }
                else{

                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode==Image_pick_gallery_code){
                images_uri=data.getData();

                Picasso.get().load(images_uri.toString()).fit().centerCrop().into(postImageIv);

            }
            else if(requestCode ==Image_pick_camera_code){
                 Log.i("error---",images_uri.toString());
                Picasso.get().load(images_uri.toString()).fit().centerCrop().into(postImageIv);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        checkUser();
        super.onStart();
    }

    private void checkUser(){
       FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            email =user.getEmail();
            uid =user.getUid();

        }else{
            Intent intent=new Intent(AddPostActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
