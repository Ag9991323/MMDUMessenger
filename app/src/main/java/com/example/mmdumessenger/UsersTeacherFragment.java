package com.example.mmdumessenger;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mmdumessenger.Adapter.FirestoreTeacherAdapter;
import com.example.mmdumessenger.Adapter.UsersAdapter;
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
public class UsersTeacherFragment extends Fragment {
    List<Users> usersList;
    RecyclerView user_recycle_view;
    FirebaseFirestore db;
    FirebaseUser user;
    FirestoreTeacherAdapter userTeacherAdapter;
    UsersAdapter adapter;

    public UsersTeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_teacher, container, false);
        user_recycle_view=view.findViewById(R.id.recycleView);
        user_recycle_view.setHasFixedSize(true);
        user_recycle_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
        usersList=new ArrayList<>();
       readUser();
        return view;
    }

    private void readTeacherUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query =rootRef.collection("Users");
        FirestoreRecyclerOptions<Users> options=new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query,Users.class)
                .build();
        userTeacherAdapter=new FirestoreTeacherAdapter(options,getActivity());
        user_recycle_view.setAdapter(userTeacherAdapter);

    }
    private void readUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Users studentUser =ds.getValue(Users.class);
           Log.i("teacherdepartment",studentUser.getName()+studentUser.getOnline());


                    try {
                        if(!studentUser.getUid().equals(user.getUid())&&((studentUser.getUserType().equals("Students"))||(studentUser.getUserType().equals("Teachers")))){

                            usersList.add(studentUser);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    adapter = new UsersAdapter(getActivity(), usersList,true);
                    user_recycle_view.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        //firestore working code


//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        db.collection("Users").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        usersList.clear();
//                        if(task.isSuccessful()){
//                            for(DocumentSnapshot userlist:task.getResult()){
//                                Users studentUser = userlist.toObject(Users.class);
//                                try {
//                                    if(!studentUser.getUid().equals(user.getUid())){
//                                        usersList.add(studentUser);
//                                    }
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                                 adapter = new UsersAdapter(getActivity(), usersList,true);
//                                user_recycle_view.setAdapter(adapter);
//                            }
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });

        //firebase working Code
//        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference studentReference =database.getReference("Students");
//        DatabaseReference teacherReference =database.getReference("Teachers");
//        studentReference.addValueEventListener(new ValueEventListener() {
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
//                    UsersAdapter adapter = new UsersAdapter(getActivity(), usersList);
//
//                    user_recycle_view.setAdapter(adapter);
//
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        teacherReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
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
//                    UsersAdapter adapter = new UsersAdapter(getActivity(), usersList);
//
//                    user_recycle_view.setAdapter(adapter);
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

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

//        if(userTeacherAdapter!=null){
//            userTeacherAdapter.startListening();
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
//        if(userTeacherAdapter!=null){
//            userTeacherAdapter.stopListening();
//        }

    }

}