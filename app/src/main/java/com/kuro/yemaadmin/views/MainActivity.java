package com.kuro.yemaadmin.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.data.model.User;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mNavLayout, mNavHome, mNavAdd, mNavProfile;

    private ImageView mNavHomeImage, mNavAddImage, mNavProfileImage;
    private TextView mNavHomeTextView, mNavAddTextView, mNavProfileTextView;
    private String mManagedCountry;
    private String mManagedCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavLayout = findViewById(R.id.nav_layout);

        mNavHome = findViewById(R.id.nav_home);
        mNavAdd = findViewById(R.id.nav_add);
        mNavProfile = findViewById(R.id.nav_profile);

        mNavHomeImage = findViewById(R.id.nav_home_image);
        mNavAddImage = findViewById(R.id.nav_add_image);
        mNavProfileImage = findViewById(R.id.nav_profile_image);

        mNavHomeTextView = findViewById(R.id.nav_home_text);
        mNavAddTextView = findViewById(R.id.nav_add_text);
        mNavProfileTextView = findViewById(R.id.nav_profile_text);

    }

    public LinearLayout getNavLayout() {
        return mNavLayout;
    }

    public void setNavHomeClickListener(View.OnClickListener onClickListener) {
        mNavHome.setOnClickListener(onClickListener);
    }

    public void setNavAddClickListener(View.OnClickListener onClickListener) {
        mNavAdd.setOnClickListener(onClickListener);
    }

    public void setNavProfileClickListener(View.OnClickListener onClickListener) {
        mNavProfile.setOnClickListener(onClickListener);
    }

    /**
     * Reset navigation items (home, add, wishlist, profile)
     * by resetting the image and the text color
     */
    public void resetNavigationItems() {

        // resetting home image
        mNavHomeImage.setImageResource(R.drawable.home_24);
        mNavHomeTextView.setTextColor(getColor(R.color.text_unselected));

        // resetting add image
        mNavAddImage.setImageResource(R.drawable.add_24);
        mNavAddTextView.setTextColor(getColor(R.color.text_unselected));

        // resetting profile image
        mNavProfileImage.setImageResource(R.drawable.profile_24);
        mNavProfileTextView.setTextColor(getColor(R.color.text_unselected));
    }

    public void setActiveNavigationItem(NavigationItems item) {
        resetNavigationItems();
        switch (item) {
            case HOME:
                // setting home as active tab
                mNavHomeImage.setImageResource(R.drawable.home_black_24);
                mNavHomeTextView.setTextColor(getColor(R.color.black));
                break;
            case ADD:
                // setting add as active tab
                mNavAddImage.setImageResource(R.drawable.add_black_24);
                mNavAddTextView.setTextColor(getColor(R.color.black));
                break;
            case PROFILE:
                // setting profile as active tab
                mNavProfileImage.setImageResource(R.drawable.profile_black_24);
                mNavProfileTextView.setTextColor(getColor(R.color.black));
        }
    }

    public String getManagedCountry() {
        return mManagedCountry;
    }

    public void setManagedCountry(String mManagedCountry) {
        this.mManagedCountry = mManagedCountry;
    }

    public String getManagedCountryCode() {
        return mManagedCountryCode;
    }

    public void setManagedCountryCode(String mManagedCountryCode) {
        this.mManagedCountryCode = mManagedCountryCode;
    }

    public enum NavigationItems {
        HOME,
        ADD,
        PROFILE
    }
}