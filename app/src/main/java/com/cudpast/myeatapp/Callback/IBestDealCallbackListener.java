package com.cudpast.myeatapp.Callback;

import com.cudpast.myeatapp.Model.BestDealModel;


import java.util.List;

public interface IBestDealCallbackListener {

    void onBestDealLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);

}
