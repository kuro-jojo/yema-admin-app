package com.kuro.yemaadmin.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.kuro.yemaadmin.R;

public class DefaultEditDialogFragment extends DialogFragment {

    TextInputEditText mMessageContent;
    private int mMessageId = -1, mPositiveBtnMessageId = -1, mNegativeBtnMessageId = -1;
    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;
    private String mContent;

    public DefaultEditDialogFragment(int message, int positiveBtnMessageId) {
        mMessageId = message;
        mPositiveBtnMessageId = positiveBtnMessageId;
        mNegativeBtnMessageId = R.string.cancel;
    }

    public DefaultEditDialogFragment setPositiveListener(@NonNull View.OnClickListener mOnConfirmationListener) {
        this.mPositiveListener = mOnConfirmationListener;
        return this;
    }

    public DefaultEditDialogFragment setNegativeListener(@Nullable View.OnClickListener mOnConfirmationListener) {
        this.mNegativeListener = mOnConfirmationListener;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button positiveBtn = view.findViewById(R.id.positive_btn);
        Button negativeBtn = view.findViewById(R.id.negative_btn);
        mMessageContent = view.findViewById(R.id.message_content);

        mMessageContent.setText(mContent);
        TextView dialogText = view.findViewById(R.id.dialog_title);

        if (mMessageId > 0) {
            dialogText.setText(getResources().getString(mMessageId));
        }
        if (mPositiveBtnMessageId > 0) {
            positiveBtn.setText(getResources().getString(mPositiveBtnMessageId));
        }
        if (mNegativeBtnMessageId > 0) {
            negativeBtn.setText(getResources().getString(mNegativeBtnMessageId));
        }

        positiveBtn.setOnClickListener(mPositiveListener);
        negativeBtn.setOnClickListener(mNegativeListener != null ? mNegativeListener : v -> dismiss());
    }

    public String getDialogContent() {
        return mMessageContent.getText() != null ? mMessageContent.getText().toString() : getString(R.string.is_this_still_available);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, "CONFIRMATION_DIALOG");
    }

    public void setMessageContent(String message) {
        mContent = message;
    }
}

