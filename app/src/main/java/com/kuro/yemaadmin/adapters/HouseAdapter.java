package com.kuro.yemaadmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.data.model.House;
import com.kuro.yemaadmin.views.fragments.home.HomeFragmentDirections;

import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.ViewHolder> {
    private final NavController mNavController;
    private final Context mContext;
    private List<House> mListOfHouses;


    public HouseAdapter(NavController mNavController, Context context) {

        this.mNavController = mNavController;
        this.mContext = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListOfHouses(List<House> mListOfHouses) {
        this.mListOfHouses = mListOfHouses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseAdapter.ViewHolder holder, int position) {
        House house = mListOfHouses.get(position);
        String title = house.getTitle();
        if (!house.getLocation().isEmpty()) {
            title += " at " + house.getLocation();
        }

        holder.mHouseItemTitle.setText(title);
        holder.mHouseItemPrice.setText(String.valueOf(house.getPrice()));
        // TODO: use Glide to show images
        Glide.with(holder.itemView).load(house.getImages().get(0))
//              .placeholder(R.drawable.template_house_item)
                .into(holder.mHouseItemImage);
        holder.mHouseItemImageTotal.setText(String.valueOf(house.getNumberOfImages()));
        int i = 0;
        if (house.getNumberOfImages() > 0) {
            i = 1;
        }
        holder.mHouseItemImageIndex.setText(String.valueOf(i));
        holder.mHouseItemPriceCurrency.setText(house.getPriceCurrency().name());
        String rentalTerm = mContext.getString(R.string.house_rental_term);

        holder.mHouseItemRentalTerm.setText(String.format(rentalTerm, house.getRentalTerm().name().toLowerCase()));

        View.OnClickListener onClickListener = view -> {
            HomeFragmentDirections.ActionHomeFragmentToHouseDetailsFragment action;
            action = HomeFragmentDirections.actionHomeFragmentToHouseDetailsFragment();
            action.setHouseId(house.getHouseId());
            NavigationUtils.navigateSafe(mNavController, action);

        };

        holder.mHouseItemImage.setOnClickListener(onClickListener);
        holder.mHouseItemTitle.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        if (mListOfHouses == null) return 0;
        return mListOfHouses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mHouseItemImage;
        private final TextView mHouseItemTitle, mHouseItemPrice, mHouseItemImageTotal, mHouseItemImageIndex, mHouseItemPriceCurrency, mHouseItemRentalTerm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mHouseItemImage = itemView.findViewById(R.id.detail_house_image);
            mHouseItemTitle = itemView.findViewById(R.id.house_item_title);
            mHouseItemPrice = itemView.findViewById(R.id.house_item_price);
            mHouseItemImageTotal = itemView.findViewById(R.id.house_item_image_total);
            mHouseItemImageIndex = itemView.findViewById(R.id.house_item_image_index);
            mHouseItemPriceCurrency = itemView.findViewById(R.id.house_item_price_currency);
            mHouseItemRentalTerm = itemView.findViewById(R.id.house_item_rental_term);
        }
    }

}
