package com.google.firebase.example.fireeats;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.model.CustomDictionary;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog Fragment containing dictionary form.
 */
public abstract class DictionaryDialogFragment extends DialogFragment {

    public static final String TAG = "DictionaryDialog";

    interface DictionaryListener {

        void onDictionary(CustomDictionary dict, DictionaryDialogFragment fragment);

    }

    DictionaryListener mDictionaryListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    protected abstract int getLayout();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DictionaryListener) {
            mDictionaryListener = (DictionaryListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }


    @OnClick(R.id.dictionary_form_cancel)
    public void onCancelClicked(View view) {
        dismiss();
    }

    public final void onSuccess() {
        onSuccessInternal();
    }

    public void onSuccessInternal() {
        // default impl does nothing
    }
}
