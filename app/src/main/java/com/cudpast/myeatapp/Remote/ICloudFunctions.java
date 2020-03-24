package com.cudpast.myeatapp.Remote;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ICloudFunctions {


    @GET("")
    Observable<ResponseBody> getCustomToker(@Query("access_token") String accessToken);



}
