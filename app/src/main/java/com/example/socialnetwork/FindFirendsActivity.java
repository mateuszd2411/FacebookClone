package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFirendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView SearchResultList;


    private DatabaseReference allUsersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_firends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Post");


        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));


        SearchButton = (ImageButton) findViewById(R.id.search_people_friends_button);
        SearchInputText = (EditText) findViewById(R.id.search_box_input);


        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchBoxInput = SearchInputText.getText().toString();

                SearchPeopleAndFriends(searchBoxInput);

            }
        });

    }


    private void SearchPeopleAndFriends(String searchBoxInput)
    {
        Query searchPeopleAndFriendsQuery=allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput+"uf8ff");
        FirebaseRecyclerOptions<FindFriends> options=new FirebaseRecyclerOptions.Builder<FindFriends>().
                setQuery(searchPeopleAndFriendsQuery, FindFriends.class).build();
        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> adapter=new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder viewHolder, final int position, @NonNull FindFriends findFriends) {
                final String PostKey = getRef(position).getKey();
                viewHolder.setFullname(findFriends.getFullname());
                viewHolder.setStatus(findFriends.getStatus());
                viewHolder.setProfileImage(getApplicationContext(), findFriends.getProfileimage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindFirendsActivity.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent findFriendsIntent = new Intent(FindFirendsActivity.this, FindFirendsActivity.class);
                        findFriendsIntent.putExtra("PostKey", PostKey);
                        startActivity(findFriendsIntent);

                    }
                });

            }
            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_display_layout,parent,false);

                FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        SearchResultList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(Context ctx, String profileimage) {
            CircleImageView myImage = mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);

        }

        public void setFullname(String fullname) {
            TextView myName=mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }
        public void setStatus(String status){
            TextView myStatus=mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }
    }


}
