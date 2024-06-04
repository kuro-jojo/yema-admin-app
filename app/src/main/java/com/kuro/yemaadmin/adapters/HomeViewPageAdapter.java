package com.kuro.yemaadmin.adapters;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.views.fragments.home.HomeContentFragment;

import java.util.ArrayList;

public class HomeViewPageAdapter extends FragmentStateAdapter {

    private final ArrayList<HouseType> mHouseType;
    private final String mManagedCountry;
    private final NavController mNavController;
    private final LinearLayout mNavLayout;

    public HomeViewPageAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<HouseType> tabType, String mManagedCountry, NavController navController, LinearLayout navLayout) {
        super(fragmentActivity);

        mHouseType = tabType;
        this.mManagedCountry = mManagedCountry;
        mNavController = navController;
        mNavLayout = navLayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new HomeContentFragment(mHouseType.get(position), mNavController, mNavLayout, mManagedCountry);
    }

    @Override
    public int getItemCount() {
        return mHouseType.size();
    }


}
