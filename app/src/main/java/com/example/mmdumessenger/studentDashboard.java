package com.example.mmdumessenger;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class studentDashboard extends AppCompatActivity  implements   NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagePath="student/profile_pic/";
    private Uri image_uri;
    ImageView profileIv;
    TextView nameTv,emailTv;
    FirebaseFirestore db;
    private StorageTask uploadTask;
    ProgressDialog progressDialog;

    DatabaseReference reference;
    DocumentReference documentReference;

    //Permission Constants


    private static final int Image_request_code=100;




    private void DrawerBar() {
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        DrawerBar();
        //init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);
         profileIv=headerview.findViewById(R.id.profileIv);
         nameTv=headerview.findViewById(R.id.nameTv);
         emailTv=headerview.findViewById(R.id.emailTv);
         progressDialog= new ProgressDialog(this);
        user = mAuth.getCurrentUser();

         //storageReference
        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        Toast.makeText(this,"header"+headerview.getId(),Toast.LENGTH_SHORT).show();
        BottomNavigationView bottomNavigationView = findViewById(R.id.NavigationContent);
        bottomNavigationView.setOnNavigationItemSelectedListener(selected);
       // database=FirebaseDatabase.getInstance();
        FirebaseDatabase  users= FirebaseDatabase.getInstance();
        reference =users.getReference("Users/"+user.getUid());
        documentReference=db.collection("Users").document(user.getUid());
        reference.keepSynced(true);

        final HashMap<String,Object> isonline =new HashMap<>();
        isonline.put("online",true);
        isonline.put("last_seen",0l);

        final HashMap<String,Object> isoffline=new HashMap<>();
        isoffline.put("online",false);
        isoffline.put("last_seen",System.currentTimeMillis());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   boolean pls=Boolean.parseBoolean(""+dataSnapshot.child("online").getValue(Boolean.class));
                   Toast.makeText(getApplicationContext(),Boolean.toString(pls),Toast.LENGTH_SHORT).show();
                   Log.i("error---",Boolean.toString(pls));

                    if(!pls){
                        documentReference.update(isoffline);
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.onDisconnect().updateChildren(isoffline).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                reference.updateChildren(isonline);
                documentReference.update(isonline);
            }
        });



//        reference.child("online").setValue(true);
//        reference.child("last_seen").setValue(0l);
//        documentReference=db.collection("Users").document(user.getUid());
//       HashMap<String,Object> hashMap =new HashMap<>();
//       hashMap.put("online",true);
//       hashMap.put("last_seen",0l);
//       documentReference.update(hashMap);
//
//        final HashMap<String,Object> hashMap1=new HashMap<>();
//        hashMap1.put("online",false);
//        hashMap1.put("last_seen",System.currentTimeMillis());
//        reference.onDisconnect().updateChildren(hashMap1);






        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.Frame, fragment1, "");
        ft1.commit();

       profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  
                    openImage();

                }



       });
        nameAndEmailset();
    }


    private void openImage() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,Image_request_code);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver =getBaseContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void  uploadImage(){
       // final ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if(image_uri!=null){
            final StorageReference fileReference =storageReference.child(System.currentTimeMillis()+"."+getFileExtension(image_uri));
            uploadTask=fileReference.putFile(image_uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                  if(!task.isSuccessful()){
                      throw task.getException();

                  }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                     Uri downloadUri=task.getResult();
                     String mUri =downloadUri.toString();
                     DocumentReference referencestore=db.collection("Users").document(user.getUid());
                     HashMap<String,Object> hashMap =new HashMap<>();
                     hashMap.put("image",mUri);
                     referencestore.update(hashMap);
                     reference.updateChildren(hashMap);
                     nameAndEmailset();
                    progressDialog.dismiss();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                       progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"No image is selected",Toast.LENGTH_SHORT).show();

        }
    }
    private void nameAndEmailset() {
        user = mAuth.getCurrentUser();

        DocumentReference userdata = db.collection("Users").document(user.getUid());
        userdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    nameTv.setText(documentSnapshot.getString("name"));
                    emailTv.setText(documentSnapshot.getString("email"));
                    String dp = documentSnapshot.getString("image");
                    //Toast.makeText(getApplicationContext(),""+dp+"1",Toast.LENGTH_SHORT).show();

                           if(!dp.isEmpty()){
                               Picasso.get().load(dp).placeholder(R.drawable.ic_profile).into(profileIv);
                           }
                           else{
                               Picasso.get().load(R.drawable.ic_profile).placeholder(R.drawable.ic_profile).into(profileIv);
                           }



                    }

                else {
                    Toast.makeText(getApplicationContext(), "document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
        //Forebase working code
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                 Users users =ds.getValue(Users.class);
//                 try{
//                     if(user.getUid().equals(users.getUid())){
//                         nameTv.setText(users.getName());
//                         emailTv.setText(users.getEmail());
//                     }
//                 }catch (Exception e){
//                     e.printStackTrace();
//                 }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



    BottomNavigationView.OnNavigationItemSelectedListener selected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    HomeFragment fragment1 = new HomeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.Frame, fragment1, "");
                    ft1.commit();
                    return true;
                case R.id.chats:
                    ChatsFragment fragment2 = new ChatsFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.Frame, fragment2, "");
                    ft2.commit();
                    return true;
                case R.id.users:
                    UsersStudentFragment fragment3 = new UsersStudentFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.Frame, fragment3, "");
                    ft3.commit();
                    return true;

                case R.id.Profile:
                   ProfileFragment fragment4 = new ProfileFragment();
                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.Frame, fragment4, "");
                    ft4.commit();
                    return true;



            }
            return false;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                final HashMap<String,Object> isoffline=new HashMap<>();
                isoffline.put("online",false);
                isoffline.put("last_seen",System.currentTimeMillis());
                reference.updateChildren(isoffline);
                checkUser();
                return true;

            case R.id.Profile:
                startActivity(new Intent(studentDashboard.this,ProfileActivity.class));
                finish();

            default:
                return false;
        }
    }













    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Image_request_code && resultCode == RESULT_OK
                && data != null&& data.getData()!=null) {
            image_uri=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload is in Progress",Toast.LENGTH_SHORT).show();
            }
            else{
                uploadImage();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.studentmenu,menu);

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                return false;
            default:
                break;
        }
        return false;
    }
    //

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }

    }
    @Override
    protected void onStart() {
        checkUser();
        super.onStart();
    }

    private void checkUser(){
       user=mAuth.getCurrentUser();
        if(user!=null){

        }else{
            Intent intent=new Intent(studentDashboard.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    }

