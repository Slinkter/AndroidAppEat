package com.cudpast.myeatapp.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cudpast.myeatapp.Model.CommentModel;
import com.cudpast.myeatapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.MyViewHolder> {


    Context context;
    List<CommentModel> commentModelList;

    public MyCommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_comment_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            holder.txt_commnet.setText(commentModelList.get(position).getComment());
            holder.txt_comment_name.setText(commentModelList.get(position).getName());
            holder.ratingBar.setRating(commentModelList.get(position).getRatingValue());

            Long timeStamp = Long.valueOf(commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
            Log.e("timeStamp", "<----" + commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
            if (timeStamp == null) {
                holder.txt_comment_date.setText(0);
                Log.e("timeStamp", "<----" + commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
            } else {
                holder.txt_comment_date.setText(DateUtils.getRelativeTimeSpanString(timeStamp));
                Log.e("timeStamp", "<----" + commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Unbinder unbinder;

        @BindView(R.id.txt_comment_date)
        TextView txt_comment_date;

        @BindView(R.id.txt_commnet)
        TextView txt_commnet;

        @BindView(R.id.txt_comment_name)
        TextView txt_comment_name;

        @BindView(R.id.rating_bar)
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.unbinder = ButterKnife.bind(this, itemView);

        }
    }
}
