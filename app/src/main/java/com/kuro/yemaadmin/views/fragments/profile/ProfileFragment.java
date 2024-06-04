package com.kuro.yemaadmin.views.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.viewModels.AuthViewModel;
import com.kuro.yemaadmin.views.LoginActivity;
import com.kuro.yemaadmin.views.MainActivity;
import com.kuro.yemaadmin.views.MainActivityInterface;
import com.kuro.yemaadmin.views.dialogs.DangerConfirmationDialog;
import com.kuro.yemaadmin.views.dialogs.EditPasswordDialogFragment;

import java.util.Objects;

public class ProfileFragment extends Fragment implements MainActivityInterface {
    private NavController mNavController;
    private AuthViewModel mAuthViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuthViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()).create(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View profileView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(profileView, savedInstanceState);

        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.VISIBLE);

        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavAddClickListener(onNavAddClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.PROFILE);

        mNavController = Navigation.findNavController(profileView);

        CountryCodePicker ccpCountry = profileView.findViewById(R.id.managed_country);
        ccpCountry.setCountryForNameCode(mainActivity.getManagedCountryCode());

        LinearLayout editPassword = profileView.findViewById(R.id.edit_password);
        TextView username = profileView.findViewById(R.id.username);
        username.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());


        LinearLayout logoutLayout = profileView.findViewById(R.id.profile_logout);
        logoutLayout.setOnClickListener(view -> {
            DangerConfirmationDialog logoutDialog = new DangerConfirmationDialog(R.string.confirm_logout, R.string.logout);
            logoutDialog.setOnConfirmationListener(view1 -> {
                logout();
            }).show(getChildFragmentManager());
        });


        editPassword.setOnClickListener(view -> {
            EditPasswordDialogFragment dialogFragment = new EditPasswordDialogFragment();
            dialogFragment.setPositiveListener(v -> {
                String newPassword = dialogFragment.getNewPassword();
                String repeatedPassword = dialogFragment.getRepeatPassword();

                if (newPassword.equals(repeatedPassword)) {
                    mAuthViewModel.updatePassword(newPassword);
                    dialogFragment.getProgressBar().setVisibility(View.VISIBLE);
                    dialogFragment.getPositiveBtn().setVisibility(View.INVISIBLE);

                    mAuthViewModel.getPasswordUpdatedMutableLiveData().observe(getViewLifecycleOwner(), passwordChanged -> {
                        if (passwordChanged) {
                            Toast.makeText(requireContext(), requireActivity().getString(R.string.password_updated_successfully), Toast.LENGTH_SHORT).show();
                            logout();
                        }
                    });
                }
            }).show(getChildFragmentManager());
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!mNavController.popBackStack()) {
                    NavigationUtils.navigateSafe(mNavController, ProfileFragmentDirections.actionProfileFragmentToHomeFragment());
                }
            }
        });
    }

    private void logout() {
        mAuthViewModel.logOut();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        requireActivity().startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = ProfileFragmentDirections.actionProfileFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavAddClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, ProfileFragmentDirections.actionProfileFragmentToAddHouseFragment());
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
        };
    }
}
