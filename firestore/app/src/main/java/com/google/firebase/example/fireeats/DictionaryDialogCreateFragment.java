package com.google.firebase.example.fireeats;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        // For now we'll only use the display name of the logged in user if no owner is provided
        // in the UI. Eventually, when we're ready to lock things down, we'll need to only pull
        // this from the logged in user.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String owner = TextUtils.isEmpty(mOwnerText.getText()) ?
                user.getDisplayName() :
                mOwnerText.getText().toString();
        CustomDictionary dict = new CustomDictionary(
                mNameText.getText().toString(),
                owner,
                user.getEmail(),
                new ArrayList<Word>());

        if (mDictionaryListener != null) {
            mDictionaryListener.onDictionary(dict);
        }

        dismiss();
        clearFormFields();
    }

    private void clearFormFields() {
        mNameText.getText().clear();
        mOwnerText.getText().clear();
    }
}
