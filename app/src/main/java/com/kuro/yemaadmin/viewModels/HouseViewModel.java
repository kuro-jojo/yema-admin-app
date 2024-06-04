package com.kuro.yemaadmin.viewModels;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kuro.androidutils.logging.Logger;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.data.model.House;
import com.kuro.yemaadmin.repositories.HouseRepository;
import com.kuro.yemaadmin.views.dialogs.VerifyDialogFragment;

import java.util.List;
import java.util.Objects;

public class HouseViewModel extends AndroidViewModel implements HouseRepository.OnFirestoreTaskComplete {
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<House> houseMutableLiveData;
    private final HouseRepository houseRepository;
    private final MutableLiveData<List<House>> houseListMutableLiveData;

    public HouseViewModel(@NonNull Application application) {
        super(application);
        houseRepository = new HouseRepository(this);
        houseMutableLiveData = new MutableLiveData<>();
        houseListMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<House>> getHouseListMutableLiveData() {
        return houseListMutableLiveData;
    }

    public void addHouse(House house, String managedCountry) {
        houseRepository.addHouse(house, managedCountry);
        houseMutableLiveData.setValue(house);
    }


    public void getHouse(String houseId) {
        houseRepository.getHouseByHouseId(houseId);
    }

    @Override
    public void housesDataLoaded(List<House> houseList) {
        houseListMutableLiveData.setValue(houseList);
    }

    @Override
    public void houseDataLoaded(House house) {
        houseMutableLiveData.setValue(house);
    }

    @Override
    public void onError(Exception e) {
        Logger.error("HouseFirestore", Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onRuntimeError(RuntimeException e) {

        Activity activity = FRAGMENT_MANAGER.getFragments().get(0).requireActivity();
        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment(activity.getString(R.string.error_on_server));
        verifyDialogFragment.show(HouseViewModel.FRAGMENT_MANAGER, "DIALOG");

        Logger.error("HouseFirestore", "Runtime error : " + e.getMessage());

        new Handler().postDelayed(activity::finish, 2000);
    }

    public MutableLiveData<House> getHouseMutableLiveData() {
        return houseMutableLiveData;
    }

    public void setOnHouseCreated(HouseRepository.OnHouseCreated onHouseCreated) {
        houseRepository.setOnHouseCreated(onHouseCreated);
    }
}