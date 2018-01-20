package com.google.firebase.example.fireeats;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fireeats.model.PartOfSpeech;
import com.google.firebase.example.fireeats.model.Word;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Dialog Fragment containing word form.
 */
public class WordDialogCreateFragment extends WordDialogFragment {

    public static final String TAG = "WordDialogCreate";

    @BindView(R.id.word_form_name)
    EditText mNameText;

    @BindView(R.id.word_form_owner)
    EditText mOwnerText;

    @Override
    protected int getLayout() {
        return R.layout.dialog_word_create;
    }

    @OnClick(R.id.word_form_button)
    public void onSubmitClicked(View view) {
        PartOfSpeech partOfSpeech = PartOfSpeech.values()[mPartOfSpeechSpinner.getSelectedItemPosition()];
        String owner = TextUtils.isEmpty(mOwnerText.getText()) ?
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName() :
                mOwnerText.getText().toString();

        Word word = new Word(dictionaryId, mNameText.getText().toString(), mDefinitionText.getText().toString(),
                mExampleSentenceText.getText().toString(), partOfSpeech, owner, owner);

        if (mWordListener != null) {
            mWordListener.onWord(word);
        }

        dismiss();
        clearFormFields();
    }

    void clearFormFields() {
        super.clearFormFields();
        mNameText.getText().clear();
        mOwnerText.getText().clear();
    }
}
