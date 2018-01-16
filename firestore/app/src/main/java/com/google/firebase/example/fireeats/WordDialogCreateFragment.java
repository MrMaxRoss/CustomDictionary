package com.google.firebase.example.fireeats;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.example.fireeats.model.PartOfSpeech;
import com.google.firebase.example.fireeats.model.Word;

import butterknife.BindView;
import butterknife.ButterKnife;
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

        Word word = new Word(dictionaryId, mNameText.getText().toString(), mDefinitionText.getText().toString(),
                mExampleSentenceText.getText().toString(), partOfSpeech, mOwnerText.getText().toString());

        if (mWordListener != null) {
            mWordListener.onWord(word, this);
        }

        dismiss();
    }

    @Override
    public void onSuccessInternal() {
        mNameText.getText().clear();
        mOwnerText.getText().clear();
    }
}
