package com.studybean.studybeanliteapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.Nullable;
import com.studybean.studybeanliteapp.Common.Common;
import com.studybean.studybeanliteapp.Interface.ItemClickListener;
import com.studybean.studybeanliteapp.Model.Category;
import com.studybean.studybeanliteapp.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class CategoryFragment extends Fragment {

    View myFragment;

    RecyclerView listCategory;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,CategoryViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference categories;

    public static CategoryFragment newInstance()
    {
        CategoryFragment categoryFragment = new CategoryFragment();

        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_category,container,false);

        listCategory = myFragment.findViewById(R.id.listCategoryId);
        listCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        listCategory.setLayoutManager(layoutManager);

        loadCategories();

        return myFragment;
    }

    private void loadCategories() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_layout,
                CategoryViewHolder.class,
                categories
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                viewHolder.category_name.setText(model.getName());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent startGame =  new Intent(getActivity(),PreStart.class);
                        Common.categoryId = adapter.getRef(position).getKey();
                        Common.QuesCategoryIndex=model.getName();
                        startActivity(startGame);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        listCategory.setAdapter(adapter);
    }
}
