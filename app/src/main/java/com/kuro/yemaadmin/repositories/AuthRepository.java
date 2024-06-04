package com.kuro.yemaadmin.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kuro.androidutils.logging.Logger;

import java.util.Objects;

public class AuthRepository {

    private final MutableLiveData<Boolean> passwordUpdatedMutableLiveData;
    private final MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private final FirebaseAuth mFirebaseAuth;

    public AuthRepository() {

        firebaseUserMutableLiveData = new MutableLiveData<>();
        passwordUpdatedMutableLiveData = new MutableLiveData<>();

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            firebaseUserMutableLiveData.setValue(mFirebaseAuth.getCurrentUser());
        }
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }


    public void login(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Logger.log("Authentication succeeded");
                        firebaseUserMutableLiveData.setValue(mFirebaseAuth.getCurrentUser());
                    } else {
                        Logger.log("Authentication failed");
                        Logger.error("FirebaseEMAIL", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                        firebaseUserMutableLiveData.setValue(null);
                    }
                });
    }

    public void resetPassword(String email) {
        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUserMutableLiveData.postValue(mFirebaseAuth.getCurrentUser());
                    } else {
                        Logger.error("FirebaseEMAIL", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                    }
                });
    }

    public void updatePassword(String password) {
        Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).updatePassword(password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordUpdatedMutableLiveData.setValue(true);
                    } else {
                        Logger.error("FirebaseEMAIL", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                    }
                });
    }

    public void logout() {
        mFirebaseAuth.signOut();
    }

    public MutableLiveData<Boolean> getPasswordUpdatedMutableLiveData() {
        return passwordUpdatedMutableLiveData;
    }
}
