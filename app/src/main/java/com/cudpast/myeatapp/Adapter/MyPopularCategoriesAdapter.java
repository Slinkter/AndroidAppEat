package com.cudpast.myeatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cudpast.myeatapp.Model.PopularCategoryModel;
import com.cudpast.myeatapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPopularCategoriesAdapter extends RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder> {


    Context context;
    List<PopularCategoryModel> list_PopularCategoryModel;

    public MyPopularCategoriesAdapter(Context context, List<PopularCategoryModel> list_PopularCategoryModel) {
        this.context = context;
        this.list_PopularCategoryModel = list_PopularCategoryModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_categories_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(list_PopularCategoryModel.get(position).getImage()).into(holder.category_image);
        holder.txt_category_name.setText(list_PopularCategoryModel.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list_PopularCategoryModel.size();
    }
    // ------------------->
    // Class aux
    public class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;
        Unbinder unbinder;

        public MyViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }
}
