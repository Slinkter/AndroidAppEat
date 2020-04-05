package com.cudpast.myeatapp.ui.fooddetail;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.CommentModel;
import com.cudpast.myeatapp.Model.FoodModel;
import com.cudpast.myeatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class FoodDetailsFragment extends Fragment {

    private FoodDetailsViewModel mViewModel;
    public Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;

    @BindView(R.id.img_food)
    ImageView img_food;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView(R.id.food_name)
    TextView food_name;
    @BindView(R.id.food_description)
    TextView food_description;
    @BindView(R.id.food_price)
    TextView food_price;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowCommnet;


    @OnClick(R.id.btn_rating)
    void onRatingButtonClick() {
        showDialogRating();
    }

    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());

        builder.setTitle("Rating Food ");
        builder.setMessage("Please fill information");
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating, null);
        RatingBar ratingBar = itemView.findViewById(R.id.rating_bar);
        EditText edt_comment = itemView.findViewById(R.id.edt_comment);
        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommentModel commentModel = new CommentModel();
                commentModel.setName(Common.currentUser.getName());
                commentModel.setUid(Common.currentUser.getUid());
                commentModel.setComment(edt_comment.getText().toString());
                commentModel.setRatingValue(ratingBar.getRating());
                Map<String, Object> serverTimeStamp = new HashMap<>();
                commentModel.setCommentTimeStamp(serverTimeStamp);


                mViewModel.setCommentModel(commentModel);


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public static FoodDetailsFragment newInstance() {
        return new FoodDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_food_detail, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        mViewModel = ViewModelProviders.of(this).get(FoodDetailsViewModel.class);
        mViewModel.getMutableLiveDataFood().observe(this, new Observer<FoodModel>() {
            @Override
            public void onChanged(FoodModel foodModel) {
                displayInfo(foodModel);
            }
        });

        mViewModel.getMutableLiveDataComment().observe(this, commentModel -> {
            submitRatingToFirabase(commentModel);
        });

        return root;
    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();


    }

    private void submitRatingToFirabase(CommentModel commentModel) {
        waitingDialog.show();
        FirebaseDatabase
                .getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.selectedFood.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addRatingToFood(commentModel.getRatingValue());
                        }
                        waitingDialog.dismiss();
                    }
                });

    }

    private void addRatingToFood(float ratingValue) {
        FirebaseDatabase
                .getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .child("foods")
                .child(Common.selectedFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FoodModel foodModel = dataSnapshot.getValue(FoodModel.class);
                            foodModel.setKey(Common.selectedFood.getKey());


                            if (foodModel.getRatingValue() == null) {
                                foodModel.setRatingValue(0d);
                            }

                            if (foodModel.getRatingCount() == null) {
                                foodModel.setRatingCount(0l);
                            }


                            double sumRating = foodModel.getRatingValue() + ratingValue;
                            long ratingCount = foodModel.getRatingCount() + 1;
                            double result = sumRating / ratingCount;

                            Map<String, Object> updateDate = new HashMap<>();
                            updateDate.put("ratingValue", result);
                            updateDate.put("ratingCount", ratingCount);

                            //Update data in variable
                            foodModel.setRatingValue(result);
                            foodModel.setRatingCount(ratingCount);


                            dataSnapshot
                                    .getRef()
                                    .updateChildren(updateDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            waitingDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Thank you", Toast.LENGTH_SHORT).show();
                                                Common.selectedFood = foodModel;
                                                mViewModel.setFoodModel(foodModel);/// call refresh
                                            }
                                        }
                                    });


                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        waitingDialog.dismiss();
                        Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayInfo(FoodModel foodModel) {
        Glide.with(getContext()).load(foodModel.getImage()).into(img_food);
        food_name.setText(new StringBuffer(foodModel.getName()));
        food_description.setText(new StringBuilder(foodModel.getDescription()));
        food_price.setText(new StringBuilder(foodModel.getPrice().toString()));

        if (foodModel.getRatingCount() != null){
            ratingBar.setRating(foodModel.getRatingCount().floatValue());
        }



        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Common.selectedFood.getName());

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FoodDetailsViewModel.class);

    }

}
