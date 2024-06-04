package com.kuro.yemaadmin.views.fragments.home;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kuro.androidutils.logging.Logger;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.adapters.HouseAdapter;
import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.viewModels.HomesContentViewModel;
import com.kuro.yemaadmin.viewModels.HomesContentViewModelFactory;

/**
 * HomesContentFragment class
 * Fragment that will contain the result (houses) of each tab item
 */
public class HomeContentFragment extends Fragment {

    private LinearLayout mNavLayout;
    private HouseType mHouseType;
    private HomesContentViewModel mHomesContentViewModel;
    private NavController mNavController;
    private TextView mHomesNotAvailable;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mManagedCountry;

    public HomeContentFragment() {
    }

    public HomeContentFragment(HouseType houseType, NavController navController, LinearLayout navLayout, String managedCountry) {
        mHouseType = houseType;
        mNavController = navController;
        mNavLayout = navLayout;
        this.mManagedCountry = managedCountry;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the data of the corresponding tab
        HomesContentViewModelFactory mViewModelFactory = new HomesContentViewModelFactory(requireActivity().getApplication(), mHouseType, mManagedCountry);
        HomesContentViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mHomesContentViewModel = new ViewModelProvider(this, mViewModelFactory).get(HomesContentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_contents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = view.findViewById(R.id.house_home_recyclerview);
        mHomesNotAvailable = view.findViewById(R.id.no_homes_available);
        mProgressBar = view.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = view.findViewById(R.id.house_home_swipe_refresh_layout);

        HouseAdapter houseAdapter = new HouseAdapter(mNavController, requireContext());
        mRecyclerView.setAdapter(houseAdapter);

        resetFragment(houseAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // make  another request to firestore to get the (updated) data
                mHomesContentViewModel.getHouses(mHouseType, mManagedCountry);
                resetFragment(houseAdapter);
                new Handler().postDelayed(() -> mSwipeRefreshLayout.setRefreshing(false), 500);
            }
        });
    }

    private void resetFragment(HouseAdapter houseAdapter) {
        mProgressBar.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mHomesNotAvailable.setVisibility(View.GONE);

        mHomesContentViewModel.gethouseMutableLiveData().observe(getViewLifecycleOwner(), houses -> {
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

            if (houses.isEmpty()) {
                mHomesNotAvailable.setVisibility(View.VISIBLE);
            } else {
                mHomesNotAvailable.setVisibility(View.GONE);
            }
            houseAdapter.setListOfHouses(houses);
        });
    }
}