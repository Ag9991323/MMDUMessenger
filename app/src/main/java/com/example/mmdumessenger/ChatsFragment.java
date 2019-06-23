package com.example.mmdumessenger;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mmdumessenger.Adapter.FirestoreUsersAdapter;
import com.example.mmdumessenger.Adapter.UserschatAdapter;
import com.example.mmdumessenger.models.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    List<Users> usersList;
    RecyclerView user_recycle_view;
    FirebaseFirestore db;
    FirebaseUser user;
    FirestoreUsersAdapter usersStudentAdapter;
    UserschatAdapter adapter;
    ArrayList uids;
    int count=0;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_student, container, false);
        user_recycle_view=view.findViewById(R.id.recycleView);
        user_recycle_view.setHasFixedSize(true);
        user_recycle_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        //UsersAdapter adapter =new UsersAdapter(getActivity(),usersList);
        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
        count=0;

        usersList=new ArrayList<>();
        uids=new ArrayList();

        readUser();

        return view;
    }


    private void readStudentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query =rootRef.collection("Users").whereEqualTo("userType","Students");
        FirestoreRecyclerOptions<Users> options=new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query,Users.class)
                .build();
        usersStudentAdapter=new FirestoreUsersAdapter(options,getActivity());
        user_recycle_view.setAdapter(usersStudentAdapter);

    }





    private void readUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Chats/"+user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    if(ds.exists()){

                        if((""+ds.child("myuid").getValue()).equals(user.getUid())){
                            uids.add(""+ds.child("hisuid").getValue());
                        }
                    }



                }

                FirebaseDatabase database=FirebaseDatabase.getInstance();
                DatabaseReference reference=database.getReference("Users");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){

                            if(uids.size()!=0){

                                if(count<uids.size()){

                                    if(ds.child("uid").getValue().equals(uids.get(count))){
                                        Users studentUser = ds.getValue(Users.class);
                                        usersList.add(studentUser);

                                        adapter=new UserschatAdapter(getActivity(),usersList,studentUser.getOnline());
                                        user_recycle_view.setAdapter(adapter);
                                        count+=1;

                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                 Query query=db.collection("Users");
//                         query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                             @Override
//                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                 for(DocumentSnapshot ds:task.getResult()){
//
//                                     if(uids.size()!=0){
//
//                                         if(count<uids.size()){
//
//                                             if(ds.getId().equals(uids.get(count))){
//                                                 Users studentUser = ds.toObject(Users.class);
//                                                 usersList.add(studentUser);
//
//                                                 adapter=new UserschatAdapter(getActivity(),usersList,studentUser.getOnline());
//                                                 user_recycle_view.setAdapter(adapter);
//                                                 count+=1;
//
//                                             }
//                                         }
//
//                                     }
//
//
//
//                                 }
//
//                             }
//                         });
            count=0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }












//firestore working code

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//        Query query =rootRef.collection("Users").whereEqualTo("userType","Students");
//                query.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        usersList.clear();
//                        if(task.isSuccessful()){
//                            for(DocumentSnapshot userlist:task.getResult()){
//
//                                Users studentUser = userlist.toObject(Users.class);
//
//                                    try {
//                                        if(!studentUser.getUid().equals(user.getUid())){
//
//
////                                            FirebaseDatabase  users= FirebaseDatabase.getInstance();
////                                            DatabaseReference reference =users.getReference("Users/"+user.getUid());
////
////                                            reference.child("online").setValue(true);
////                                            reference.child("last_seen").setValue(0l);
////
////                                            reference.child("online").onDisconnect().setValue(false);
////                                            reference.child("last_seen").onDisconnect().setValue( ServerValue.TIMESTAMP);
//                                            usersList.add(studentUser);
//                                        }
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                    }
//                                    adapter = new UsersAdapter(getActivity(), usersList,studentUser.getOnline());
//                                    user_recycle_view.setAdapter(adapter);
//
//
//
//                            }
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });

        //upar wala firestore working code
        //Firebase Working Code
//        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference =database.getReference("Students");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Users studentUser = ds.getValue(Users.class);
//                    try {
//                        if (!(studentUser.getUid().equals(user.getUid()))) {
//                            usersList.add(studentUser);
//                            Log.i("modelUser", String.valueOf(studentUser));
//                        }
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                  UsersAdapter adapter = new UsersAdapter(getActivity(), usersList);
//
//                  user_recycle_view.setAdapter(adapter);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchitem=menu.findItem(R.id.action_search);
        SearchView searchview=(SearchView)searchitem.getActionView();

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }



    @Override
    public  void onStart() {
        super.onStart();

//        if(usersStudentAdapter!=null){
//           usersStudentAdapter.startListening();
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
//        if(usersStudentAdapter!=null){
//            usersStudentAdapter.stopListening();
//        }

    }

}
