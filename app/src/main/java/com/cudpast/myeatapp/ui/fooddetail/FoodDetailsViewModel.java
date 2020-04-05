package com.cudpast.myeatapp.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.CommentModel;
import com.cudpast.myeatapp.Model.FoodModel;

public class FoodDetailsViewModel extends ViewModel {


    private MutableLiveData<FoodModel> mutableLiveDataFood;
    private MutableLiveData<CommentModel> mutableLiveDataComment;



    public FoodDetailsViewModel() {
        mutableLiveDataComment = new MutableLiveData<>();
    }

    public MutableLiveData<FoodModel> getMutableLiveDataFood() {

        if (mutableLiveDataFood == null){
            mutableLiveDataFood = new MutableLiveData<>();
        }
        mutableLiveDataFood.setValue(Common.selectedFood);
        return mutableLiveDataFood;
    }

    public void setCommentModel(CommentModel commentModel){
        if (mutableLiveDataComment !=null){
            mutableLiveDataComment.setValue(commentModel);
        }
    }

    public void setMutableLiveDataFood(MutableLiveData<FoodModel> mutableLiveDataFood) {
        this.mutableLiveDataFood = mutableLiveDataFood;
    }

    public MutableLiveData<CommentModel> getMutableLiveDataComment() {
        return mutableLiveDataComment;
    }

    public void setMutableLiveDataComment(MutableLiveData<CommentModel> mutableLiveDataComment) {
        this.mutableLiveDataComment = mutableLiveDataComment;
    }

    public void setFoodModel(FoodModel foodModel){
        if (mutableLiveDataFood == null){
            mutableLiveDataFood.setValue(foodModel);
        }


    }
}
