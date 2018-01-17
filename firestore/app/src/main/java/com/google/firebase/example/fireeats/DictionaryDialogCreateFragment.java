package com.google.firebase.example.fireeats;

import android.view.View;
import android.widget.EditText;

import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.example.fireeats.model.Word;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Dialog Fragment containing word form.
 */
public class DictionaryDialogCreateFragment extends DictionaryDialogFragment {

    public static final String TAG = "DictionaryDialogCreate";

    @BindView(R.id.dictionary_form_name)
    EditText mNameText;

    @BindView(R.id.dictionary_form_owner)
    EditText mOwnerText;

    @Override
    protected int getLayout() {
        return R.layout.dialog_dictionary_create;
    }

    @OnClick(R.id.dictionary_form_button)
    public void onSubmitClicked(View view) {
        CustomDictionary dict = new CustomDictionary(
                mNameText.getText().toString(),
                mOwnerText.getText().toString(),
                new ArrayList<Word>());

        if (mDictionaryListener != null) {
            mDictionaryListener.onDictionary(dict, this);
        }

        dismiss();
    }

    @Override
    public void onSuccessInternal() {
        mNameText.getText().clear();
        mOwnerText.getText().clear();
    }
}
