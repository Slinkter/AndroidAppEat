package com.cudpast.myeatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cudpast.myeatapp.Callback.IRecyclerClickListener;
import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Database.CartDataSource;
import com.cudpast.myeatapp.Database.CartDatabase;
import com.cudpast.myeatapp.Database.CartItem;
import com.cudpast.myeatapp.Database.LocalCartDataSource;
import com.cudpast.myeatapp.EventBus.CounterCartEvent;
import com.cudpast.myeatapp.EventBus.FoodItemClick;
import com.cudpast.myeatapp.Model.FoodModel;
import com.cudpast.myeatapp.R;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder> {

    private Context context;
    private List<FoodModel> foodModelList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;


    public MyFoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyFoodListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyFoodListAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_food_image);
        holder.txt_food_price.setText(new StringBuffer("$").append(foodModelList.get(position).getPrice()));
        holder.txt_food_name.setText(new StringBuffer("").append(foodModelList.get(position).getName()));
        //Event
        holder.setListener((view, pos) -> {
            Common.selectedFood = foodModelList.get(pos);
            Common.selectedFood.setKey(String.valueOf(pos));
            EventBus.getDefault().postSticky(new FoodItemClick(true, foodModelList.get(pos)));
        });
        //
        holder.img_quick_cart.setOnClickListener(v -> {

            CartItem cartItem = new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());

            cartItem.setFoodId(foodModelList.get(position).getId());
            cartItem.setFoodName(foodModelList.get(position).getName());
            cartItem.setFoodImage(foodModelList.get(position).getImage());
            cartItem.setFoodPrice(Double.valueOf(String.valueOf(foodModelList.get(position).getPrice())));
            cartItem.setFoodQuantity(1);
            cartItem.setFoodExtraPrice(0.0);//Because default
            cartItem.setFoodAddon("Default");
            cartItem.setFoodSize("Default");

            //5:16

            compositeDisposable.add(cartDataSource
                    .insertOrReplaceALL(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                Toast.makeText(context, "Add to Cart success", Toast.LENGTH_SHORT).show();
                                //Here we will send a notify to HomeActivity to update counter in cart
                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                            }, throwable -> {
                                Toast.makeText(context, "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    )
            );
        });
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Unbinder unbinder;
        IRecyclerClickListener listener;

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_quick_cart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v, getAdapterPosition());
        }
    }
}
