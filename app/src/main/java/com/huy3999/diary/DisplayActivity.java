package com.huy3999.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView mEntryList;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Query mQueryCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mAuth = FirebaseAuth.getInstance();

        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Entries");
        mDatabase.keepSynced(true);

        mQueryCurrentUser = mDatabase.orderByChild("uid").equalTo(currentUserId);


        mEntryList = findViewById(R.id.entry_list);
        mEntryList.setHasFixedSize(true);
        mEntryList.setLayoutManager(new LinearLayoutManager(this));


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null ){
                    startActivity(new Intent(DisplayActivity.this, MainActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Entry, EntryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Entry, EntryViewHolder>(
                Entry.class,
                R.layout.entry_card,
                EntryViewHolder.class,
                mQueryCurrentUser

        ) {

            @Override
            protected void populateViewHolder(EntryViewHolder viewHolder, Entry model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setDate(model.getDate());
                viewHolder.setColor(model.getColor());
                //viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };

        mEntryList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public EntryViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title){

            TextView e_title = mView.findViewById(R.id.entry_title);
            e_title.setText(title);

        }

        public void setContent(String content){

            TextView e_content = mView.findViewById(R.id.entry_content);
            e_content.setText(content);
        }

        public void setDate(String date){

            TextView e_date = mView.findViewById(R.id.entry_date);
            e_date.setText(date);
        }
        public void setColor(int color){
            CardView cardView = mView.findViewById(R.id.entry_card);

            cardView.setCardBackgroundColor(color);

            //cardView.setBackgroundColor(color);
        }

//        public void setImage(Context ctx,String image){
//
//            ImageView e_image = (ImageView) mView.findViewById(R.id.entry_image);
//            Picasso.with(ctx).load(image).into(e_image);
//        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){

            startActivity(new Intent(DisplayActivity.this, EntryActivity.class));

        }
        if (item.getItemId() == R.id.action_logout){
            signout();

        }

        return super.onOptionsItemSelected(item);
    }

    private void signout() {
        mAuth.signOut();
    }
}
