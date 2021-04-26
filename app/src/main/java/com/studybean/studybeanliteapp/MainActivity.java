package com.studybean.studybeanliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static final int Request_Code = 100;
    int red=0;

    ImageView profilePhotoId;

    FirebaseDatabase database;

    DatabaseReference correctAnswer, questionIndex, last;
    ListView listViewFoodItem;

    FirebaseUser user;

    String uid;
    int totalCoin, totalCorrect;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    TextView signOut;

    TextView contact,privacy,userEmail,empty;

    FirebaseAuth mAuth;
    CardView cardCat;

    TextView cor, tot;

    List<PracItemDetail> detailList;
    PracDetailList adapter;
    PracItemDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        empty = findViewById(R.id.emptyId);

        cor = findViewById(R.id.correctId);
        tot = findViewById(R.id.totId);

        cardCat = findViewById(R.id.cardCategoryId);

        profilePhotoId = findViewById(R.id.profilePhotoId);

        userEmail = findViewById(R.id.userEmailId);

        privacy = findViewById(R.id.privacyId);
        privacy.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        else
        {
            user = mAuth.getCurrentUser();

            assert user != null;
            uid = user.getUid();

            database = FirebaseDatabase.getInstance();

            detailList = new ArrayList<>();

            correctAnswer = database.getReference("Correct_Answer");
            questionIndex = database.getReference("Question_Index");
            listViewFoodItem = (ListView) findViewById(R.id.listBuyerViewId);
            last = database.getReference("Practice_Session");

            setDefaultFragment();
            setUpToolbar();
        }

        if(mAuth.getCurrentUser()!=null)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();

                    // UID specific to the provider
                    String uid = profile.getUid();

                    // Name, email address, and profile photo Url
                    String name = profile.getDisplayName();
                    String email = profile.getEmail();
                    Uri photoUrl = profile.getPhotoUrl();

                    Picasso.with(MainActivity.this).load(photoUrl).resize(100,100).into(profilePhotoId);

                    userEmail.setText(name);
                }
            }
        }

        signOut = findViewById(R.id.signOutId);
        signOut.setOnClickListener(this);

        contact = findViewById(R.id.contactId);
        contact.setOnClickListener(this);

        cardCat.setOnClickListener(this);
    }

    private void setUpToolbar()
    {
        drawerLayout = findViewById(R.id.drawerLayoutId);
        toolbar = findViewById(R.id.toolBarId);

        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    private void setDefaultFragment() {
        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutId,CategoryFragment.newInstance());
        transaction.commit();*/
    }

    @Override
    public void onClick(View v) {
        if(v==signOut)
        {
            //LoginManager.getInstance().logOut();
            mAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        if(v==privacy)
        {
            String url = "http://sites.google.com/view/solutionpro";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

        if(v==cardCat)
        {
            startActivity(new Intent(this,Home.class));
        }

        if(v==contact)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            String[] to = {"applicationull@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of your mail");
            intent.putExtra(Intent.EXTRA_TEXT, "Description of your mail here");
            intent.setType("message/rcf822");
            Intent chooser=Intent.createChooser(intent,"Send Email");
            startActivity(chooser);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final int[] lol = {0};

        correctAnswer.child(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    String f = dataSnapshot.getValue(String.class);

                    int lu = Integer.parseInt(f);

                    totalCorrect=lu;

                    cor.setText(String.valueOf(totalCorrect));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for(int i=0;i<=1000;i++)
        {
            questionIndex.child(uid).child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null)
                    {
                        String lastIndex = dataSnapshot.getValue(String.class);

                        lol[0] = Integer.parseInt(lastIndex)+ lol[0];

                        tot.setText(String.valueOf(lol[0]));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        detailList.clear();
        for(int i=0;i<=1000;i++)
        {
            last.child(uid).child(String.valueOf(i)).child("1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue()!=null)
                    {
                        detail = dataSnapshot.getValue(PracItemDetail.class);

                        detailList.add(detail);

                        adapter = new PracDetailList(MainActivity.this, detailList);

                        listViewFoodItem.setAdapter(adapter);

                        empty.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
