package com.example.mmdumessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mmdumessenger.Adapter.ChatAdapter;
import com.example.mmdumessenger.Adapter.FirestoreChatAdapter;
import com.example.mmdumessenger.models.MessageModel;
import com.example.mmdumessenger.models.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    static RecyclerView recyclerView;
    ImageButton sendBtn;
    CircleImageView ProfilePictue;
    EditText sendMessage;
    TextView Name;
    TextView  status;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference studentReference,teacherReference;
    ListenerRegistration seenListener;
    String hisUid;
    String myUid;
    public static String Image;
    String documentId;
    FirebaseFirestore db;
    FirebaseUser user;
    List<MessageModel> chats_messageList;
    static ChatAdapter myadapter;
    FirestoreChatAdapter chatAdapter;
    Query seenQuery;
    boolean chatsAvailable =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        recyclerView = findViewById(R.id.Chat_RecycleView);
        sendBtn = findViewById(R.id.sendBtn);
        ProfilePictue = findViewById(R.id.profileIv);
        sendMessage = findViewById(R.id.sendMessage);
        Name = findViewById(R.id.ReciverName);
        status = findViewById(R.id.ReciverStatus);



//        final LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myUid = user.getUid();
        db = FirebaseFirestore.getInstance();
       firebaseDatabase=FirebaseDatabase.getInstance();
//        studentReference=firebaseDatabase.getReference().child("Students");
//        teacherReference=firebaseDatabase.getReference().child("Teachers");


        DatabaseReference reference=firebaseDatabase.getReference("Users/"+hisUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Users user =dataSnapshot.getValue(Users.class);
                    Name.setText(user.getName());
                    Image=user.getImage();
                    Boolean isonline=user.getOnline();
                    Long last_seen=user.getLast_seen();
                    if(isonline){
                        status.setText("online");
                    }
                    else{
                        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                        calendar.setTimeInMillis( last_seen);
                        String DateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                        status.setText("Last seen at:"+DateTime);
                    }




                    try {
                        Picasso.get().load(Image).placeholder(R.drawable.ic_profile_picture).into(ProfilePictue);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_profile_picture).into(ProfilePictue);
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        DocumentReference userdata = db.collection("Users").document(hisUid);
//        userdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    Name.setText(documentSnapshot.getString("name"));
//                    Image= documentSnapshot.getString("image");
//
//
//                    try {
//                        Picasso.get().load(Image).placeholder(R.drawable.ic_profile_picture).into(ProfilePictue);
//                    } catch (Exception e) {
//                        Picasso.get().load(R.drawable.ic_profile_picture).into(ProfilePictue);
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "document does not exist", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "message is empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessageFunc(message);
                }

            }
        });

        setupRecyclerView();
        seenMessage();

        //chatadapter se pehle ye chlta tha
//     if(user!=null) {
//         chats_messageList = new ArrayList<>();
//         myadapter = new ChatAdapter(ChatActivity.this, chats_messageList, Image);
//         Query chatsread = db.collection("Chats").document(myUid).collection("messages").document("all_message").collection(hisUid).orderBy("timeStamp");
//
//         chatsread.addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
//             @Override
//             public void onEvent(@Nullable QuerySnapshot
//                                         queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                 if (!queryDocumentSnapshots.isEmpty()) {
//
//                     for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                         if (doc.getType() == DocumentChange.Type.ADDED) {
//                             MessageModel messageModel = doc.getDocument().toObject(MessageModel.class);
//                             chats_messageList.add(messageModel);
//                             DocumentSnapshot lastVisible = queryDocumentSnapshots.getDocuments()
//                                     .get(queryDocumentSnapshots.size() - 1);
//                             Query next = db.collection("Chats").document(myUid).collection("messages").document("all_message").collection(hisUid).orderBy("timeStamp", Query.Direction.ASCENDING);
//                             next.startAfter(lastVisible);
//                             myadapter.notifyDataSetChanged();
//                         }
//                         if (doc.getType() == DocumentChange.Type.MODIFIED) {
//                             String docID = doc.getDocument().getId();
//                            MessageModel obj = doc.getDocument().toObject(MessageModel.class);
//                             if(doc.getOldIndex() == doc.getNewIndex())
//                             {
//                                 chats_messageList.set(doc.getOldIndex(),obj);
//                             }
//                             else
//                             {
//                                 chats_messageList.remove(doc.getOldIndex());
//                                 chats_messageList.add(doc.getNewIndex(),obj);
//                                 myadapter.notifyItemMoved(doc.getOldIndex(),doc.getNewIndex());
//                             }
//                             myadapter.notifyDataSetChanged();
//                         }
//
////                         recyclerView.setAdapter(myadapter);
//                     }
//                 }
//             }
//         });

//         myadapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//             @Override
//             public void onItemRangeInserted(int positionStart, int itemCount) {
//                 super.onItemRangeInserted(positionStart, itemCount);
//                 int friendlyMessageCount = myadapter.getItemCount();
//                 int lastVisiblePosition =
//                         linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                 // If the recycler view is initially being loaded or the
//                 // user is at the bottom of the list, scroll to the bottom
//                 // of the list to show the newly added message.
//                 if (lastVisiblePosition == -1 ||
//                         (positionStart >= (friendlyMessageCount - 1) &&
//                                 lastVisiblePosition == (positionStart - 1))) {
//                     recyclerView.scrollToPosition(positionStart);
//                 }
//             }
//         });

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
            }
        });
    }


    private void setupRecyclerView() {

        Query chatsQuery= db.collection("Chats").document(myUid).collection("messages").document("all_message").collection(hisUid).orderBy("timeStamp");

        FirestoreRecyclerOptions<MessageModel> options =new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(chatsQuery,MessageModel.class)
                .build();

        chatAdapter = new FirestoreChatAdapter(options,ChatActivity.this,Image);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

    }

    public void seenMessage(){
          seenQuery =db.collection("Chats").document(myUid).collection("messages").document("all_message").collection(hisUid);
        seenListener=seenQuery.addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentChange doc :queryDocumentSnapshots.getDocumentChanges()){
                            MessageModel model =doc.getDocument().toObject(MessageModel.class);
                            HashMap<String ,Object >seenHashMap=new HashMap<>();
                            seenHashMap.put("isSeen",true);
                            if(model.getReciever().equals(hisUid)&&model.getSender().equals(myUid)) {

                                    doc.getDocument().getReference().update(seenHashMap);
                                }



                    }
                }
            }
        });
    }

















//       Firebase working code
//        Query studentquery =studentReference.orderByChild("uid").equalTo(hisUId);
//        studentquery.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean student=false;
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                  String name=""+ds.child("name").getValue();
//                  String Image =""+ds.child("image").getValue();
//                  Log.i("name of reciever",name);
//                  Name.setText(name);
//                  student=true;
//                  try{
//                      Picasso.get().load(Image).placeholder(R.drawable.ic_profile_picture).into(ProfilePictue);
//                  }catch (Exception e){
//                      Picasso.get().load(R.drawable.ic_profile_picture).into(ProfilePictue);
//                  }
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//            Query teacherquery =teacherReference.orderByChild("uid").equalTo(hisUId);
//            teacherquery.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for(DataSnapshot ds:dataSnapshot.getChildren()){
//                        String name=""+ds.child("name").getValue();
//                        String Image =""+ds.child("image").getValue();
//                        Log.i("name of reciever",name);
//                        Name.setText(name);
//                        try{
//                            Picasso.get().load(Image).placeholder(R.drawable.ic_profile_picture).into(ProfilePictue);
//                        }catch (Exception e){
//                            Picasso.get().load(R.drawable.ic_profile_picture).into(ProfilePictue);
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

        private void sendMessageFunc(final String message) {
            String timeStamp =String.valueOf(System.currentTimeMillis());
              //chats message send

            final HashMap<String,Object> hashmap=new HashMap<>();
            hashmap.put("sender",myUid);
            hashmap.put("reciever",hisUid);
            hashmap.put("message",message);
            hashmap.put("timeStamp",timeStamp);
            hashmap.put("isSeen",false);
            db.collection("Chats").document(myUid).collection("messages").document("all_message").collection(hisUid).add(hashmap)
                 .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                     @Override
                     public void onSuccess(DocumentReference documentReference) {
                         final HashMap<String,Object> hashMap=new HashMap<>();
                         hashMap.put("hisuid",hisUid);
                         hashMap.put("myuid",myUid);


                         FirebaseDatabase database=FirebaseDatabase.getInstance();
                          DatabaseReference reference=database.getReference("Chats");
                          reference.child(myUid).child(hisUid).setValue(hashMap);



                         documentId=documentReference.getId();
                         db.collection("Chats").document(hisUid).collection("messages").document("all_message").collection(myUid).document(documentId).set(hashmap)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         Toast.makeText(getApplicationContext(),"msg sent",Toast.LENGTH_SHORT).show();

//                            db.collection("Users").document(myUid).collection("messages").document("all_message").collection(hisUid).document(documentId).set(hashmap);
//                                         db.collection("Users").document(hisUid).collection("messages").document("all_message").collection(myUid).document(documentId).set(hashmap);
                                     }
                                 });



                     }
                 });

                 sendMessage.setText("");
        }



          //firebase working Codes
//        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("Chats");
//        dbref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                chats_messageList.clear();
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                   MessageModel chat=ds.getValue(MessageModel.class);
//                    if(chat.getSender().equals(myUid)&&chat.getReciever().equals(hisUid)||
//                            chat.getSender().equals(hisUid)&&chat.getReciever().equals(myUid)){
//                        chats_messageList.add(chat);
//                    }
//                    chatMessageAdapter=new ChatAdapter(ChatActivity.this,chats_messageList,hisImage);
//                    chatMessageAdapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(chatMessage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    @Override
    protected void onStart() {
        super.onStart();
        checkUser();
        if(chatAdapter!=null){
            chatAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(chatAdapter!=null){
            chatAdapter.stopListening();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
       seenListener.remove();
    }

    private void checkUser() {
        FirebaseUser user =  mAuth.getCurrentUser();
        if(user!=null){
            //let see
            myUid=user.getUid();
        }
        else{
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

        }



