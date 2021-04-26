package com.studybean.studybeanliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Done extends AppCompatActivity {
    int tCo,tWr;
    TextView wrongId, correctId;

    ListView listViewFoodItem;
    FirebaseDatabase database;
    DatabaseReference last;
    String uid;
    FirebaseAuth mAuth;
    FirebaseUser user;
    List<ItemDetail> detailList;
    DetailList adapter;
    ItemDetail detail;

    Button dash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        dash = findViewById(R.id.dashboardId);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        assert user != null;
        uid = user.getUid();

        Bundle bundle = getIntent().getExtras();
        tCo = bundle.getInt("tCo");
        tWr = bundle.getInt("tWr");

        wrongId = findViewById(R.id.wrongId);
        correctId = findViewById(R.id.correctId);

        correctId.setText(String.valueOf(tCo));
        wrongId.setText(String.valueOf(tWr));

        database = FirebaseDatabase.getInstance();

        detailList = new ArrayList<>();

        listViewFoodItem = (ListView) findViewById(R.id.listBuyerViewId);
        last = database.getReference("Last_Usage");

        last.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        detailList.clear();

                        for(DataSnapshot detailSnapshot: dataSnapshot.getChildren()){
                            detail = detailSnapshot.getValue(ItemDetail.class);

                            detailList.add(detail);
                        }

                        adapter = new DetailList(Done.this, detailList);

                        listViewFoodItem.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        dash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Done.this,MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
