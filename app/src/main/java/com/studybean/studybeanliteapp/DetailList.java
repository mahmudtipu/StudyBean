package com.studybean.studybeanliteapp;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.studybean.studybeanliteapp.Common.Common;

/**
 * Created by Tipu on 3/15/2018.
 */

public class DetailList extends ArrayAdapter<ItemDetail>{
    private Activity context;
    private List<ItemDetail> detailList;
    int item;
    TextView textView1;
    TextView textView2;
    Button ansA, ansB, ansC, ansD;
    ImageView question_image;

    public DetailList(Activity context, List<ItemDetail> detailList){
        super(context,R.layout.list_layout, detailList);
        this.context = context;
        this.detailList = detailList;
    }

    @Override
    public int getCount() {
        return detailList.size();
    }

    @Override
    public ItemDetail getItem(int position) {
        return detailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        textView1 = (TextView) listViewItem.findViewById(R.id.questionTextId);
        textView2 = (TextView) listViewItem.findViewById(R.id.desId);
        ansA = (Button) listViewItem.findViewById(R.id.btnAnswerA);
        ansB = (Button) listViewItem.findViewById(R.id.btnAnswerB);
        ansC = (Button) listViewItem.findViewById(R.id.btnAnswerC);
        ansD = (Button) listViewItem.findViewById(R.id.btnAnswerD);

        question_image = (ImageView) listViewItem.findViewById(R.id.questionImageId);

        final ItemDetail detail = detailList.get(position);

        textView2.setText(detail.getDes());
        ansA.setText(detail.getAnswerA());
        ansB.setText(detail.getAnswerB());
        ansC.setText(detail.getAnswerC());
        ansD.setText(detail.getAnswerD());

        if(detail.getIsImageQuestion().equals("true"))
        {

            Picasso.with(getContext())
                    .load(detail.getQuestion())
                    .into(question_image);
            question_image.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.INVISIBLE);
        }
        else
        {
            textView1.setText(detail.getQuestion());
        }

        if(detail.getAnswerA().equals(detail.getCorrectAnswer()))
        {
            ansA.setBackgroundColor(Color.parseColor("#008000"));
            ansA.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(detail.getAnswerB().equals(detail.getCorrectAnswer()))
        {
            ansB.setBackgroundColor(Color.parseColor("#008000"));
            ansB.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(detail.getAnswerC().equals(detail.getCorrectAnswer()))
        {
            ansC.setBackgroundColor(Color.parseColor("#008000"));
            ansC.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(detail.getAnswerD().equals(detail.getCorrectAnswer()))
        {
            ansD.setBackgroundColor(Color.parseColor("#008000"));
            ansD.setTextColor(Color.parseColor("#ffffff"));
        }

        if(detail.getAnswer().equals(detail.getAnswerA())&&!detail.getAnswerA().equals(detail.getCorrectAnswer()))
        {
            ansA.setBackgroundColor(Color.RED);
        }
        if(detail.getAnswer().equals(detail.getAnswerB())&&!detail.getAnswerB().equals(detail.getCorrectAnswer()))
        {
            ansB.setBackgroundColor(Color.RED);
        }
        else if(detail.getAnswer().equals(detail.getAnswerC())&&!detail.getAnswerC().equals(detail.getCorrectAnswer()))
        {
            ansC.setBackgroundColor(Color.RED);
        }
        else if(detail.getAnswer().equals(detail.getAnswerD())&&!detail.getAnswerD().equals(detail.getCorrectAnswer()))
        {
            ansD.setBackgroundColor(Color.RED);
        }

        return listViewItem;
    }
}
