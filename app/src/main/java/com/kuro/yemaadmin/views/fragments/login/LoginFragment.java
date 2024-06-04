package com.kuro.yemaadmin.views.fragments.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.androidutils.ui.SoftKeyboard;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.viewModels.AuthViewModel;
import com.kuro.yemaadmin.viewModels.UserViewModel;
import com.kuro.yemaadmin.views.MainActivity;

import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private static final int PASSWORD_MIN_LENGTH = 6;
    private FirebaseAuth mFirebaseAuth;
    private TextInputEditText mEmail, mPassword;
    private Button mLoginBtn;
    private TextView mForgotPassword, mInvalidCredentials;
    private boolean mIsEmailValid, mIsPasswordValid;
    private Pattern mEmailPattern;
    private AuthViewModel mAuthViewModel;
    private UserViewModel mUserViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()).create(AuthViewModel.class);
        UserViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mUserViewModel = new ViewModelProvider(this, ViewModelProvider.
                AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View loginView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(loginView, savedInstanceState);

        NavController navController = Navigation.findNavController(loginView);
        mFirebaseAuth = FirebaseAuth.getInstance();
        String emailRegex = "[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        mEmailPattern = Pattern.compile(emailRegex);

        if (mFirebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return;
        }

        mEmail = loginView.findViewById(R.id.email);
        mPassword = loginView.findViewById(R.id.password);
        mLoginBtn = loginView.findViewById(R.id.login_btn);
        mForgotPassword = loginView.findViewById(R.id.forgot_password_txt);
        mInvalidCredentials = loginView.findViewById(R.id.invalid_credentials);

        mLoginBtn.setEnabled(false);
        mInvalidCredentials.setVisibility(View.INVISIBLE);

        // hiding the keyboard on focus lost
        View[] focusableViews = {mEmail, mPassword};
        loginView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));

        validateEmail();
        validatePassword();
        mAuthViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                Toast.makeText(requireContext(), "Authentication succeeded", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } else {
                mInvalidCredentials.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
        mLoginBtn.setOnClickListener(v -> {
            if (mEmail.getText() != null && mPassword.getText() != null) {
                mAuthViewModel.login(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        mForgotPassword.setOnClickListener(view -> {
            NavigationUtils.navigateSafe(navController, LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment());
        });
    }

    private void validateEmail() {
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInvalidCredentials.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = String.valueOf(s);
                mIsEmailValid = mEmailPattern.matcher(email).find();

                mLoginBtn.setEnabled(mIsEmailValid && mIsPasswordValid);
            }
        });
    }

    private void validatePassword() {
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInvalidCredentials.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = String.valueOf(s);
                mIsPasswordValid = password.length() >= PASSWORD_MIN_LENGTH;
                mLoginBtn.setEnabled(mIsEmailValid && mIsPasswordValid);
            }
        });
    }
}