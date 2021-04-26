package com.studybean.studybeanliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.studybean.studybeanliteapp.Common.Common;
import com.studybean.studybeanliteapp.Model.Question;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static com.studybean.studybeanliteapp.Common.Common.categoryId;

public class Start2 extends AppCompatActivity implements View.OnClickListener{
    private TextToSpeech mTTs;
    int cc=0;
    int tCo=0, tWr=0;

    int i,lastCorrectAnswer,tap,totalCorrect,corAns=0,lim;

    TextView textCorrect,showCorrect,shareAppId;

    FirebaseDatabase database;
    DatabaseReference questionIndex,correctAnswer,questions,last,practice;

    FirebaseAuth mAuth;
    FirebaseUser user;

    String uid;

    ImageView question_image;
    TextView question_text;
    Button btnA,btnB,btnC,btnD,nextQuestion, bMusic;

    int index=0,totalQuestion=1,adTap=0,ps;
    String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);

        Bundle bundle = getIntent().getExtras();
        i = bundle.getInt("i");
        ps = bundle.getInt("ps");
        lim = bundle.getInt("lim");
        lastCorrectAnswer = bundle.getInt("totalCorrectAnswer");

        textCorrect = findViewById(R.id.correctAnswerId);
        showCorrect = findViewById(R.id.showCorrectId);
        shareAppId = findViewById(R.id.playingShareId);

        bMusic = findViewById(R.id.bmusic);

        mTTs = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = mTTs.setLanguage(Locale.US);

                    if(result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS","Language not supported");
                    }
                    else {
                        bMusic.setEnabled(true);
                    }
                }
                else {
                    Log.e("TTS","Initialization failed");
                }
            }
        });

        question_text = findViewById(R.id.questionTextId);

        question_image = findViewById(R.id.questionImageId);

        btnA = findViewById(R.id.btnAnswerA);
        btnB = findViewById(R.id.btnAnswerB);
        btnC = findViewById(R.id.btnAnswerC);
        btnD = findViewById(R.id.btnAnswerD);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        assert user != null;
        uid = user.getUid();

        database = FirebaseDatabase.getInstance();
        questions = database.getReference("Questions2");
        questionIndex = database.getReference("Question_Index");
        correctAnswer = database.getReference("Correct_Answer");
        last = database.getReference("Last_Usage");
        practice = database.getReference("Practice_Session");

        nextQuestion = findViewById(R.id.nextId);

        nextQuestion.setOnClickListener(this);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

        bMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        shareAppId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLink();
            }
        });

        last.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void shareLink() {
        Intent intent = new Intent(Start2.this,Done.class);
        intent.putExtra("tCo",tCo);
        intent.putExtra("tWr",tWr);
        finish();
        startActivity(intent);
    }

    private void speak() {
        String text = question_text.getText().toString();
        mTTs.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        if(mTTs != null)
        {
            mTTs.stop();
            mTTs.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        if(index < totalQuestion)
        {
            Button clickButton = (Button)v;

            btnA.setBackgroundColor(Color.WHITE);
            btnB.setBackgroundColor(Color.WHITE);
            btnC.setBackgroundColor(Color.WHITE);
            btnD.setBackgroundColor(Color.WHITE);

            if(clickButton.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
            {
                nextQuestion.setVisibility(View.VISIBLE);
                showCorrect.setVisibility(View.INVISIBLE);

                clickButton.setBackgroundColor(Color.parseColor("#008000"));
                clickButton.setTextColor(Color.parseColor("#ffffff"));

                corAns++;

                if(corAns==1)
                {
                    tCo++;
                    totalCorrect=totalCorrect+1;

                    questionIndex.child(String.format("%s",uid)).child(String.format("%s", categoryId))
                            .setValue(String.valueOf(index));

                    correctAnswer.child(String.format("%s",uid))
                            .setValue(String.valueOf(totalCorrect));

                    last.child(uid).child(String.valueOf(cc)).child("Question").setValue(Common.questionList.get(index).getQuestion());
                    last.child(uid).child(String.valueOf(cc)).child("Des").setValue(Common.questionList.get(index).getDes());
                    last.child(uid).child(String.valueOf(cc)).child("AnswerA").setValue(Common.questionList.get(index).getAnswerA());
                    last.child(uid).child(String.valueOf(cc)).child("AnswerB").setValue(Common.questionList.get(index).getAnswerB());
                    last.child(uid).child(String.valueOf(cc)).child("AnswerC").setValue(Common.questionList.get(index).getAnswerC());
                    last.child(uid).child(String.valueOf(cc)).child("AnswerD").setValue(Common.questionList.get(index).getAnswerD());
                    last.child(uid).child(String.valueOf(cc)).child("CorrectAnswer").setValue(Common.questionList.get(index).getCorrectAnswer());
                    last.child(uid).child(String.valueOf(cc)).child("CategoryId").setValue(categoryId);
                    last.child(uid).child(String.valueOf(cc)).child("Answer").setValue(clickButton.getText());

                    if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
                    {
                        last.child(uid).child(String.valueOf(cc)).child("IsImageQuestion").setValue("true");
                    }
                    else
                    {
                        last.child(uid).child(String.valueOf(cc)).child("IsImageQuestion").setValue("false");
                    }

                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Question").setValue(Common.questionList.get(index).getQuestion());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Des").setValue(Common.questionList.get(index).getDes());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerA").setValue(Common.questionList.get(index).getAnswerA());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerB").setValue(Common.questionList.get(index).getAnswerB());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerC").setValue(Common.questionList.get(index).getAnswerC());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerD").setValue(Common.questionList.get(index).getAnswerD());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("CorrectAnswer").setValue(Common.questionList.get(index).getCorrectAnswer());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("CategoryId").setValue(Common.QuesCategoryIndex);
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("CategoryId2").setValue(String.valueOf(ps));
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Answer").setValue(clickButton.getText());
                    practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Date").setValue(formattedDate);

                    if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
                    {
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("IsImageQuestion").setValue("true");
                    }
                    else
                    {
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("IsImageQuestion").setValue("false");
                    }

                }

                tap = 4;
            }
            else
            {
                if(clickButton==btnA|clickButton==btnB|clickButton==btnC|clickButton==btnD)
                {

                    nextQuestion.setVisibility(View.VISIBLE);

                    corAns++;

                    btnA.setTextColor(Color.BLACK);
                    btnB.setTextColor(Color.BLACK);
                    btnC.setTextColor(Color.BLACK);
                    btnD.setTextColor(Color.BLACK);

                    if(tap<4)
                    {
                        tWr++;

                        questionIndex.child(String.format("%s",uid)).child(String.format("%s", categoryId))
                                .setValue(String.valueOf(index));

                        correctAnswer.child(String.format("%s",uid))
                                .setValue(String.valueOf(totalCorrect));

                        last.child(uid).child(String.valueOf(cc)).child("Question").setValue(Common.questionList.get(index).getQuestion());
                        last.child(uid).child(String.valueOf(cc)).child("Des").setValue(Common.questionList.get(index).getDes());
                        last.child(uid).child(String.valueOf(cc)).child("AnswerA").setValue(Common.questionList.get(index).getAnswerA());
                        last.child(uid).child(String.valueOf(cc)).child("AnswerB").setValue(Common.questionList.get(index).getAnswerB());
                        last.child(uid).child(String.valueOf(cc)).child("AnswerC").setValue(Common.questionList.get(index).getAnswerC());
                        last.child(uid).child(String.valueOf(cc)).child("AnswerD").setValue(Common.questionList.get(index).getAnswerD());
                        last.child(uid).child(String.valueOf(cc)).child("CorrectAnswer").setValue(Common.questionList.get(index).getCorrectAnswer());
                        last.child(uid).child(String.valueOf(cc)).child("CategoryId").setValue(categoryId);
                        last.child(uid).child(String.valueOf(cc)).child("Answer").setValue(clickButton.getText());

                        if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
                        {
                            last.child(uid).child(String.valueOf(cc)).child("IsImageQuestion").setValue("true");
                        }
                        else
                        {
                            last.child(uid).child(String.valueOf(cc)).child("IsImageQuestion").setValue("false");
                        }

                        clickButton.setBackgroundColor(Color.RED);

                        showCorrect.setText("Wrong Answer; "+Common.questionList.get(index).getCorrectAnswer());
                        showCorrect.setTextColor(Color.parseColor("#FF0000"));
                        showCorrect.setVisibility(View.VISIBLE);
                        tap=5;

                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Question").setValue(Common.questionList.get(index).getQuestion());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Des").setValue(Common.questionList.get(index).getDes());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerA").setValue(Common.questionList.get(index).getAnswerA());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerB").setValue(Common.questionList.get(index).getAnswerB());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerC").setValue(Common.questionList.get(index).getAnswerC());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("AnswerD").setValue(Common.questionList.get(index).getAnswerD());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("CorrectAnswer").setValue(Common.questionList.get(index).getCorrectAnswer());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("CategoryId").setValue(Common.QuesCategoryIndex);
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("CategoryId2").setValue(String.valueOf(ps));
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Answer").setValue(clickButton.getText());
                        practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("Date").setValue(formattedDate);

                        if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
                        {
                            practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("IsImageQuestion").setValue("true");
                        }
                        else
                        {
                            practice.child(uid).child(String.valueOf(ps)).child(String.valueOf(cc)).child("IsImageQuestion").setValue("false");
                        }
                    }
                    else
                    {
                        tap=7;
                    }
                }
            }

            if(clickButton==nextQuestion)
            {
                nextQuestion.setVisibility(View.INVISIBLE);
                showCorrect.setVisibility(View.INVISIBLE);
                adTap=0;
                showQuestion(++index);
                tap = 0;
                corAns = 0;
            }

        }
    }

    private void showQuestion(int index) {
        cc++;
        if(cc==lim+1)
        {
            Intent intent = new Intent(Start2.this,Done.class);
            intent.putExtra("tCo",tCo);
            intent.putExtra("tWr",tWr);
            finish();
            startActivity(intent);
        }

        textCorrect.setText(String.valueOf(cc+"/"+lim));

        nextQuestion.setBackgroundColor(Color.parseColor("#2196F3"));
        btnA.setTextColor(Color.BLACK);
        btnB.setTextColor(Color.BLACK);
        btnC.setTextColor(Color.BLACK);
        btnD.setTextColor(Color.BLACK);

        if(index < totalQuestion)
        {
            if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                bMusic.setVisibility(View.GONE);

                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            }

            else
            {
                question_text.setText(Common.questionList.get(index).getQuestion());

                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());
        }
        else
        {
            questionIndex.child(String.format("%s",uid)).child(String.format("%s", categoryId))
                    .setValue(String.valueOf(0));
            //Intent intent = new Intent(this,Done.class);
            //startActivity(intent);
            Intent intent = new Intent(Start2.this,Done.class);
            intent.putExtra("tCo",tCo);
            intent.putExtra("tWr",tWr);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {

        if(!isConnected(Start2.this)) buildDialog(Start2.this).show();

        index = i;

        totalCorrect = lastCorrectAnswer;

        super.onResume();

        questions.orderByChild("CategoryId").equalTo(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.questionList.clear();
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            Question ques = postSnapshot.getValue(Question.class);
                            Common.questionList.add(ques);
                        }
                        totalQuestion = Common.questionList.size();

                        showQuestion(index);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        correctAnswer.child(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    String f = dataSnapshot.getValue(String.class);

                    int lu = Integer.parseInt(f);

                    totalCorrect=lu;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        btnA.setBackgroundColor(Color.WHITE);
        btnB.setBackgroundColor(Color.WHITE);
        btnC.setBackgroundColor(Color.WHITE);
        btnD.setBackgroundColor(Color.WHITE);

        btnA.setTextColor(Color.BLACK);
        btnB.setTextColor(Color.BLACK);
        btnC.setTextColor(Color.BLACK);
        btnD.setTextColor(Color.BLACK);

        i=index;
        lastCorrectAnswer=totalCorrect;
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to update the last question index. Press ok to go home if internet is not available.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }
}
