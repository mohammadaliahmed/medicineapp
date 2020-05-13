package com.appsinventiv.medicineapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.appsinventiv.medicineapp.Models.SubCategoryModel;
import com.appsinventiv.medicineapp.R;
import com.appsinventiv.medicineapp.Utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by AliAh on 26/06/2018.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    Context context;
    ArrayList<SubCategoryModel> itemList;

    public CategoriesAdapter(Context context, ArrayList<SubCategoryModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grocery_item_layout, parent, false);
        CategoriesAdapter.ViewHolder holder = new CategoriesAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SubCategoryModel model = itemList.get(position);
        holder.title.setText(model.getSubCategoryName());
        Glide.with(context).load(model.getPicUrl()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);
        }
    }
}
