package com.cudpast.myeatapp.Callback;


import com.cudpast.myeatapp.Model.CategoryModel;

import java.util.List;

public interface ICategoryCallbackListener {

    void onCategoryLoadSucess(List<CategoryModel> bestDealModels);
    void onCategoryLoadFailed(String message);

}
