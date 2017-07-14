package com.example.kaildyhoang.mycookbookapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kaildyhoang.mycookbookapplication.ChatActivity;
import com.example.kaildyhoang.mycookbookapplication.R;
import com.example.kaildyhoang.mycookbookapplication.models.Follow;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Left_Activity extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference followDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Follow,FollowViewHolder> mFollowdapter;

    public Left_Activity() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.v_activity_left,container,false);
        mAuth = FirebaseAuth.getInstance();

        followDatabaseRef = FirebaseDatabase.getInstance().getReference();
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerViewFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        mFollowdapter = new FirebaseRecyclerAdapter<Follow, FollowViewHolder>(
                Follow.class,
                R.layout.activity_friend_items,
                FollowViewHolder.class,
                followDatabaseRef.child("users").child(uid).child("idFriendsList"))
        {
            @Override
            protected void populateViewHolder(FollowViewHolder viewHolder, final Follow model, int position) {
                viewHolder.avatar(model.getAvatar());
                viewHolder.name(model.getName());
                viewHolder.email(model.getEmail());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keyFriend = model.getIdFriend();
                        Intent intent = new Intent(getContext(),ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyFriend",keyFriend);
                        intent.putExtra("MyBundleForChat",bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(mFollowdapter);
        mFollowdapter.notifyDataSetChanged();
    }
    //    View holder for recycler view
    public static class FollowViewHolder extends RecyclerView.ViewHolder{

        private final TextView _txtVName,_txtVEmail;
        private final ImageView _imgVAvatar;

        public FollowViewHolder(final View itemView) {
            super(itemView);
            //TextView
            _txtVName = (TextView) itemView.findViewById(R.id.textViewUserName);
            _txtVEmail = (TextView) itemView.findViewById(R.id.textViewEmailItem);
            _imgVAvatar=(ImageView) itemView.findViewById(R.id.imageViewAvatar);

        }
        private void name(String title){_txtVName.setText(title);
        }
        private void email(String title){_txtVEmail.setText(title);
        }
        private void avatar(String title){
            Glide.with(itemView.getContext())
                    .load(title)
                    .crossFade()
                    .placeholder(R.drawable.load_icon)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(_imgVAvatar);
        }
    }
}