package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView CommentList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;

    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;


    private String Post_Key, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mAuth =FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");


        CommentList = (RecyclerView) findViewById(R.id.comments_list);
        CommentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentList.setLayoutManager(linearLayoutManager);

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_btn);


        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            String userName = dataSnapshot.child("username").getValue().toString();


                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter;

        FirebaseRecyclerOptions<Comments> options=
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(PostsRef,Comments.class)
                        .build();

        firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int i, @NonNull Comments comments)
                    {
                        commentsViewHolder.setUsername(comments.getUsername());
                        commentsViewHolder.setDate(comments.getDate());
                        commentsViewHolder.setTime(comments.getTime());
                        commentsViewHolder.setComment(comments.getComment());


                    }

                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
                        CommentsViewHolder commentsViewHolder=new CommentsViewHolder(view);
                        return commentsViewHolder;
                    }
                };

        CommentList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date)
        {
            TextView mydate=(TextView)mView.findViewById(R.id.comment_date);
            mydate.setText("Date: " + date);
        }
        public void setComment(String comment)
        {
            TextView mycomment=(TextView)mView.findViewById(R.id.comment_text);
            mycomment.setText(comment);
        }
        public void setTime(String time)
        {
            TextView mytime=(TextView)mView.findViewById(R.id.comment_time);
            mytime.setText("Time: " + time);
        }

        public void setUsername(String username)
        {
            TextView myusername=(TextView)mView.findViewById(R.id.comment_username);
            myusername.setText("@ " + username + "  ");
        }

    }

    private void ValidateComment(String userName) {

        String commentText = CommentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please write text to comment...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
                commentsMap.put("uid", current_user_id);
                commentsMap.put("comment", commentText);
                commentsMap.put("date", saveCurrentDate);
                commentsMap.put("time", saveCurrentTime);
                commentsMap.put("username", userName);

                PostsRef.child(RandomKey).updateChildren(commentsMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(CommentsActivity.this, "you have commented successfully", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(CommentsActivity.this, "Error Occured, try again...", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
        }

    }
}
