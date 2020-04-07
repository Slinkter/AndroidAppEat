package com.cudpast.myeatapp.ui.foodlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.FoodModel;

import java.util.List;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<FoodModel>> mutableLiveDataFoodList;

    public FoodListViewModel() {

    }

    public MutableLiveData<List<FoodModel>> getMutableLiveDataFoodList (){

        if (mutableLiveDataFoodList == null){
            mutableLiveDataFoodList = new MutableLiveData<>();
            mutableLiveDataFoodList.setValue(Common.categorySelected.getFoods());

        }
        return mutableLiveDataFoodList;
    }

}