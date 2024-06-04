package com.kuro.yemaadmin.views.fragments.houses;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.adapters.ImageSliderAdapter;
import com.kuro.yemaadmin.data.model.House;

public class HouseCreationReviewFragment extends Fragment {
    private TextView mHouseItemImageIndex;
    private Uri[] mListOfImagesUri;
    private House mHouse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View reviewView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(reviewView, savedInstanceState);
        NavController navController = Navigation.findNavController(reviewView);

        TextView mHouseItemTitle = reviewView.findViewById(R.id.review_house_title);
        TextView mHouseItemPrice = reviewView.findViewById(R.id.review_house_price);
        TextView mHouseItemPriceCurrency = reviewView.findViewById(R.id.review_house_price_currency);
        TextView mHouseItemImageTotal = reviewView.findViewById(R.id.review_house_image_total);
        TextView mHouseDescription = reviewView.findViewById(R.id.review_description_content);
        TextView mHouseRequirements = reviewView.findViewById(R.id.review_requirement_content);
        TextView mHouseItemRentalTerm = reviewView.findViewById(R.id.review_house_rental_term);
        ViewPager2 mViewPager = reviewView.findViewById(R.id.review_house_view_pager);
        ImageButton backButton = reviewView.findViewById(R.id.back_to_previous_fragment);
        TextView houseType = reviewView.findViewById(R.id.house_type);

        Button mAddHouseBtn = reviewView.findViewById(R.id.add_house_btn);
        mHouseItemImageIndex = reviewView.findViewById(R.id.review_house_image_index);

        mHouse = HouseCreationReviewFragmentArgs.fromBundle(requireArguments()).getHouse();
        mListOfImagesUri = HouseCreationReviewFragmentArgs.fromBundle(requireArguments()).getListOfImages();

        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(requireContext(), mListOfImagesUri);
        mViewPager.setAdapter(imageSliderAdapter);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHouseItemImageIndex.setText(String.valueOf(position + 1));
            }
        });

        mHouseItemImageTotal.setText(String.valueOf(mListOfImagesUri.length));

        String title = mHouse.getTitle();
        if (!mHouse.getLocation().isEmpty()) {
            title += " " + getString(R.string.at) + " " + mHouse.getLocation();
        }
        mHouseItemTitle.setText(title);

        mHouseItemPrice.setText(String.valueOf(mHouse.getPrice()));
        mHouseItemPriceCurrency.setText(mHouse.getPriceCurrency().name());
        String rentalTerm = mHouseItemRentalTerm.getText().toString();
        mHouseItemRentalTerm.setText(String.format(rentalTerm, mHouse.getRentalTerm().name().toLowerCase()));

        mHouseDescription.setText(Html.fromHtml(mHouse.getDescription(), Html.FROM_HTML_MODE_LEGACY).toString());
        mHouseRequirements.setText(mHouse.getRequirements());

        houseType.setText(mHouse.getHouseType().type);

        mAddHouseBtn.setOnClickListener(v -> {
            HouseCreationReviewFragmentDirections.ActionHouseReviewFragmentToProcessingFragment action;
            action = HouseCreationReviewFragmentDirections.actionHouseReviewFragmentToProcessingFragment(mHouse, mListOfImagesUri);
            NavigationUtils.navigateSafe(navController, action);

        });
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressedHandler(navController);
            }
        });
        backButton.setOnClickListener(v -> {
            onBackPressedHandler(navController);
        });
    }

    private void onBackPressedHandler(NavController navController) {
        HouseCreationReviewFragmentDirections.ActionHouseReviewFragmentToAddHouseFragment action;
        action = HouseCreationReviewFragmentDirections.actionHouseReviewFragmentToAddHouseFragment();
        action.setListOfImages(mListOfImagesUri);
        action.setHouse(mHouse);
        NavigationUtils.navigateSafe(navController, action);
    }
}