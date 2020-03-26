package com.cudpast.myeatapp.Callback;

import com.cudpast.myeatapp.Model.PopularCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {

    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);

}
