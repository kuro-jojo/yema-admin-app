package com.kuro.yemaadmin.viewModels;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.kuro.androidutils.logging.Logger;
import com.kuro.yemaadmin.data.model.User;
import com.kuro.yemaadmin.repositories.UserRepository;
import com.kuro.yemaadmin.views.dialogs.VerifyDialogFragment;

import java.util.Objects;

public class UserViewModel extends AndroidViewModel implements UserRepository.OnFirestoreTaskComplete {
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<User> userMutableLiveData;
    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(this);
        userMutableLiveData = new MutableLiveData<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            getUser(auth.getUid());
        }
    }

    public void getUser(String uid) {
        userRepository.getUser(uid);
    }

    @Override
    public void userDataLoaded(User user) {
        userMutableLiveData.setValue(user);
    }

    @Override
    public void onError(Exception e) {
        Logger.error("UserFirestore", Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onRuntimeError(RuntimeException e) {

        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment("An error occurred on the server. \n Exiting the app");
        verifyDialogFragment.show(UserViewModel.FRAGMENT_MANAGER, "DIALOG");

        Logger.error("SERVER_ERROR", "error : " + e.getMessage());

        new Handler().postDelayed(() -> FRAGMENT_MANAGER.getFragments().get(0).requireActivity().finish(), 5000);
    }

    public MutableLiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
