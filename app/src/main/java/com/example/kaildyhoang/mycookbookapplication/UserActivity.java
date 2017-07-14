package com.example.kaildyhoang.mycookbookapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kaildyhoang.mycookbookapplication.models.BookMark;
import com.example.kaildyhoang.mycookbookapplication.models.Follow;
import com.example.kaildyhoang.mycookbookapplication.models.LikeBy;
import com.example.kaildyhoang.mycookbookapplication.models.Post;
import com.example.kaildyhoang.mycookbookapplication.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class UserActivity extends AppCompatActivity {
    private ImageView _imgVUserAvatar,_imgVUserCover;
    private TextView _txtVShowUN;
    private Button _btnFollow,_btnChat;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef,postDatabaseRef;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mPostAdapter;
    private String TAG = "PostAdapter";
    private long countOfLikes;
    private Calendar calendar;
    private String nameFriend,keyFriend,uName,userID, directionKey, likeByKey, userName, postByAvatar, postTitle,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initialiseScreen();
    }

    private  void initialiseScreen(){

        _imgVUserAvatar = (ImageView) findViewById(R.id.imageViewAvatarUser);
        _imgVUserCover = (ImageView) findViewById(R.id.imageViewCoverUser);

        _txtVShowUN = (TextView) findViewById(R.id.textViewNameUser);

        _btnFollow = (Button) findViewById(R.id.buttonFollow);
        _btnChat = (Button) findViewById(R.id.buttonChat);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        postDatabaseRef = FirebaseDatabase.getInstance().getReference().child("posts");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserActivity.this));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        Toast.makeText(UserActivity.this,"Wait! Fetching list..." + userID, Toast.LENGTH_SHORT).show();


        _btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("keyFriend",keyFriend);
                intent.putExtra("MyBundleForChat",bundle);
                startActivity(intent);
            }
        });

        _btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_btnFollow.getText().equals("FOLLOW")){
                    doFollow();
                }else if(_btnFollow.getText().equals("UNFOLLOW")){
                    doUnFollow();
                }else{
                    doFollow();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        doCheckBundle();

        doCheckFollow();

        postDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                doLoadPost(keyFriend);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void doCheckBundle(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.containsKey("MyBundleFromMain")){
            keyFriend = intent.getBundleExtra("MyBundleFromMain").getString("keyFriend");
            Log.d(TAG,"MyBundleFromMain"+keyFriend);
            doLoadPost(keyFriend);
            if(keyFriend.equalsIgnoreCase(userID)){
                _btnFollow.setVisibility(View.GONE);
                _btnChat.setVisibility(View.GONE);
                _imgVUserAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyProfile",keyFriend);
                        intent.putExtra("MyBundleForEdit",bundle);
                        startActivity(intent);
                    }
                });
            }else {
                _btnFollow.setVisibility(View.VISIBLE);
            }
        }
        if(bundle.containsKey("MyBundleFromSearch")){
            keyFriend = intent.getBundleExtra("MyBundleFromSearch").getString("keyFriend");
            Log.d(TAG,"MyBundleFromSearch"+keyFriend);
            doLoadPost(keyFriend);
            if(keyFriend.equalsIgnoreCase(userID)){
                _btnFollow.setVisibility(View.GONE);
                _btnChat.setVisibility(View.GONE);
                _imgVUserAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyProfile",keyFriend);
                        intent.putExtra("MyBundleForEdit",bundle);
                        startActivity(intent);
                    }
                });
            }else {
                _btnFollow.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doCheckFollow(){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        databaseRef.child("users/"+userID+"/idFriendsList/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if(childDataSnapshot.getKey().equals(keyFriend)){
                        _btnFollow.setText("UNFOLLOW");
                    }else{
                        _btnFollow.setText("FOLLOW");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void doLoadPost(String keyFriend){

        calendar = Calendar.getInstance();
        final DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        databaseRef.child("users/"+keyFriend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameFriend = user.getName();
                Glide.with(getApplicationContext())
                        .load(user.getAvatar())
                        .crossFade()
                        .placeholder(R.drawable.load_icon)
                        .thumbnail(0.1f)
                        .transform(new CircleTransform(getApplicationContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(_imgVUserAvatar);
                _txtVShowUN.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.activity_post_items,
                PostViewHolder.class,
                postDatabaseRef.orderByChild("postBy").equalTo(keyFriend))
        {

            @Override
            protected void populateViewHolder(final UserActivity.PostViewHolder viewHolder, Post model, final int position) {
                final String postById = model.getPostBy();
                postTitle = model.getTitle();
                int selectedItem = position;
                String postKey = mPostAdapter.getRef(selectedItem).getKey();

                long pCreateAt = Long.parseLong(model.getPostAt());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = new Date( pCreateAt * 1000);

                databaseRef.child("users/"+postById).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userName = user.getName();
                        postByAvatar = user.getAvatar();
                        email = user.getEmail();

                        viewHolder.postBy(userName);
                        viewHolder.avatarPicture(postByAvatar);

                        if(!dataSnapshot.getKey().equalsIgnoreCase(userID)){
                            viewHolder._txtVMenuOption.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getMessage());
                    }
                });
                databaseRef.child("posts/"+postKey+"/likeBy/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        countOfLikes = dataSnapshot.getChildrenCount();
                        if(dataSnapshot.hasChild(userID)){
                            viewHolder._imgVUnLike.setVisibility(View.GONE);
                            viewHolder.countOfLikes(String.valueOf(countOfLikes));
                        }else{
                            viewHolder._imgVUnLike.setVisibility(View.VISIBLE);
                            viewHolder.countOfLikes(String.valueOf(countOfLikes));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getMessage());
                    }
                });
                viewHolder.postWhen(simpleDateFormat.format(date));
                viewHolder.postTitle(model.getTitle());
                viewHolder.countOfLikes(String.valueOf(model.getCountOfLikes()));
                viewHolder.illustrationPicture(model.getIllustrationPicture());

//                Item Click
                viewHolder._txtVMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(),viewHolder._txtVMenuOption);
                        popupMenu.inflate(R.menu.post_options_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.menuEdit:
                                        int selectedItem = position;
                                        String postKey = mPostAdapter.getRef(selectedItem).getKey();
                                        Intent intent = new Intent(getApplicationContext(),AddNewPostActivity.class);
                                        intent.putExtra("postKey",postKey);
                                        intent.putExtra("directionKey",directionKey);
                                        intent.putExtra("options","edit");
                                        startActivity(intent);
                                        break;
                                    case R.id.menuDelete:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                                        builder.setMessage("Do you want to delete this data?").setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        int selectedItem = position;
                                                        String postKey = mPostAdapter.getRef(selectedItem).getKey();

                                                        Log.d(TAG, "Dy:onClick " + "position:" + selectedItem);

                                                        postDatabaseRef.child(postKey).removeValue();
                                                        databaseRef.child("direction/"+directionKey).removeValue();
                                                        databaseRef.child("likeBy/"+likeByKey).removeValue();
                                                        databaseRef.child("users/"+userID+"/posts/"+postKey).removeValue();

                                                        mPostAdapter.notifyItemRemoved(selectedItem);
                                                        recyclerView.invalidate();

                                                        onStart();
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog dialog = builder.create();
                                        dialog.setTitle("Confirm");
                                        dialog.show();

                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
                viewHolder._imgVUnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItem = position;
                        String postKey = mPostAdapter.getRef(selectedItem).getKey();

                        viewHolder._imgVUnBookmark.setVisibility(View.GONE);

                        doBookmark(postKey,postTitle,postById,userName,postByAvatar);
                    }
                });

                viewHolder._imgVBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItem = position;
                        String postKey = mPostAdapter.getRef(selectedItem).getKey();

                        viewHolder._imgVUnBookmark.setVisibility(View.VISIBLE);

                        doUnBookmark(postKey);
                    }
                });
                viewHolder._imgVUnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItem = position;
                        String postKey = mPostAdapter.getRef(selectedItem).getKey();

                        viewHolder._imgVUnLike.setVisibility(View.GONE);
                        viewHolder._imgVLike.setVisibility(View.VISIBLE);

                        doLike(userName,postKey);

                        viewHolder.countOfLikes(String.valueOf(countOfLikes));
                    }
                });
                viewHolder._imgVLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItem = position;
                        String postKey = mPostAdapter.getRef(selectedItem).getKey();

                        viewHolder._imgVUnLike.setVisibility(View.VISIBLE);
                        doUnLike(postKey);

                        viewHolder.countOfLikes(String.valueOf(countOfLikes));
                    }
                });

                viewHolder._imgVAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyFriend",postById);
                        intent.putExtra("MyBundleFromMain",bundle);
                        startActivity(intent);
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedItem = position;
                        String postKey = mPostAdapter.getRef(selectedItem).getKey();
                        Intent intent = new Intent(getApplicationContext(),PostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("postId",postKey);
                        intent.putExtra("MyPostKey",bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(mPostAdapter);
    }

    private void doFollow(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        Follow follow = new Follow(keyFriend,postByAvatar,nameFriend,email);
        Map<String, Object> userPost = new HashMap<String, Object>();
        userPost.put(keyFriend,follow);
        databaseRef.child("users/"+userID+"/idFriendsList").updateChildren(userPost);
        databaseRef.child("users/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> beFollowed = new HashMap<String, Object>();
                User user = dataSnapshot.getValue(User.class);
                uName = user.getName();
                Follow follow = new Follow(userID,user.getAvatar(),user.getName(),user.getEmail());
                beFollowed.put(userID,follow);
                databaseRef.child("users/"+keyFriend+"/beFollowed").updateChildren(beFollowed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        _btnFollow.setText("UNFOLLOW");
    }

    private void doUnFollow(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        databaseRef.child("users/"+userID+"/idFriendsList/"+keyFriend).removeValue();
        _btnFollow.setText("FOLLOW");
        databaseRef.child("users/"+keyFriend+"/beFollowed/"+userID).removeValue();
    }

    private void doBookmark(String postId, String postTitle, String postById, String postByName, String postByAvatar){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        BookMark bookMark = new BookMark(postId,postTitle,postById,postByName,postByAvatar);

        databaseRef.child("users/"+userID+"/bookMarks/"+postId).setValue(bookMark);
    }

    private void doUnBookmark(String postId){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        databaseRef.child("users/"+userID+"/bookMarks/"+postId).removeValue();
    }

    private void doLike(String userName, final String postKey){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        final LikeBy likeby = new LikeBy(userID,userName);

        databaseRef.child("posts/"+postKey+"/likeBy/"+userID).setValue(likeby);
        databaseRef.child("users/"+userID+"/friendsPosts/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(postKey)){
                    databaseRef.child("users/"+userID+"/friendsPosts/"+postKey+"/likeBy/"+userID).setValue(likeby);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void doUnLike(String postKey){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        databaseRef.child("posts/"+postKey+"/likeBy/"+userID).removeValue();
        databaseRef.child("users/"+userID+"/friendsPosts/"+postKey+"/likeBy/"+userID).removeValue();

    }
    //    View holder for recycler view
    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private final TextView _txtVUserName, _txtVCountLikes, _txtVTitle, _txtVMenuOption, _txtVPostWhen;
        private final ImageView _imgVCover, _imgVAvatar, _imgVLike,_imgVUnLike ,_imgVBookmark, _imgVUnBookmark;

        public PostViewHolder(final View itemView) {
            super(itemView);

            //TextView
            _txtVUserName = (TextView) itemView.findViewById(R.id.textViewUserName);
            _txtVCountLikes = (TextView) itemView.findViewById(R.id.textViewCountLikes);
            _txtVTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            _txtVMenuOption = (TextView) itemView.findViewById(R.id.textViewOptionMenu);
            _txtVPostWhen = (TextView) itemView.findViewById(R.id.textViewPostWhen);

            //ImageView
            _imgVCover = (ImageView) itemView.findViewById(R.id.imageViewCover);
            _imgVAvatar = (ImageView) itemView.findViewById(R.id.imageViewAvatar);
            _imgVLike = (ImageView) itemView.findViewById(R.id.imageViewLikes) ;
            _imgVUnLike = (ImageView) itemView.findViewById(R.id.imageViewUnLikes) ;
            _imgVBookmark = (ImageView) itemView.findViewById(R.id.imageViewBookmark);
            _imgVUnBookmark = (ImageView) itemView.findViewById(R.id.imageViewUnBookmark);
        }

        private void postBy(String title){
            _txtVUserName.setText(title);
        }
        private void countOfLikes(String title){
            _txtVCountLikes.setText(String.valueOf(title));
        }
        private void postTitle(String title){
            _txtVTitle.setText(title);
        }
        private void postWhen(String title){
            _txtVPostWhen.setText(title);
        }
        private void illustrationPicture(String title){
            Glide.with(itemView.getContext())
                    .load(title)
                    .crossFade()
                    .placeholder(R.drawable.load_icon)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(_imgVCover);
        }
        private void avatarPicture(String title){
            Glide.with(itemView.getContext())
                    .load(title)
                    .centerCrop()
                    .placeholder(R.drawable.load_icon)
                    .thumbnail(0.1f)
                    .transform(new CircleTransform(itemView.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(_imgVAvatar);
        }
    }
}
