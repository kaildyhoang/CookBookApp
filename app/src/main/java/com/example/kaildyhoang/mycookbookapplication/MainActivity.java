package com.example.kaildyhoang.mycookbookapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kaildyhoang.mycookbookapplication.models.BookMark;
import com.example.kaildyhoang.mycookbookapplication.models.LikeBy;
import com.example.kaildyhoang.mycookbookapplication.models.Post;
import com.example.kaildyhoang.mycookbookapplication.models.User;
import com.example.kaildyhoang.mycookbookapplication.view.Right_Activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends Fragment {

    private RecyclerView recyclerView;
    private EditText _searchView;
    private DatabaseReference databaseRef,postDatabaseRef;
    private ProgressDialog progressDialog;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mPostAdapter;
    private String TAG = "PostAdapter";
    private long countOfLikes;
    private Calendar calendar;
    private int REQUEST_CODE = 100;
    private String directionKey, postTitle, userName, postByAvatar;

    private FirebaseAuth mAuth;
    ImageView _imgVNavMenu,_imgVNoti, _imgVChat;
    public MainActivity() {
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Opening...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(getActivity(),AddNewPostActivity.class);
                intent.putExtra("options","add");
                startActivity(intent);
            }
        });

        initialiseScreen(view);
        return view;
    }


    private void initialiseScreen(View view){
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        postDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        _searchView = (EditText) view.findViewById(R.id.editText_search);
        recyclerView = (RecyclerView)view. findViewById(R.id.recyclerViewShow);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _imgVNavMenu =(ImageView)view.findViewById(R.id.imageViewNavMenu) ;

        Toast.makeText(getActivity(),"Wait! Loading post...", Toast.LENGTH_SHORT).show();

        _searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SearchFriendsActivity.class));
            }
        });

        _imgVNavMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"hehe...", Toast.LENGTH_SHORT).show();
                Fragment fragment = new Right_Activity();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.rightcontainer, fragment); // fragmen container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack(null);  // this will manage backstack
                transaction.commit();
                Toast.makeText(getActivity(),"haha...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        doLoadPost(uid);
    }

    private void doLoadPost(final String uid){
        calendar = Calendar.getInstance();
        final DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.activity_post_items,
                PostViewHolder.class,
                databaseRef.child("users/"+uid+"/friendsPosts").orderByChild("title"))
        {

            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final String postById = model.getPostBy();
                postTitle = model.getTitle();
                directionKey = model.getDirection();

                long pCreateAt = Long.parseLong(model.getPostAt());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = new Date( pCreateAt * 1000);

                int selectedItem = position;
                final String postKey = mPostAdapter.getRef(selectedItem).getKey();

                databaseRef.child("users/"+postById).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userName = user.getName();
                        postByAvatar = user.getAvatar();

                        viewHolder.postBy(userName);
                        viewHolder.avatarPicture(postByAvatar);

                        if(!dataSnapshot.getKey().equalsIgnoreCase(uid)){
                            viewHolder._txtVMenuOption.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getMessage());
                    }
                });

                databaseRef.child("users/"+uid+"/friendsPosts/"+postKey+"/likeBy/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        countOfLikes = dataSnapshot.getChildrenCount();
                        if(dataSnapshot.hasChild(uid)){
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

                databaseRef.child("users/"+uid+"/bookMarks/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        countOfBookmark = dataSnapshot.getChildrenCount();
                        if(dataSnapshot.hasChild(postKey)){
                            viewHolder._imgVUnBookmark.setVisibility(View.GONE);
                        }else{
                            viewHolder._imgVUnBookmark.setVisibility(View.VISIBLE);
                        }
//
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getMessage());
                    }
                });
                viewHolder.postWhen(simpleDateFormat.format(date));
                viewHolder.postTitle(postTitle);
                viewHolder.countOfLikes(String.valueOf(countOfLikes));
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
                                        Intent intent = new Intent(getContext(),AddNewPostActivity.class);
                                        intent.putExtra("postKey",postKey);
                                        intent.putExtra("directionKey",directionKey);
                                        intent.putExtra("options","edit");
                                        startActivity(intent);
                                        break;
                                    case R.id.menuDelete:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setMessage("Do you want to delete this data?").setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        int selectedItem = position;
                                                        String postKey = mPostAdapter.getRef(selectedItem).getKey();

                                                        Log.d(TAG, "Dy:onClick " + "position:" + selectedItem);

                                                        postDatabaseRef.child(postKey).removeValue();
                                                        databaseRef.child("direction/"+directionKey).removeValue();
//                                                        databaseRef.child("likeBy/"+likeByKey).removeValue();
                                                        databaseRef.child("users/"+uid+"/posts/"+postKey).removeValue();
//                                                        mPostAdapter.getRef(selectedItem).removeValue();

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
                        Intent intent = new Intent(getContext(),UserActivity.class);
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
                        Intent intent = new Intent(getContext(),PostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("postId",postKey);
                        intent.putExtra("MyPostKey",bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(mPostAdapter);
        mPostAdapter.notifyDataSetChanged();
    }

    private void doBookmark(String postId, String postTitle, String postById, String postByName, String postByAvatar){
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        BookMark bookMark = new BookMark(postId,postTitle,postById,postByName,postByAvatar);

        databaseRef.child("users/"+uid+"/bookMarks/"+postId).setValue(bookMark);
    }

    private void doUnBookmark(String postId){
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        databaseRef.child("users/"+uid+"/bookMarks/"+postId).removeValue();
    }

    private void doLike(String userName, String postKey){
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        final LikeBy likeby = new LikeBy(uid,userName);

        databaseRef.child("posts/"+postKey+"/likeBy/"+uid).setValue(likeby);
        databaseRef.child("users/"+uid+"/friendsPosts/"+postKey+"/likeBy/"+uid).setValue(likeby);

    }

    private void doUnLike(String postKey){
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        databaseRef.child("posts/"+postKey+"/likeBy/"+uid).removeValue();
        databaseRef.child("users/"+uid+"/friendsPosts/"+postKey+"/likeBy/"+uid).removeValue();

    }

    //    View holder for recycler view
    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private final TextView _txtVUserName, _txtVCountLikes, _txtVTitle, _txtVMenuOption,_txtVPostWhen;
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
                    .crossFade()
                    .placeholder(R.drawable.load_icon)
                    .thumbnail(0.1f)
                    .transform(new CircleTransform(itemView.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(_imgVAvatar);
        }
    }
}
