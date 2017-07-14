package com.example.kaildyhoang.mycookbookapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kaildyhoang.mycookbookapplication.models.Direction;
import com.example.kaildyhoang.mycookbookapplication.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostActivity extends AppCompatActivity {
    private TextView _txtVTitle, _txtVDescription, _txtVCountPeoples, _txtVIngredient, _txtVDirectionTime, _txtVDirection;
    private ImageView _imgVIllustration, _imgVDirectionIllustration;
    private DatabaseReference databaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initialiseScreen();
    }

    private void initialiseScreen(){

        databaseRef = FirebaseDatabase.getInstance().getReference();

        _txtVTitle = (TextView) findViewById(R.id.textViewTitlePost);
        _txtVDescription = (TextView) findViewById(R.id.textViewDesciption);
        _txtVCountPeoples = (TextView) findViewById(R.id.textViewCountOfPeople);
        _txtVIngredient = (TextView) findViewById(R.id.textViewIngredient);
        _txtVDirectionTime = (TextView) findViewById(R.id.textViewDirectionTime);
        _txtVDirection = (TextView) findViewById(R.id.textViewDirectionContent);

        _imgVIllustration = (ImageView) findViewById(R.id.imageViewIllPic);
        _imgVDirectionIllustration = (ImageView) findViewById(R.id.imageViewDirectionIllPic);
    }

    private void loadPost(String postID){
        databaseRef.child("posts/"+postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                _txtVTitle.setText(post.getTitle());
                _txtVDescription.setText(post.getDescription());
                _txtVCountPeoples.setText(String.valueOf(post.getEatPeopleCount())+" people(s)");
                _txtVIngredient.setText(post.getIngredient());
                _txtVDirectionTime.setText(String.valueOf(post.getCookTimeLimit())+" minutes");
                Glide.with(getApplicationContext())
                        .load(post.getIllustrationPicture())
                        .crossFade()
                        .placeholder(R.drawable.load_icon)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(_imgVIllustration);
                databaseRef.child("direction/"+post.getDirection()+"/01/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot scDataSnapshot) {
                        Direction direction = scDataSnapshot.getValue(Direction.class);
                        _txtVDirection.setText(direction.getDirectionCont());
                        Glide.with(getApplicationContext())
                                .load(direction.getDirectionIllustrationPicture())
                                .crossFade()
                                .placeholder(R.drawable.load_icon)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(_imgVDirectionIllustration);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getMessage());
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if(intent.hasExtra("MyPostKey")){
            Bundle bundle = intent.getBundleExtra("MyPostKey");
            String postID = bundle.getString("postId");
            loadPost(postID);
        }else{
            Toast.makeText(getApplicationContext(),"STh Wrong",Toast.LENGTH_SHORT).show();
        }
    }
}
