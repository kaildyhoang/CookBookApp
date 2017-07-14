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
import android.widget.TextView;

import com.example.kaildyhoang.mycookbookapplication.models.Post;
import com.example.kaildyhoang.mycookbookapplication.PostActivity;
import com.example.kaildyhoang.mycookbookapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyMethodActivity extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference postDatabaseRef;
    private Calendar calendar;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Post,PostViewHolder> mPostAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_method,container,false);
        mAuth = FirebaseAuth.getInstance();

        postDatabaseRef = FirebaseDatabase.getInstance().getReference().child("posts");
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerViewMyMethodActivity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        calendar = Calendar.getInstance();
        final DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.activity_my_method_items,
                PostViewHolder.class,
                postDatabaseRef.orderByChild("postBy").equalTo(uid))
        {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, final int position) {
                long pCreateAt = Long.parseLong(model.getPostAt());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = new Date( pCreateAt * 1000);
                viewHolder.date("at: "+simpleDateFormat.format(date));
                viewHolder.title(model.getTitle());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keyPost = mPostAdapter.getRef(position).getKey();
                        Intent intent = new Intent(getContext(),PostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("postId",keyPost);
                        intent.putExtra("MyPostKey",bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(mPostAdapter);
        mPostAdapter.notifyDataSetChanged();
    }
    //    View holder for recycler view
    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private final TextView _txtVTitle, _txtVDate;

        public PostViewHolder(final View itemView) {
            super(itemView);
            //TextView
            _txtVTitle = (TextView) itemView.findViewById(R.id.textViewTitleMethod);
            _txtVDate = (TextView) itemView.findViewById(R.id.textViewDateMethod);

        }
        private void title(String title){_txtVTitle.setText(title);
        }
        private void date(String title){_txtVDate.setText(title);
        }
    }
}