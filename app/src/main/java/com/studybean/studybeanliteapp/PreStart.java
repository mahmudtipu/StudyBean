package com.studybean.studybeanliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studybean.studybeanliteapp.Common.Common;
import com.studybean.studybeanliteapp.Model.Question;

import java.util.Collections;

public class PreStart extends AppCompatActivity {

    int i,totalCorrectAnswer, progress,clickGo=0;

    Button btnPlay, btnPlay2;

    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference questions;
    DatabaseReference questionIndex,correctAnswer,practice,limit;

    FirebaseAuth mAuth;
    FirebaseUser user;

    String uid;
    int ps=0,lim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_start);

        progressBar = findViewById(R.id.progressBarId);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        assert user != null;
        uid = user.getUid();

        database = FirebaseDatabase.getInstance();

        questions = database.getReference("Questions");

        questionIndex = database.getReference("Question_Index");
        correctAnswer = database.getReference("Correct_Answer");
        practice = database.getReference("Practice_Session");
        limit = database.getReference("Limit");

        btnPlay = findViewById(R.id.buttonPlay);
        btnPlay2 = findViewById(R.id.buttonPlay2);

        progressBar.setVisibility(View.VISIBLE);
        thread.start();

        limit.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                {
                    String limit = snapshot.getValue(String.class);
                    lim = Integer.parseInt(limit);
                }
                else
                {
                    lim = 21;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        correctAnswer.child(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    String lastCorrectAnswer = dataSnapshot.getValue(String.class);

                    totalCorrectAnswer = Integer.parseInt(lastCorrectAnswer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        questionIndex.child(uid).child(Common.categoryId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    String lastIndex = dataSnapshot.getValue(String.class);

                    i = Integer.parseInt(lastIndex)+1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        practice.child(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        ps = Integer.parseInt(dataSnapshot1.getKey())+1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Common.questionList.size()!=0&&clickGo==0)
                {
                    Intent intent = new Intent(PreStart.this,Start.class);
                    intent.putExtra("i",i);
                    intent.putExtra("ps",ps);
                    intent.putExtra("totalCorrectAnswer",totalCorrectAnswer);
                    finish();
                    startActivity(intent);
                    clickGo++;
                }
            }
        });

        btnPlay2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Common.questionList.size()!=0&&clickGo==0)
                {
                    Intent intent = new Intent(PreStart.this,StartPrac.class);
                    intent.putExtra("i",i);
                    intent.putExtra("totalCorrectAnswer",totalCorrectAnswer);
                    finish();
                    startActivity(intent);
                    clickGo++;
                }
            }
        });

        loadQuestions(Common.categoryId);
    }

    public  void doWork()
    {
        for(progress=10; progress<=500;progress=progress+10)
        {
            try {
                Thread.sleep(500);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadQuestions(String categoryId) {
        if(Common.questionList.size() > 0)
        {
            Common.questionList.clear();
        }

        questions.orderByChild("CategoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            Question ques = postSnapshot.getValue(Question.class);
                            Common.questionList.add(ques);

                            progressBar.setVisibility(View.GONE);
                        }

                        Intent intent = new Intent(PreStart.this,Start.class);
                        intent.putExtra("i",i);
                        intent.putExtra("ps",ps);
                        intent.putExtra("lim",lim);
                        intent.putExtra("totalCorrectAnswer",totalCorrectAnswer);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Collections.shuffle(Common.questionList);
    }
}
