package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Messages;
import model.MessagesAdapter;

public class ChatActivity extends AppCompatActivity {

    String receiver_name, receiver_uid, sender_uid;
    CircleImageView profileImage;
    TextView receiverName;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    String senderRoom, receiverRoom;

    CardView sendBtn;
    EditText editMessage;

    RecyclerView messageAdapter;
    ArrayList<Messages> messageList;
    MessagesAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiver_name = getIntent().getStringExtra("name");
        receiver_uid = getIntent().getStringExtra("uid");
        profileImage = findViewById(R.id.profile_image);
        sendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.messageToSend);
        fDatabase = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();


        receiverName = findViewById(R.id.receiver_name);
        receiverName.setText(receiver_name);

        sender_uid = fAuth.getUid();
        senderRoom = sender_uid+receiver_uid;
        receiverRoom = receiver_uid+sender_uid;

        messageAdapter = findViewById(R.id.messageAdapter);
        messageList= new ArrayList<>();
        LinearLayoutManager LLM = new LinearLayoutManager(this);
        chatAdapter = new MessagesAdapter(ChatActivity.this, messageList);
        LLM.setStackFromEnd(true);
        messageAdapter.setLayoutManager(LLM);
        messageAdapter.setAdapter(chatAdapter);





        DatabaseReference chatReference = fDatabase.getReference().child("Chats").child(senderRoom).child("Messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dsnapshot: snapshot.getChildren()){
                    Messages messages = dsnapshot.getValue(Messages.class);
                    messageList.add(messages);
                }
                chatAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editMessage.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Message is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                editMessage.setText("");
                Date date = new Date();
                Messages messages = new Messages(message, fAuth.getUid(), date.getTime());
                fDatabase.getReference().child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        fDatabase.getReference().child("Chats")
                                .child(receiverRoom)
                                .child("Messages")
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {

                            }
                        });
                    }
                });

            }
        });




    }
}