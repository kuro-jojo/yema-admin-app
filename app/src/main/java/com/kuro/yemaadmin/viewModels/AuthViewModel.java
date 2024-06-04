package com.kuro.yemaadmin.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.kuro.yemaadmin.repositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;


    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        userMutableLiveData = authRepository.getFirebaseUserMutableLiveData();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void resetPassword(String email) {
        authRepository.resetPassword(email);
    }

    public void updatePassword(String password) {
        authRepository.updatePassword(password);
    }

    public void logOut() {
        authRepository.logout();
    }

    public MutableLiveData<Boolean> getPasswordUpdatedMutableLiveData() {
        return authRepository.getPasswordUpdatedMutableLiveData();
    }
}
