package com.kuro.yemaadmin.views.fragments;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.utils.ViewUtil;

import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private static final int PASSWORD_MIN_LENGTH = 4;
    private FirebaseAuth mFirebaseAuth;
    private TextInputEditText mEmail, mPassword;
    private Button mLoginBtn;
    private TextView mForgotPassword, mInvalidCredentials;
    private boolean mIsEmailValid, mIsPasswordValid;
    private Pattern mEmailPattern;

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
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToAddHouseFragment());
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
        loginView.setOnTouchListener(ViewUtil.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));

        validateEmail();
        validatePassword();

        mLoginBtn.setOnClickListener(v -> {
            if (mEmail.getText() != null && mPassword.getText() != null) {
                mFirebaseAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                navController.navigate(LoginFragmentDirections.actionLoginFragmentToAddHouseFragment());
                            } else {
                                mInvalidCredentials.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        mForgotPassword.setOnClickListener(view -> {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment());
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