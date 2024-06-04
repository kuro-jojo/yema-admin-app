package com.kuro.yemaadmin.viewModels;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kuro.androidutils.logging.Logger;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.data.model.House;
import com.kuro.yemaadmin.repositories.HouseRepository;
import com.kuro.yemaadmin.views.dialogs.VerifyDialogFragment;

import java.util.List;
import java.util.Objects;

public class HomesContentViewModel extends AndroidViewModel implements HouseRepository.OnFirestoreTaskComplete {
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<List<House>> houseMutableLiveData;
    private final HouseRepository houseRepository;

    public HomesContentViewModel(@NonNull Application application, @Nullable HouseType tabType, @NonNull String managedCountry) {
        super(application);
        houseRepository = new HouseRepository(this);
        houseMutableLiveData = new MutableLiveData<>();
        getHouses(tabType, managedCountry);
    }

    public void getHouses(@Nullable HouseType houseType, @NonNull String managedCountry) {
        if (houseType != null) {
            if (!managedCountry.isEmpty()) {
                houseRepository.getHousesByHouseTypeAndCountry(houseType, managedCountry);
            } else {
                houseRepository.getHousesByHouseType(houseType);
            }
        }
        if (houseType == null) {
            if (!managedCountry.isEmpty()) {
                houseRepository.getAllHousesByCountry(managedCountry);
            } else {
                houseRepository.getAllHouses();
            }
        }
    }


    @Override
    public void housesDataLoaded(List<House> houseList) {
        houseMutableLiveData.setValue(houseList);
    }

    @Override
    public void houseDataLoaded(House house) {

    }

    @Override
    public void onError(Exception e) {
        Logger.error("HouseFirestore", Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onRuntimeError(RuntimeException e) {
        Activity activity = FRAGMENT_MANAGER.getFragments().get(0).requireActivity();
        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment(activity.getString(R.string.error_on_server));
        verifyDialogFragment.show(HomesContentViewModel.FRAGMENT_MANAGER, "DIALOG");

        new Handler().postDelayed(() -> {
            if (FRAGMENT_MANAGER.getFragments().size() > 0) {
                activity.finish();
            }
        }, 5000);
    }

    public MutableLiveData<List<House>> gethouseMutableLiveData() {
        return houseMutableLiveData;
    }
}
