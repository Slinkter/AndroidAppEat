package com.cudpast.myeatapp.ui.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Database.CartDataSource;
import com.cudpast.myeatapp.Database.CartDatabase;
import com.cudpast.myeatapp.Database.CartItem;
import com.cudpast.myeatapp.Database.LocalCartDataSource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {


    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;


    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context) {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    public MutableLiveData<List<CartItem>> getMutableLiveData() {
        if (mutableLiveDataCartItems == null) {
            mutableLiveDataCartItems = new MutableLiveData<>();
        }

        getAllCartItems();

        return mutableLiveDataCartItems;
    }


    public void onStop() {
        compositeDisposable.clear();
    }


    private void getAllCartItems() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    mutableLiveDataCartItems.setValue(cartItems);
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mutableLiveDataCartItems.setValue(null);
                    }
                })

        );

    }


}
