package com.cudpast.myeatapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.cudpast.myeatapp.Adapter.MyBestDealsAdapter;
import com.cudpast.myeatapp.Adapter.MyPopularCategoriesAdapter;
import com.cudpast.myeatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    public HomeViewModel homeViewModel;
    public LayoutAnimationController layoutAnimationController;
    public Unbinder unbinder;
    //
    @BindView(R.id.recycler_popular)
    RecyclerView recyclerView_popular;
    @BindView(R.id.viewpager)
    LoopingViewPager loopViewPager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        initRecyclerView();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // 1
        homeViewModel.getPopularList().observe(this, popularCategoryModels -> {
            MyPopularCategoriesAdapter adapter = new MyPopularCategoriesAdapter(getContext(), popularCategoryModels);
            recyclerView_popular.setAdapter(adapter);
            recyclerView_popular.setLayoutAnimation(layoutAnimationController);
        });
        // 2
        homeViewModel.getBestDealList().observe(this, bestDealModels -> {
            MyBestDealsAdapter adapter = new MyBestDealsAdapter(getContext(), bestDealModels, true);
            loopViewPager.setAdapter(adapter);
        });
        return root;
    }

    private void initRecyclerView() {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        recyclerView_popular.setHasFixedSize(true);
        recyclerView_popular.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        loopViewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        loopViewPager.pauseAutoScroll();
        super.onPause();
    }
}
