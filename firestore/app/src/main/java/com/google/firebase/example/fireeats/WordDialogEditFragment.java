package com.google.firebase.example.fireeats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.example.fireeats.model.PartOfSpeech;
import com.google.firebase.example.fireeats.model.Word;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by maxr on 1/13/18.
 */

public class WordDialogEditFragment extends WordDialogFragment {
    public static final String TAG = "WordDialogEdit";

    public static final String KEY_WORD_ID = "key_word_id";
    public static final String KEY_OWNER = "key_owner";

    @BindView(R.id.word_edit_form_name)
    TextView mNameText;

    @BindView(R.id.word_edit_form_owner)
    TextView mOwnerText;

    Word word;

    @Override
    protected int getLayout() {
        return R.layout.dialog_word_edit;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        // Get word ID from extras
        String wordId = getActivity().getIntent().getExtras().getString(KEY_WORD_ID);
        if (wordId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_WORD_ID);
        }
        mNameText.setText(String.format(mNameText.getText().toString(), wordId));

        if (word == null) {
            throw new IllegalArgumentException("Must pass in word");
        }
        mOwnerText.setText(word.getOwner());
        mDefinitionText.setText(word.getDefinition());
        mExampleSentenceText.setText(word.getExampleSentence());
        mPartOfSpeechSpinner.setSelection(word.getPartOfSpeech().ordinal());

        return v;
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

    public void setWord(Word word) {
        this.word = word;
    }
}
