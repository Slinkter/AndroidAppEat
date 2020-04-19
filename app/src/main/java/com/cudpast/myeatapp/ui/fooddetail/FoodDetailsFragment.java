package com.cudpast.myeatapp.ui.fooddetail;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Database.CartDataSource;
import com.cudpast.myeatapp.Database.CartDatabase;
import com.cudpast.myeatapp.Database.CartItem;
import com.cudpast.myeatapp.Database.LocalCartDataSource;
import com.cudpast.myeatapp.EventBus.CounterCartEvent;
import com.cudpast.myeatapp.Model.AddonModel;
import com.cudpast.myeatapp.Model.CommentModel;
import com.cudpast.myeatapp.Model.FoodModel;
import com.cudpast.myeatapp.Model.SizeModel;
import com.cudpast.myeatapp.R;
import com.cudpast.myeatapp.ui.comments.CommentFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FoodDetailsFragment extends Fragment implements TextWatcher {

    private FoodDetailsViewModel foodDetailViewModel;
    public Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;
    private BottomSheetDialog addonBottomSheetDialog;
    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    // View need inflate
    ChipGroup chip_group_addon;
    EditText edt_search;

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
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;
    @BindView(R.id.img_add_addon)
    ImageView img_add_addon;
    @BindView(R.id.chip_group_user_selected)
    ChipGroup chip_group_user_selected_addon;

    @OnClick(R.id.img_add_addon)
    void onAddonClick() {
        if (Common.selectedFood.getAddon() != null) {
            displayAddonList();
            addonBottomSheetDialog.show();
        }
    }

    @OnClick(R.id.btnCart)
    void onCartItemAdd() {
        CartItem cartItem = new CartItem();
        cartItem.setUid(Common.currentUser.getUid());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setFoodId(Common.selectedFood.getId());
        cartItem.setFoodName(Common.selectedFood.getName());
        cartItem.setFoodImage(Common.selectedFood.getImage());
        cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectedFood.getPrice())));
        cartItem.setFoodQuantity(Integer.valueOf(numberButton.getNumber()));
        cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectedFood.getUserSelectedSize(), Common.selectedFood.getUserSelectedAddon()));//Because default
        if (Common.selectedFood.getUserSelectedAddon() != null) {
            cartItem.setFoodAddon(new Gson().toJson(Common.selectedFood.getUserSelectedAddon()));
        } else {
            cartItem.setFoodAddon("Default");
        }

        if (Common.selectedFood.getUserSelectedSize() != null) {
            cartItem.setFoodSize(new Gson().toJson(Common.selectedFood.getUserSelectedSize()));
        } else {
            cartItem.setFoodSize("Default");
        }

        cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUid(),
                cartItem.getFoodId(),
                cartItem.getFoodSize(),
                cartItem.getFoodAddon())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CartItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(CartItem cartItemfromDB) {
                        if (cartItemfromDB.equals(cartItem)) {
                            //al ready in datbase , just update
                            cartItemfromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                            cartItemfromDB.setFoodAddon(cartItem.getFoodAddon());
                            cartItemfromDB.setFoodSize(cartItem.getFoodSize());
                            cartItemfromDB.setFoodQuantity(cartItemfromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                            cartDataSource.updateCartItems(cartItemfromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            Toast.makeText(getContext(), "Update cart Success", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            // item not available in cart before , insert new
                            compositeDisposable.add(cartDataSource.insertOrReplaceALL(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to Cart success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage().contains("empty")) {
                            //Defaut , if cart is empty this code will be fired
                            compositeDisposable.add(cartDataSource.insertOrReplaceALL(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to Cart success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        } else {
                            Toast.makeText(getContext(), "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    @OnClick(R.id.btn_rating)
    void onRatingButtonClick() {
        showDialogRating();
    }

    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick() {
        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(), "CommentFragment");

    }

    private void displayAddonList() {

        if (Common.selectedFood.getAddon().size() > 0) {
            chip_group_addon.clearCheck();
            chip_group_addon.removeAllViews();

            edt_search.addTextChangedListener(this);
            //Add all view

            for (AddonModel addonModel : Common.selectedFood.getAddon()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$").append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (Common.selectedFood.getUserSelectedAddon() == null) {
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        }
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);
            }
        }
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

        builder.setPositiveButton("ok", (dialog, which) -> {
            CommentModel commentModel = new CommentModel();
            commentModel.setName(Common.currentUser.getName());
            commentModel.setUid(Common.currentUser.getUid());
            commentModel.setComment(edt_comment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String, Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);
            foodDetailViewModel.setCommentModel(commentModel);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static FoodDetailsFragment newInstance() {
        return new FoodDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        foodDetailViewModel = ViewModelProviders.of(this).get(FoodDetailsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_detail, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        foodDetailViewModel.getMutableLiveDataFood().observe(this, foodModel -> displayInfo(foodModel));
        foodDetailViewModel.getMutableLiveDataComment().observe(this, commentModel -> {
            submitRatingToFirabase(commentModel);
        });
        //
        return root;
    }

    private void initViews() {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        //
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display, null);
        chip_group_addon = (ChipGroup) layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search = (EditText) layout_addon_display.findViewById(R.id.edt_search);
        //
        addonBottomSheetDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);
        addonBottomSheetDialog.setContentView(layout_addon_display);

        addonBottomSheetDialog.setOnDismissListener(dialog -> {
            displayUserSelectAddon();
            calculateToTotalPrice();
        });

    }

    private void displayUserSelectAddon() {
        if (Common.selectedFood.getUserSelectedAddon() != null &&
                Common.selectedFood.getUserSelectedAddon().size() > 0) {

            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel : Common.selectedFood.getUserSelectedAddon()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(view -> {
                    chip_group_user_selected_addon.removeView(view);
                    Common.selectedFood.getUserSelectedAddon().remove(addonModel);
                    calculateToTotalPrice();
                });

                chip_group_user_selected_addon.addView(chip);

            }
        } else if (Common.selectedFood.getUserSelectedAddon().size() == 0) {
            chip_group_user_selected_addon.removeAllViews();
        }
    }


    private void submitRatingToFirabase(CommentModel commentModel) {
        waitingDialog.show();
        FirebaseDatabase
                .getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.selectedFood.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addRatingToFood(commentModel.getRatingValue());
                    }
                    waitingDialog.dismiss();
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
                                                foodDetailViewModel.setFoodModel(foodModel);/// call refresh
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

        if (foodModel.getRatingCount() != null) {
            ratingBar.setRating(foodModel.getRatingCount().floatValue());
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Common.selectedFood.getName());

        for (SizeModel sizeModel : Common.selectedFood.getSize()) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Common.selectedFood.setUserSelectedSize(sizeModel);
                        calculateToTotalPrice();
                    }
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            radioButton.setLayoutParams(params);
            radioButton.setText(sizeModel.getName());
            radioButton.setTag(sizeModel.getPrice());
            rdi_group_size.addView(radioButton);
        }

        if (rdi_group_size.getChildCount() > 0) {
            RadioButton radioButton = (RadioButton) rdi_group_size.getChildAt(0);
            radioButton.setChecked(true);
        }
        calculateToTotalPrice();
    }

    private void calculateToTotalPrice() {
        double totalPrice = Double.parseDouble(Common.selectedFood.getPrice().toString()), displayPrice = 0.0;
        // Addon
        if (Common.selectedFood.getUserSelectedAddon() != null && Common.selectedFood.getUserSelectedAddon().size() > 0) {
            for (AddonModel addonModel : Common.selectedFood.getUserSelectedAddon()) {
                totalPrice += Double.parseDouble(addonModel.getPrice().toString());
            }
        }
        //size
        totalPrice += Double.parseDouble(Common.selectedFood.getUserSelectedSize().getPrice().toString());
        displayPrice = totalPrice * (Integer.parseInt(numberButton.getNumber()));
        displayPrice = Math.round(displayPrice * 100.0 / 100.0);
        food_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        foodDetailViewModel = ViewModelProviders.of(this).get(FoodDetailsViewModel.class);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Nothing
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();

        for (AddonModel addonModel : Common.selectedFood.getAddon()) {
            if (addonModel.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {

                        if (Common.selectedFood.getUserSelectedAddon() == null) {
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        }
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);

            }

        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Nothing
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
