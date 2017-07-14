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

import com.example.kaildyhoang.mycookbookapplication.models.BookMark;
import com.example.kaildyhoang.mycookbookapplication.PostActivity;
import com.example.kaildyhoang.mycookbookapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BookMarkActivity extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference postDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<BookMark,BookMarkViewHolder> mBookMarkAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book_mark,container,false);
        mAuth = FirebaseAuth.getInstance();

        postDatabaseRef = FirebaseDatabase.getInstance().getReference();
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerViewBookMarkActivity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        mBookMarkAdapter = new FirebaseRecyclerAdapter<BookMark, BookMarkViewHolder>(
                BookMark.class,
                R.layout.activity_my_method_items,
                BookMarkViewHolder.class,
                postDatabaseRef.child("users").child(uid).child("bookMarks"))
        {
            @Override
            protected void populateViewHolder(BookMarkViewHolder viewHolder, BookMark model,final int position) {

                viewHolder.title(model.getBookMarkTitle());
                viewHolder.by("by: "+model.getPostByName());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keyPost = mBookMarkAdapter.getRef(position).getKey();
                        Intent intent = new Intent(getContext(),PostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("postId",keyPost);
                        intent.putExtra("MyPostKey",bundle);
                        startActivity(intent);
                    }
                });
            }

        };
        recyclerView.setAdapter(mBookMarkAdapter);
        mBookMarkAdapter.notifyDataSetChanged();
    }
    //    View holder for recycler view
    public static class BookMarkViewHolder extends RecyclerView.ViewHolder{

        private final TextView _txtVTitle, _txtVBy;

        public BookMarkViewHolder(final View itemView) {
            super(itemView);
            //TextView
            _txtVTitle = (TextView) itemView.findViewById(R.id.textViewTitleMethod);
            _txtVBy = (TextView) itemView.findViewById(R.id.textViewDateMethod);

        }
        private void title(String title){_txtVTitle.setText(title);
        }
        private void by(String title){_txtVBy.setText(title);
        }
    }
}