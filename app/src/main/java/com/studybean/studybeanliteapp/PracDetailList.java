package com.studybean.studybeanliteapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Tipu on 3/15/2018.
 */

public class PracDetailList extends ArrayAdapter<PracItemDetail>{
    private Activity context;
    private List<PracItemDetail> detailList;
    int item;
    TextView textView1;
    TextView textView2;
    Button review;

    public PracDetailList(Activity context, List<PracItemDetail> detailList){
        super(context,R.layout.prac_list_layout, detailList);
        this.context = context;
        this.detailList = detailList;
    }

    @Override
    public int getCount() {
        return detailList.size();
    }

    @Override
    public PracItemDetail getItem(int position) {
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

        View listViewItem = inflater.inflate(R.layout.prac_list_layout,null,true);

        textView1 = (TextView) listViewItem.findViewById(R.id.questionTextId);
        textView2 = (TextView) listViewItem.findViewById(R.id.desId);
        review = (Button) listViewItem.findViewById(R.id.reviewId);

        final PracItemDetail detail = detailList.get(position);

        textView2.setText(detail.getCategoryId());

        textView1.setText(detail.getDate());

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int catId = Integer.parseInt(detailList.get(position).getCategoryId2());

                Intent intent = new Intent(getContext(),Review.class);
                intent.putExtra("catId",catId);
                context.startActivity(intent);
            }
        });

        return listViewItem;
    }
}
