package com.example.kaildyhoang.mycookbookapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kaildyhoang.mycookbookapplication.models.Chat;
import com.example.kaildyhoang.mycookbookapplication.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by LOC on 7/8/2017.
 */

public class ChatActivity extends AppCompatActivity {
    EditText _edtChat;
    Button _btnChat;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference chatDatabaseRef,mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseRecyclerAdapter<Chat, ChatViewHolder> mChatAdapter;
    private String TAG = "ChatAdapter", userID;
    //    private String keyFriend;
    public ChatActivity() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initialiseScreen();

    }

    private void initialiseScreen(){

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://cookbookapplication-396d8.appspot.com");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        _btnChat = (Button) findViewById(R.id.buttonChat);
        _edtChat =(EditText) findViewById(R.id.editTextChat);
        //ONCLICK
        _btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCreateMessenger();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        doLoadBundle();
    }
    private void doLoadBundle(){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        Intent intent = getIntent();
        final String keyFriend = intent.getBundleExtra("MyBundleForChat").getString("keyFriend");

        final String room_type_1 = userID +"_"+keyFriend ;
        final String room_type_2 = keyFriend +"_"+ userID;

        mDatabaseRef.child("chat").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(room_type_1)){
                    loadMsg(room_type_1);
                }else if(dataSnapshot.hasChild(room_type_2)){
                    loadMsg(room_type_2);
                }else{
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadMsg(String type){
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        mChatAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.activity_chat_items,
                ChatViewHolder.class,
                mDatabaseRef.child("chat/"+type))
        {

            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, int position) {
                viewHolder.messenger(model.getMessenger());
                viewHolder.avatarPicture(model.getAvatar());

                if(model.getSender().equals(uid)){

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);

                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params2.addRule(RelativeLayout.START_OF,viewHolder._llAvatar.getId());
                    params2.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
                    params2.setMarginStart(150);
                    params2.setMarginEnd(10);

                    viewHolder._llAvatar.setLayoutParams(params);
                    viewHolder._llMsg.setLayoutParams(params2);
                    viewHolder._llMsg.setBackgroundResource(R.drawable.sender_rounded_corner);
                    viewHolder._llMsg.setPadding(30,30,30,30);
                    viewHolder._txtVMessenger.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
            }
        };
        recyclerView.setAdapter(mChatAdapter);
    }
    //    View holder for recycler view
    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        private final TextView  _txtVMessenger;
        private final ImageView _imgVAvatar;
        private final LinearLayout _llAvatar,_llMsg;

        public ChatViewHolder(final View itemView) {
            super(itemView);

            //TextView
            _txtVMessenger = (TextView) itemView.findViewById(R.id.textViewUserNameChat);

            //ImageView
            _imgVAvatar = (ImageView) itemView.findViewById(R.id.imageViewAvatarChat);

            //LinearLayout
            _llAvatar = (LinearLayout) itemView.findViewById(R.id.linearLayoutAvatar);
            _llMsg = (LinearLayout) itemView.findViewById(R.id.linearLayoutMsg);



        }
        private void messenger(String title){
            _txtVMessenger.setText(title);
        }
        private void avatarPicture(String title){
            Glide.with(itemView.getContext())
                    .load(title)
                    .crossFade()
                    .placeholder(R.drawable.load_icon)
                    .thumbnail(0.1f)
                    .transform(new CircleTransform(itemView.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(_imgVAvatar);
        }
    }

    private void doCreateMessenger(){

        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        Intent intent = getIntent();
        final String keyFriend = intent.getBundleExtra("MyBundleForChat").getString("keyFriend");

        final String room_type_1 = uid +"_"+keyFriend ;
        final String room_type_2 = keyFriend +"_"+ uid;

        mDatabaseRef.child("chat/").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(room_type_1)){
                    mDatabaseRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            final String avatar = user.getAvatar();
                            String messenger = _edtChat.getText().toString();

                            final Chat chat = new Chat(
                                    messenger,
                                    avatar,
                                    uid,
                                    keyFriend
                            );
                            final String postKey = mDatabaseRef.child("chat").child(uid+keyFriend).push().getKey();
                            mDatabaseRef.child("chat").child(room_type_1).child(postKey).setValue(chat);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else if(dataSnapshot.hasChild(room_type_2)){
                    mDatabaseRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            final String avatar = user.getAvatar();
                            String messenger = _edtChat.getText().toString();

                            final Chat chat = new Chat(
                                    messenger,
                                    avatar,
                                    uid,
                                    keyFriend
                            );
                            final String postKey = mDatabaseRef.child("chat").child(uid+keyFriend).push().getKey();
                            mDatabaseRef.child("chat").child(room_type_2).child(postKey).setValue(chat);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    mDatabaseRef.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            final String avatar = user.getAvatar();
                            String messenger = _edtChat.getText().toString();

                            final Chat chat = new Chat(
                                    messenger,
                                    avatar,
                                    uid,
                                    keyFriend
                            );
                            final String postKey = mDatabaseRef.child("chat").child(uid+keyFriend).push().getKey();
                            mDatabaseRef.child("chat").child(room_type_1).child(postKey).setValue(chat);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
