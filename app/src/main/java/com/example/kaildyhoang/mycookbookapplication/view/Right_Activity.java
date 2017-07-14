package com.example.kaildyhoang.mycookbookapplication.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kaildyhoang.mycookbookapplication.CircleTransform;
import com.example.kaildyhoang.mycookbookapplication.R;
import com.example.kaildyhoang.mycookbookapplication.SignInActivity;
import com.example.kaildyhoang.mycookbookapplication.models.User;
import com.example.kaildyhoang.mycookbookapplication.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Right_Activity extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TextView _txtVName, _txtVSignOut;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private String userID;
    private ImageView _imgVAvatar;

    public Right_Activity() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.v_activity_right, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        progressDialog = new ProgressDialog(getContext());

        mViewPager = (ViewPager) view.findViewById(R.id.rightcontainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userAuth = mAuth.getCurrentUser();
        userID = userAuth.getUid();

        _txtVName = (TextView) view.findViewById(R.id.textViewNameUserRight);
        _imgVAvatar = (ImageView) view.findViewById(R.id.imageViewAvatarRight);
        _txtVSignOut = (TextView) view.findViewById(R.id.textViewSignOut);

        _txtVSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Signing out...");
                progressDialog.show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
            }
        });
        _imgVAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("keyFriend",userID);
                intent.putExtra("MyBundleFromMain",bundle);
                startActivity(intent);
            }
        });

        mDatabaseRef.child("users/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                _txtVName.setText(user.getName());
                Glide.with(getContext())
                        .load(user.getAvatar())
                        .crossFade()
                        .placeholder(R.drawable.load_icon)
                        .thumbnail(0.1f)
                        .transform(new CircleTransform(getContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(_imgVAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}