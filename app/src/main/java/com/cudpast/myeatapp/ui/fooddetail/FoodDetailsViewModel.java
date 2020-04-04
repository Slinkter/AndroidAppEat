package com.cudpast.myeatapp.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.FoodModel;

public class FoodDetailsViewModel extends ViewModel {


    private MutableLiveData<FoodModel> mutableLiveDataFood;

    public FoodDetailsViewModel() {
    }

    public MutableLiveData<FoodModel> getMutableLiveDataFood() {

        if (mutableLiveDataFood == null){
            mutableLiveDataFood = new MutableLiveData<>();
        }
        mutableLiveDataFood.setValue(Common.selectedFood);



        return mutableLiveDataFood;
    }
}
