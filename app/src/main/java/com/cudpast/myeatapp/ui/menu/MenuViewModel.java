package com.cudpast.myeatapp.ui.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cudpast.myeatapp.Callback.ICategoryCallbackListener;
import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends ViewModel implements ICategoryCallbackListener {

    //Variables
    private ICategoryCallbackListener categoryCallbackListener;
    private MutableLiveData<List<CategoryModel>> categoryListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();

    //Constructor
    public MenuViewModel() {
        categoryCallbackListener = this;
    }
    //metodos sobreecritos

    @Override
    public void onCategoryLoadSucess(List<CategoryModel> categoryModelsList) {
        categoryListMutable.setValue(categoryModelsList);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        messageError.setValue(message);
    }

    //metodo de clase
    public MutableLiveData<List<CategoryModel>> getCategoryListMutable() {
        if (categoryListMutable == null) {
            categoryListMutable = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadCategories();
        }
        return categoryListMutable;

    }


    public MutableLiveData<String> getMessageError() {
        return messageError;
    }
    //metodo auxiliar - datos
    private void loadCategories() {
        List<CategoryModel> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF);
        categoryRef.orderByKey();
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) { //dataSnapshot.getChildren  <-- es una lista
                    CategoryModel categoryModel = x.getValue(CategoryModel.class);
                    categoryModel.setMenu_id(x.getKey());
                    tempList.add(categoryModel);
                }

                categoryCallbackListener.onCategoryLoadSucess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                categoryCallbackListener.onCategoryLoadFailed(databaseError.getMessage());
            }
        });
    }



}