package com.kuro.yemaadmin.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.kuro.yemaadmin.R;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Arrays;

public class EditPasswordDialogFragment extends DialogFragment {

    public static final int PASSWORD_MIN_LENGTH = 6;
    private TextInputEditText mNewPassword, mRepeatPassword;
    private int mMessageId = -1, mPositiveBtnMessageId = -1, mNegativeBtnMessageId = -1;
    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;
    private Button mPositiveBtn;
    private TextView mError, mForce;
    private boolean noError;
    private ProgressBar mProgressBar;
    private PasswordValidator mValidator;

    public EditPasswordDialogFragment(int message, int positiveBtnMessageId) {
        mMessageId = message;
        mPositiveBtnMessageId = positiveBtnMessageId;
        mNegativeBtnMessageId = R.string.cancel;
    }

    public EditPasswordDialogFragment() {
        mNegativeBtnMessageId = R.string.cancel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_edit_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mValidator = new PasswordValidator(Arrays.asList(
                new LengthRule(PASSWORD_MIN_LENGTH, 16),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()
        ));
        mPositiveBtn = view.findViewById(R.id.positive_btn);
        Button negativeBtn = view.findViewById(R.id.negative_btn);
        mPositiveBtn.setEnabled(false);
        mNewPassword = view.findViewById(R.id.new_password);
        mRepeatPassword = view.findViewById(R.id.repeat_password);
        mError = view.findViewById(R.id.invalid_password);
        mForce = view.findViewById(R.id.invalid_password_force);

        mProgressBar = view.findViewById(R.id.password_progressbar);
        mProgressBar.setVisibility(View.GONE);

        mNewPassword.addTextChangedListener(validatePassword(mRepeatPassword));
        mRepeatPassword.addTextChangedListener(validatePassword(mNewPassword));
        TextView dialogText = view.findViewById(R.id.dialog_title);

        if (mMessageId > 0) {
            dialogText.setText(getResources().getString(mMessageId));
        }
        if (mPositiveBtnMessageId > 0) {
            mPositiveBtn.setText(getResources().getString(mPositiveBtnMessageId));
        }
        if (mNegativeBtnMessageId > 0) {
            negativeBtn.setText(getResources().getString(mNegativeBtnMessageId));
        }

        mPositiveBtn.setOnClickListener(mPositiveListener);
        negativeBtn.setOnClickListener(mNegativeListener != null ? mNegativeListener : v -> dismiss());
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, "CONFIRMATION_DIALOG");
    }

    private TextWatcher validatePassword(TextInputEditText other) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mError.setVisibility(View.VISIBLE);
                String password = s.toString();
                if (password.isEmpty() || (other.getText() != null && !password.equals(other.getText().toString()))) {
                    mError.setText(requireActivity().getString(R.string.passwords_do_not_match));
                }
                if (other.getText() != null && password.equals(other.getText().toString())) {
                    if (password.length() < PASSWORD_MIN_LENGTH) {
                        String e = requireActivity().getString(R.string.password_must_be_at_least_6_characters_long);
                        mError.setText(String.format(e, PASSWORD_MIN_LENGTH));
                    } else {
                        RuleResult result = mValidator.validate(new PasswordData(password));
                        if (!result.isValid()) {
                            mForce.setVisibility(View.VISIBLE);
                        } else {
                            mForce.setVisibility(View.GONE);
                            noError = true;
                        }
                        mError.setVisibility(View.GONE);
                    }
                }
                mPositiveBtn.setEnabled(noError);
                noError = false;
            }
        };
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public Button getPositiveBtn() {
        return mPositiveBtn;
    }

    public String getNewPassword() {
        return mNewPassword.getText() != null ? mNewPassword.getText().toString() : "";
    }

    public String getRepeatPassword() {
        return mRepeatPassword.getText() != null ? mRepeatPassword.getText().toString() : "";
    }

    public EditPasswordDialogFragment setPositiveListener(@NonNull View.OnClickListener mOnConfirmationListener) {
        this.mPositiveListener = mOnConfirmationListener;
        return this;
    }

    public EditPasswordDialogFragment setNegativeListener(@Nullable View.OnClickListener mOnConfirmationListener) {
        this.mNegativeListener = mOnConfirmationListener;
        return this;
    }
}

