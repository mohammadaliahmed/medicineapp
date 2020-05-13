package com.appsinventiv.medicineapp.Activities;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.appsinventiv.medicineapp.Adapters.ChatAdapter;
import com.appsinventiv.medicineapp.Interface.NotificationObserver;
import com.appsinventiv.medicineapp.Models.AdminModel;
import com.appsinventiv.medicineapp.Models.ChatModel;
import com.appsinventiv.medicineapp.R;
import com.appsinventiv.medicineapp.Utils.Constants;
import com.appsinventiv.medicineapp.Utils.NotificationAsync;
import com.appsinventiv.medicineapp.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class LiveChat extends AppCompatActivity implements NotificationObserver {

    DatabaseReference mDatabase;
    EditText message;
    FloatingActionButton send;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatAdapter adapter;
    ArrayList<ChatModel> chatModelArrayList = new ArrayList<>();
    int soundId;
    SoundPool sp;
    String adminFcmKey;
    boolean noData = true;
    String foneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);


        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(LiveChat.this, R.raw.tick_sound, 1);


    }



    @Override
    protected void onResume() {
        super.onResume();
        getAdminDetails();
        getMessagesFromServer();
        readAllMessages();
    }

    private void getAdminDetails() {
        mDatabase.child("Settings").child("AdminNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    foneNumber = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("Admin").child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AdminModel model = dataSnapshot.getValue(AdminModel.class);
                    if (model != null) {
                        LiveChat.this.setTitle(model.getId());
                        adminFcmKey = model.getFcmKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readAllMessages() {
        mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel chatModel = snapshot.getValue(ChatModel.class);
                        if (chatModel != null) {
                            if (!chatModel.getUsername().equals(SharedPrefs.getUserModel().getPhone())) {
                                mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(chatModel.getId()).child("status").setValue("read");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessagesFromServer() {
        recyclerView = findViewById(R.id.chats);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(LiveChat.this, chatModelArrayList);
        recyclerView.setAdapter(adapter);

        mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    noData = false;
                    chatModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = snapshot.getValue(ChatModel.class);
                        if (model != null) {
                            chatModelArrayList.add(model);
                            recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    noData = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                }

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    sendMessageToServer(Constants.MESSAGE_TYPE_TEXT, "", "");
                }

            }
        });

    }

    private void sendMessageToServer(final String type, final String url, String extension) {

        final String msg = message.getText().toString();
        message.setText(null);
        final String key = mDatabase.push().getKey();


        mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(key)
                .setValue(new ChatModel(key,
                        msg,
                        SharedPrefs.getUserModel().getPhone(),
                        System.currentTimeMillis(),
                        "sending",
                        SharedPrefs.getUserModel().getPhone(),
                        SharedPrefs.getUserModel().getName(),
                        type.equals(Constants.MESSAGE_TYPE_IMAGE) ? url : "",
                        type.equals(Constants.MESSAGE_TYPE_DOCUMENT) ? url : "",
                        "." + extension,
                        type


                )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                sp.play(soundId, 1, 1, 0, 0, 1);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatModelArrayList.size() - 1);

                mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(key).child("status").setValue("sent");


                NotificationAsync notificationAsync = new NotificationAsync(LiveChat.this);
                String NotificationTitle = "New message from " + SharedPrefs.getUserModel().getName();
                String NotificationMessage = "";
                if (type.equals(Constants.MESSAGE_TYPE_TEXT)) {
                    NotificationMessage = SharedPrefs.getUserModel().getPhone()+": " + msg;
                }
                notificationAsync.execute("ali", adminFcmKey, NotificationTitle, NotificationMessage, "Chat", key);


            }
        });


    }




    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccess(String chatId) {
        mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(chatId).child("status").setValue("delivered");
    }

    @Override
    public void onFailure() {

    }


}
