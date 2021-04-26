package com.studybean.studybeanliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Review extends AppCompatActivity {
    int catId;

    ListView listViewFoodItem;
    FirebaseDatabase database;
    DatabaseReference last;
    String uid;
    FirebaseAuth mAuth;
    FirebaseUser user;
    List<ItemDetail> detailList;
    DetailList adapter;
    ItemDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        assert user != null;
        uid = user.getUid();

        Bundle bundle = getIntent().getExtras();
        catId = bundle.getInt("catId");

        database = FirebaseDatabase.getInstance();

        detailList = new ArrayList<>();

        listViewFoodItem = (ListView) findViewById(R.id.listBuyerViewId);
        last = database.getReference("Practice_Session");

        last.child(uid).child(String.valueOf(catId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        detail = dataSnapshot1.getValue(ItemDetail.class);

                        detailList.add(detail);

                        adapter = new DetailList(Review.this, detailList);

                        listViewFoodItem.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
