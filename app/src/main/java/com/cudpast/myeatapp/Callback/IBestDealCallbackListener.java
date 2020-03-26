package com.cudpast.myeatapp.Callback;

import com.cudpast.myeatapp.Model.BestDealModel;


import java.util.List;

public interface IBestDealCallbackListener {


    void onBestDealLoadSuccess(List<BestDealModel> popularCategoryModels);
    void onBestDealLoadFailed(String message);

}
