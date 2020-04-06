package com.cudpast.myeatapp.Callback;

import com.cudpast.myeatapp.Model.CommentModel;

import java.util.List;

public interface ICommentCallbackListener {

    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String meessage);

}
