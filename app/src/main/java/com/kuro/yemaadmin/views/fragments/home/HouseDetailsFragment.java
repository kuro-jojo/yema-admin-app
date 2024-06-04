package com.kuro.yemaadmin.views.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.adapters.ImageSliderAdapter;
import com.kuro.yemaadmin.data.model.House;
import com.kuro.yemaadmin.viewModels.HouseViewModel;
import com.kuro.yemaadmin.views.MainActivity;
import com.kuro.yemaadmin.views.MainActivityInterface;


public class HouseDetailsFragment extends Fragment implements MainActivityInterface {
    private HouseViewModel mHouseViewModel;
    private String mHouseId;

    private TextView mHouseItemTitle, mHouseItemPrice, mHouseItemImageIndex;
    private TextView mHouseItemImageTotal, mHouseDescription, mHouseRequirements, mHouseItemPriceCurrency, mHouseItemRentalTerm, mHouseType;
    private ScrollView mScrollView;
    private ViewPager2 mViewPager;
    private NavController mNavController;

    public HouseDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HouseViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mHouseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(HouseViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_house_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View detailView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(detailView, savedInstanceState);

        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.VISIBLE);
        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavAddClickListener(onNavAddClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.HOME);
        mHouseId = HouseDetailsFragmentArgs.fromBundle(requireArguments()).getHouseId();
        mNavController = Navigation.findNavController(detailView);

        mScrollView = detailView.findViewById(R.id.scrollView);
        mScrollView.setVisibility(View.GONE);

        ImageButton mBackHome = detailView.findViewById(R.id.back_to_previous_fragment);
        mViewPager = detailView.findViewById(R.id.detail_house_view_pager);
        mHouseItemTitle = detailView.findViewById(R.id.detail_house_title);
        mHouseItemPrice = detailView.findViewById(R.id.detail_house_price);
        mHouseItemImageIndex = detailView.findViewById(R.id.detail_house_image_index);
        mHouseItemImageTotal = detailView.findViewById(R.id.detail_house_image_total);
        mHouseDescription = detailView.findViewById(R.id.detail_description_content);
        mHouseRequirements = detailView.findViewById(R.id.detail_requirement_content);
        mHouseItemPriceCurrency = detailView.findViewById(R.id.detail_house_price_currency);
        mHouseItemRentalTerm = detailView.findViewById(R.id.detail_house_rental_term);
        mHouseItemImageIndex = detailView.findViewById(R.id.detail_house_image_index);

        mHouseType = detailView.findViewById(R.id.house_type);

        mBackHome.setOnClickListener(view -> mNavController.popBackStack(R.id.houseDetailsFragment, true));

        updateView(detailView);
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!mNavController.popBackStack()) {
                    NavigationUtils.navigateSafe(mNavController, HouseDetailsFragmentDirections.actionHouseDetailsFragmentToHomeFragment());
                }
            }
        });
    }


    private void updateView(View view) {
        mHouseViewModel.getHouse(mHouseId);
        mHouseViewModel.getHouseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<House>() {
            @Override
            public void onChanged(House house) {
                if (house != null) {
                    ProgressBar mProgressBar = view.findViewById(R.id.progressBar);
                    mProgressBar.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);

                    String title = house.getTitle();
                    if (!house.getLocation().isEmpty()) {
                        title += " at " + house.getLocation();
                    }
                    mHouseItemTitle.setText(title);
                    // set images
                    ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(requireContext(), house.getImages());
                    mViewPager.setAdapter(imageSliderAdapter);
                    mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            mHouseItemImageIndex.setText(String.valueOf(position + 1));
                        }
                    });
                    mHouseItemPrice.setText(String.valueOf(house.getPrice()));
                    mHouseItemImageTotal.setText(String.valueOf(house.getNumberOfImages()));
                    mHouseDescription.setText(house.getDescription());
                    mHouseRequirements.setText(house.getRequirements());
                    mHouseItemPriceCurrency.setText(house.getPriceCurrency().name());
                    String rentalTerm = mHouseItemRentalTerm.getText().toString();
                    mHouseItemRentalTerm.setText(String.format(rentalTerm, house.getRentalTerm().name().toLowerCase()));
                    mHouseType.setText(house.getHouseType().type);
                }
            }
        });
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavAddClicked() {
        return view -> {
            NavigationUtils.navigateSafe(mNavController, HouseDetailsFragmentDirections.actionHouseDetailsFragmentToAddHouseFragment());
        };

    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
//            NavDirections navDirections;
//            if (mFirebaseAuth.getCurrentUser() != null) {
//                navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileFragment();
//            } else {
//                navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileGuestFragment();
//            }
//            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }
}
