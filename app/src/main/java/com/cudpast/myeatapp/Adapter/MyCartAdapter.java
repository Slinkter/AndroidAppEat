package com.cudpast.myeatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.cudpast.myeatapp.Database.CartItem;
import com.cudpast.myeatapp.EventBus.UpdateItemInCart;
import com.cudpast.myeatapp.R;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyviewHolder> {


    Context context;
    List<CartItem> cartItemList;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyCartAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.latouy_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.MyviewHolder holder, int position) {



        Glide.with(context).load(cartItemList.get(position).getFoodImage()).into(holder.img_cart);
        holder.txt_food_name.setText(new StringBuilder(cartItemList.get(position).getFoodName()));
        holder.txt_food_price.setText(new StringBuilder("").append(cartItemList.get(position).getFoodPrice() + cartItemList.get(position).getFoodExtraPrice()));
        //Event
        holder.numberButton.setOnValueChangeListener((view, oldValue, newValue) -> {
            //When user click this button , we will update datebase
            cartItemList.get(position).setFoodQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {

        private Unbinder unbinder;
        @BindView(R.id.img_cart)
        ImageView img_cart;

        @BindView(R.id.txt_food_price)
        TextView txt_food_price;

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;

        @BindView(R.id.number_button)
        ElegantNumberButton numberButton;


        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
