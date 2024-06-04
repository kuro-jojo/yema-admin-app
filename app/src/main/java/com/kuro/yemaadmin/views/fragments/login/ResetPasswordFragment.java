package com.kuro.yemaadmin.views.fragments.login;

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
import com.kuro.yemaadmin.R;
import com.kuro.androidutils.ui.SoftKeyboard;
import com.kuro.yemaadmin.viewModels.AuthViewModel;

import java.util.regex.Pattern;

public class ResetPasswordFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private TextInputEditText mEmail;
    private Button mSendResetMail;
    private TextView mResetPostMessage, mLoginText;
    private Pattern mEmailPattern;
    private AuthViewModel mAuthViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuthViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())).get(AuthViewModel.class);

        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View resetPasswordView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(resetPasswordView, savedInstanceState);

        NavController navController = Navigation.findNavController(resetPasswordView);
        mFirebaseAuth = FirebaseAuth.getInstance();
        String emailRegex = "[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        mEmailPattern = Pattern.compile(emailRegex);


        mEmail = resetPasswordView.findViewById(R.id.email);
        mSendResetMail = resetPasswordView.findViewById(R.id.reset_password_btn);
        mResetPostMessage = resetPasswordView.findViewById(R.id.reset_post_message);
        mLoginText = resetPasswordView.findViewById(R.id.login_text);

        mSendResetMail.setEnabled(false);
        mResetPostMessage.setVisibility(View.INVISIBLE);

        // hiding the keyboard on focus lost
        resetPasswordView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(mEmail, requireContext()));

        validateEmail();

        mSendResetMail.setOnClickListener(v -> {
            // TODO: update this by using MVVM
            if (mEmail.getText() != null) {
                mFirebaseAuth.sendPasswordResetEmail(mEmail.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mResetPostMessage.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(), "Mail successfully sent", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to send the mail", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        mLoginText.setOnClickListener(view -> {
            NavigationUtils.navigateSafe(navController, ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment());
        });
    }

    private void validateEmail() {
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mSendResetMail.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = String.valueOf(s);

                mSendResetMail.setEnabled(mEmailPattern.matcher(email).find());
            }
        });
    }
}