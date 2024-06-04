package com.kuro.yemaadmin.views.fragments.houses;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.androidutils.ui.SoftKeyboard;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.adapters.ImageAdapter;
import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.data.enums.ListingType;
import com.kuro.yemaadmin.data.enums.PriceCurrency;
import com.kuro.yemaadmin.data.enums.RentalTerm;
import com.kuro.yemaadmin.data.model.House;
import com.kuro.yemaadmin.utils.HouseDescriptionGenerator;
import com.kuro.yemaadmin.views.MainActivity;
import com.kuro.yemaadmin.views.MainActivityInterface;
import com.kuro.yemaadmin.views.dialogs.DefaultConfirmationDialogFragment;
import com.kuro.yemaadmin.views.dialogs.DefaultEditDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

public class AddHouseFragment extends Fragment implements MainActivityInterface {
    private static final int MAX_SELECTED_IMAGES = 15;
    private int mRemainImages = MAX_SELECTED_IMAGES;
    private TextInputEditText mTitleEditText, mLocationEditText, mPriceEditText, mNbBedroomsEditText,
            mNbBathroomsEditText, mNbParkingSpotEditText, mLivingAreaEditText, mDescriptionEditText, mRequirementsEditText;
    private Spinner mListingTypeSpinner, mHouseTypeSpinner, mRentalTermSpinner;
    private TextView mImageTextView, mAddImageError;
    private ImageButton mAddImages;
    private RecyclerView mListImagesRecyclerView;
    private Button mReviewHouseBtn;
    private ActivityResultLauncher<Intent> mGetContent;
    private ImageAdapter mImageAdapter;
    private ImageButton mClearDescription;
    private MainActivity mainActivity;
    private NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Move layouts up when soft keyboard is shown
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        if (intent != null && intent.getClipData() != null && mListImagesRecyclerView != null) {
                            if (mAddImageError != null) {
                                mAddImageError.setVisibility(View.GONE);
                            }
// TODO : size of image
                            int selectedImagesCount = intent.getClipData().getItemCount();
                            if (selectedImagesCount <= mRemainImages) {
                                mRemainImages -= selectedImagesCount;
                                for (int i = 0; i < Objects.requireNonNull(intent.getClipData()).getItemCount(); i++) {
                                    ClipData.Item item = intent.getClipData().getItemAt(i);
                                    mImageAdapter.addImageUri(item.getUri());
                                }
                            } else {
                                Toast.makeText(requireActivity(), String.format(getString(R.string.you_can_select_up_to), MAX_SELECTED_IMAGES), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_house, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View addView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(addView, savedInstanceState);
        mNavController = Navigation.findNavController(addView);
        mainActivity = (MainActivity) requireActivity();

        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavAddClickListener(onNavAddClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());
        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.ADD);

        TextInputLayout titleLayout = addView.findViewById(R.id.title_layout);
        mTitleEditText = addView.findViewById(R.id.title_edit);
        setDefaultErrorMessage(titleLayout);
        addChangeListener(titleLayout, mTitleEditText);

        TextInputLayout locationLayout = addView.findViewById(R.id.location_layout);
        mLocationEditText = addView.findViewById(R.id.address_edit);
        setDefaultErrorMessage(locationLayout);
        addChangeListener(locationLayout, mLocationEditText);

        TextInputLayout priceLayout = addView.findViewById(R.id.price_layout);
        mPriceEditText = addView.findViewById(R.id.price_edit);
        setDefaultErrorMessage(priceLayout);
        addChangeListener(priceLayout, mPriceEditText);

        TextInputLayout descriptionLayout = addView.findViewById(R.id.description_layout);
        mDescriptionEditText = addView.findViewById(R.id.description_edit);
        setDefaultErrorMessage(descriptionLayout);
        addChangeListener(descriptionLayout, mDescriptionEditText);

        TextInputLayout requirementsLayout = addView.findViewById(R.id.requirements_layout);
        mRequirementsEditText = addView.findViewById(R.id.requirements_edit);
        setDefaultErrorMessage(requirementsLayout);
        addChangeListener(requirementsLayout, mRequirementsEditText);

        mNbBedroomsEditText = addView.findViewById(R.id.nb_bedrooms_edit);
        setDefaultValueForEditText(mNbBedroomsEditText);

        mNbBathroomsEditText = addView.findViewById(R.id.nb_bathrooms_edit);
        setDefaultValueForEditText(mNbBathroomsEditText);

        mNbParkingSpotEditText = addView.findViewById(R.id.nb_parking_edit);
        setDefaultValueForEditText(mNbParkingSpotEditText);

        mLivingAreaEditText = addView.findViewById(R.id.living_area_edit);
        setDefaultValueForEditText(mLivingAreaEditText);

        mAddImageError = addView.findViewById(R.id.images_text_error);
        mAddImageError.setVisibility(View.GONE);

        mHouseTypeSpinner = addView.findViewById(R.id.house_type_spinner);
        mListingTypeSpinner = addView.findViewById(R.id.listing_spinner);
        mRentalTermSpinner = addView.findViewById(R.id.rental_term_spinner);

        ImageButton mGenerateDescription = addView.findViewById(R.id.generate_description);
        mClearDescription = addView.findViewById(R.id.clear_description);

        mAddImages = addView.findViewById(R.id.add_image_btn);
        mListImagesRecyclerView = addView.findViewById(R.id.list_of_images_recyclerview);
        mImageTextView = addView.findViewById(R.id.images_text);
        mReviewHouseBtn = addView.findViewById(R.id.review_house_btn);
        mReviewHouseBtn.setEnabled(false);

        mImageAdapter = new ImageAdapter(new ArrayList<>());
        mListImagesRecyclerView.setAdapter(mImageAdapter);

        String text = getString(R.string.images_add);
        mImageTextView.setText(String.format(text, 0, MAX_SELECTED_IMAGES));


        // listing type spinner
        ArrayAdapter<ListingType> listingTypeAdapter = new ArrayAdapter<>(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListingType.values());
        mListingTypeSpinner.setAdapter(listingTypeAdapter);

        // house type spinner
        ArrayAdapter<HouseType> houseTypeAdapter = new ArrayAdapter<>(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, HouseType.values());
        mHouseTypeSpinner.setAdapter(houseTypeAdapter);

        // rental term spinner
        ArrayAdapter<RentalTerm> rentalTermAdapter = new ArrayAdapter<>(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, RentalTerm.values());
        mRentalTermSpinner.setAdapter(rentalTermAdapter);

        mImageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mImageTextView.setText(String.format(text, mImageAdapter.getItemCount(), MAX_SELECTED_IMAGES));
                mReviewHouseBtn.setEnabled(enableAddButton());
            }
        });

        // from review fragment
        Uri[] _uris = AddHouseFragmentArgs.fromBundle(requireArguments()).getListOfImages();
        if (_uris != null) {
            for (Uri uri : _uris) {
                mImageAdapter.addImageUri(uri);
            }
        }

        House _house = AddHouseFragmentArgs.fromBundle(requireArguments()).getHouse();
        if (_house != null) {
            mTitleEditText.setText(_house.getTitle());
            mLocationEditText.setText(_house.getLocation());
            mPriceEditText.setText(String.valueOf(_house.getPrice()));
            mNbBedroomsEditText.setText(String.valueOf(_house.getNumberBedrooms()));
            mNbBathroomsEditText.setText(String.valueOf(_house.getNumberBathrooms()));
            mNbParkingSpotEditText.setText(String.valueOf(_house.getNumberParkingSpot()));
            mLivingAreaEditText.setText(String.valueOf(_house.getLivingArea()));
            mDescriptionEditText.setText(_house.getDescription());
            mRequirementsEditText.setText(_house.getRequirements());
        }

        mAddImages.setOnClickListener(v -> {
            mAddImages.setEnabled(false);
            if (mRemainImages > 0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                mGetContent.launch(intent);

            } else {
                Toast.makeText(requireActivity(), String.format(getString(R.string.you_can_select_up_to), MAX_SELECTED_IMAGES), Toast.LENGTH_SHORT).show();
            }
            new Handler().postDelayed(() -> mAddImages.setEnabled(true), 1000);  // 1000 milliseconds delay
        });

        mReviewHouseBtn.setOnClickListener(v -> {
            mReviewHouseBtn.setEnabled(false);
            House house = getNewHouse();
            String d = Objects.requireNonNull(mDescriptionEditText.getText()).toString();
            house.setDescription(d);
            house.setRequirements(Objects.requireNonNull(mRequirementsEditText.getText()).toString());
            // TODO : change currency
            house.setPriceCurrency(PriceCurrency.CFA);

            setDefaultValueForEditText(mNbBedroomsEditText);
            setDefaultValueForEditText(mNbBathroomsEditText);
            setDefaultValueForEditText(mNbParkingSpotEditText);

            AddHouseFragmentDirections.ActionAddHouseFragmentToHouseReviewFragment action;
            Uri[] uris = new Uri[mImageAdapter.getItemCount()];
            for (int i = 0; i < mImageAdapter.getItemCount(); i++) {
                uris[i] = mImageAdapter.getListOfImageUri().get(i);
            }
            action = AddHouseFragmentDirections.actionAddHouseFragmentToHouseReviewFragment(house, uris);
            NavigationUtils.navigateSafe(mNavController, action);
            new Handler().postDelayed(() -> mReviewHouseBtn.setEnabled(true), 1000);  // 1000 milliseconds delay
        });

        mGenerateDescription.setOnClickListener(view -> {
            mGenerateDescription.setEnabled(false);
            DefaultConfirmationDialogFragment alertDialog = new DefaultConfirmationDialogFragment(R.string.generate_description_question, R.string.yes);
            alertDialog
                    .setPositiveListener(v -> {
                        alertDialog.dismiss();
                        DefaultEditDialogFragment dialog = new DefaultEditDialogFragment(R.string.description, R.string.use);
                        String desc = HouseDescriptionGenerator.generateDescription(getNewHouse(), requireContext());
                        dialog.setMessageContent(String.valueOf(Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY)));
                        dialog
                                .setPositiveListener(v1 -> {
                                    mDescriptionEditText.setText(desc);
                                    dialog.dismiss();
                                    mClearDescription.setVisibility(View.VISIBLE);
                                })
                                .show(getChildFragmentManager());
                    })
                    .show(getChildFragmentManager());

            new Handler().postDelayed(() -> {
                mGenerateDescription.setEnabled(true);
            }, 1000);
        });
        mClearDescription.setOnClickListener(view -> {
            mClearDescription.setEnabled(false);
            mDescriptionEditText.setText("");
            mClearDescription.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                mClearDescription.setEnabled(true);
            }, 1000);
        });
        hideKeyboard(addView);
    }


    @NonNull
    private House getNewHouse() {
        House house = new House();
        house.setTitle(Objects.requireNonNull(mTitleEditText.getText()).toString());
        house.setLocation(Objects.requireNonNull(mLocationEditText.getText()).toString());
        if (!Objects.requireNonNull(mPriceEditText.getText()).toString().isEmpty()) {
            house.setPrice(Double.parseDouble(Objects.requireNonNull(mPriceEditText.getText()).toString()));
        } else {
            house.setPrice(0);
        }
        house.setListingType(ListingType.valueOf(mListingTypeSpinner.getSelectedItem().toString()));
        house.setHouseType(HouseType.valueOf(mHouseTypeSpinner.getSelectedItem().toString()));
        house.setRentalTerm(RentalTerm.valueOf(mRentalTermSpinner.getSelectedItem().toString()));
        house.setNumberBedrooms(Integer.parseInt(Objects.requireNonNull(mNbBedroomsEditText.getText()).toString()));
        house.setNumberBathrooms(Integer.parseInt(Objects.requireNonNull(mNbBathroomsEditText.getText()).toString()));
        house.setNumberParkingSpot(Integer.parseInt(Objects.requireNonNull(mNbParkingSpotEditText.getText()).toString()));
        house.setLivingArea(Float.parseFloat(Objects.requireNonNull(mLivingAreaEditText.getText()).toString()));
        return house;
    }

    private void addChangeListener(TextInputLayout inputLayout, TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = String.valueOf(s).trim();
                if (text.isEmpty()) {
                    setDefaultErrorMessage(inputLayout);
                } else {
                    inputLayout.setErrorEnabled(false);
                }
                if (editText.equals(mDescriptionEditText)) {
                    if (mDescriptionEditText.getLineCount() > 2) {
                        mClearDescription.setVisibility(View.VISIBLE);
                    } else {
                        mClearDescription.setVisibility(View.GONE);
                    }
                }
                mReviewHouseBtn.setEnabled(enableAddButton());
            }
        });
    }

    private boolean isInputCorrect(TextInputEditText editText) {
        return editText.getText() != null && !editText.getText().toString().trim().isEmpty();
    }

    private void setDefaultErrorMessage(TextInputLayout layout) {
        layout.setErrorEnabled(true);
        layout.setError(getString(R.string.field_mandatory));
    }

    private boolean enableAddButton() {
        return isImageAdded()
                && isInputCorrect(mTitleEditText)
                && isInputCorrect(mLocationEditText)
                && isInputCorrect(mPriceEditText)
                && isInputCorrect(mDescriptionEditText)
                && isInputCorrect(mRequirementsEditText);
    }

    private void setDefaultValueForEditText(TextInputEditText editText) {
        editText.setText("0");
    }

    private boolean isImageAdded() {
        if (mImageAdapter.getItemCount() == 0) {
            mAddImageError.setVisibility(View.VISIBLE);
            return false;
        }
        mAddImageError.setVisibility(View.GONE);
        return true;
    }


    private void hideKeyboard(@NonNull View view) {
        View[] focusableViews = {mTitleEditText, mPriceEditText, mNbParkingSpotEditText, mNbBathroomsEditText, mNbBedroomsEditText, mLivingAreaEditText};
        view.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));
        ConstraintLayout constraintLayout = view.findViewById(R.id.constraint_layout);

        if (constraintLayout != null) {
            for (int i = 0; i < constraintLayout.getChildCount(); i++) {
                View innerView = constraintLayout.getChildAt(i);
                if (!(innerView instanceof TextInputEditText)) {
                    innerView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));
                }
            }
        }
    }


    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, AddHouseFragmentDirections.actionAddHouseFragmentToHomeFragment());

    }

    @Override
    public View.OnClickListener onNavAddClicked() {
        return view -> {
        };

    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, AddHouseFragmentDirections.actionAddHouseFragmentToProfileFragment());
    }
}