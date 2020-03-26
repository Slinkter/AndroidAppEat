package com.cudpast.myeatapp.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cudpast.myeatapp.Callback.IBestDealCallbackListener;
import com.cudpast.myeatapp.Callback.IPopularCallbackListener;
import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.BestDealModel;
import com.cudpast.myeatapp.Model.PopularCategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements IPopularCallbackListener, IBestDealCallbackListener {

    private MutableLiveData<List<PopularCategoryModel>> popularList;
    private MutableLiveData<List<BestDealModel>> bestDealList;
    private MutableLiveData<String> messageError;
    private IPopularCallbackListener popularCallbackListener;
    private IBestDealCallbackListener bestDealCallbackListener;


    public HomeViewModel() {
        popularCallbackListener = this;
        bestDealCallbackListener = this;
    }

    public MutableLiveData<List<BestDealModel>> getBestDealList() {

        if (bestDealList == null){
            bestDealList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBestDealsList();
        }


        return bestDealList;
    }

    private void loadBestDealsList() {
        List<BestDealModel> tempList = new ArrayList<>();
        DatabaseReference bestDealref = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS_REF);

        bestDealref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    BestDealModel model = itemSnapshot.getValue(BestDealModel.class);
                    tempList.add(model);
                }
                bestDealCallbackListener.onBestDealLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bestDealCallbackListener.onBestDealLoadFailed(databaseError.getMessage());
            }
        });

    }

    @Override
    public void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels) {
        popularList.setValue(popularCategoryModels);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }

    public MutableLiveData<List<PopularCategoryModel>> getPopularList() {

        if (popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList();
        }
        return popularList;
    }

    private void loadPopularList() {

        List<PopularCategoryModel> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORY_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    PopularCategoryModel model = itemSnapshot.getValue(PopularCategoryModel.class);
                    tempList.add(model);
                }
                popularCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallbackListener.onPopularLoadFailed(databaseError.getMessage());
            }
        });

    }

    public void setPopularList(MutableLiveData<List<PopularCategoryModel>> popularList) {
        this.popularList = popularList;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public void setMessageError(MutableLiveData<String> messageError) {
        this.messageError = messageError;
    }

    public IPopularCallbackListener getPopularCallbackListener() {
        return popularCallbackListener;
    }

    public void setPopularCallbackListener(IPopularCallbackListener popularCallbackListener) {
        this.popularCallbackListener = popularCallbackListener;
    }

    @Override
    public void onBestDealLoadSuccess(List<BestDealModel> bestDealModel) {
        bestDealList.setValue(bestDealModel);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
        messageError.setValue(message);
    }
}