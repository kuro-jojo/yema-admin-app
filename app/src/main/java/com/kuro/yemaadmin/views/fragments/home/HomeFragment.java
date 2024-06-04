package com.kuro.yemaadmin.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.adapters.HomeViewPageAdapter;
import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.viewModels.UserViewModel;
import com.kuro.yemaadmin.views.MainActivity;
import com.kuro.yemaadmin.views.MainActivityInterface;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MainActivityInterface {
    private static final ArrayList<String> TAB_TEXTS = new ArrayList<>();
    private static final ArrayList<HouseType> TAB_FRAGMENTS = new ArrayList<>();
    private ViewPager2 mHomeViewPager;
    private TabLayout mTabLayout;
    private NavController mNavController;
    private MainActivity mMainActivity;
    private UserViewModel mUserViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mUserViewModel = new ViewModelProvider(this, ViewModelProvider.
                AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // adding the tab items title and TabItem element once
        if (TAB_FRAGMENTS.isEmpty() && TAB_TEXTS.isEmpty()) {
            TAB_FRAGMENTS.add(null);
            TAB_TEXTS.add("All houses");
            for (HouseType houseType : HouseType.values()) {
                TAB_FRAGMENTS.add(houseType);
                TAB_TEXTS.add(houseType.type);
            }
        }
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View homeView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(homeView, savedInstanceState);
        mMainActivity = (MainActivity) requireActivity();

        mMainActivity.setNavHomeClickListener(onNavHomeClicked());
        mMainActivity.setNavAddClickListener(onNavAddClicked());
        mMainActivity.setNavProfileClickListener(onNavProfileClicked());
        // set the view as active tab
        mMainActivity.setActiveNavigationItem(MainActivity.NavigationItems.HOME);

        mNavController = Navigation.findNavController(homeView);

        mTabLayout = homeView.findViewById(R.id.tab_layout);
        mHomeViewPager = homeView.findViewById(R.id.fragment_home_viewpager);
        CardView mLogoToHome = homeView.findViewById(R.id.logo_to_profile);

        mLogoToHome.setOnClickListener(onNavProfileClicked());
        CountryCodePicker ccpCountry = homeView.findViewById(R.id.managed_country);
        mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                ccpCountry.setCountryForNameCode(user.getManagedCountryCode());
                mMainActivity.setManagedCountryCode(user.getManagedCountryCode());
                mMainActivity.setManagedCountry(ccpCountry.getSelectedCountryName().toLowerCase());
                ccpCountry.setVisibility(View.VISIBLE);
                setupViewPager(homeView);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!mNavController.popBackStack()) {
                    requireActivity().finish();
                }
            }
        });
    }


    private void setupViewPager(View view) {
        mHomeViewPager.setAdapter(new HomeViewPageAdapter(requireActivity(), TAB_FRAGMENTS, mMainActivity.getManagedCountry(), Navigation.findNavController(view), mMainActivity.getNavLayout()));
        mHomeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        // Adding the tab items and attaching the viewPager to the tabLayout
        new TabLayoutMediator(mTabLayout, mHomeViewPager, (tab, position) -> tab.setText(TAB_TEXTS.get(position))).attach();
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
        };
    }

    @Override
    public View.OnClickListener onNavAddClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, HomeFragmentDirections.actionHomeFragmentToAddHouseFragment());

    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, HomeFragmentDirections.actionHomeFragmentToProfileFragment());
    }
}
