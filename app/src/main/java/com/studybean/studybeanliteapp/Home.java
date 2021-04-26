package com.studybean.studybeanliteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    TextView dash;
    FirebaseUser user;
    String uid;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        else
        {
            dash = findViewById(R.id.dashId);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutId,CategoryFragment.newInstance());
            transaction.commit();

            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutId2,CategoryFragment2.newInstance());
            transaction2.commit();

            FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutId3,CategoryFragment3.newInstance());
            transaction3.commit();

            dash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home.this,MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
