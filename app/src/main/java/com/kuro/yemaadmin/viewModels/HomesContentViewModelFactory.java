package com.kuro.yemaadmin.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kuro.yemaadmin.data.enums.HouseType;


public class HomesContentViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final HouseType houseType;
    private final String managedCountry;

    public HomesContentViewModelFactory(Application application, HouseType tabType, String managedCountry) {
        super(application);
        this.application = application;
        this.houseType = tabType;
        this.managedCountry = managedCountry;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomesContentViewModel.class)) {
            return (T) new HomesContentViewModel(application, houseType, managedCountry);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
