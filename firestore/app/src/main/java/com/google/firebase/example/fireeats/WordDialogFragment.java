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
public abstract class WordDialogFragment extends DialogFragment {

    public static final String TAG = "WordDialog";

    public static final String KEY_DICTIONARY_ID = "key_dictionary_id";

    @BindView(R.id.word_form_definition)
    EditText mDefinitionText;

    @BindView(R.id.word_form_example_sentence)
    EditText mExampleSentenceText;

    @BindView(R.id.word_form_part_of_speech)
    Spinner mPartOfSpeechSpinner;

    interface WordListener {

        void onWord(Word word, WordDialogFragment fragment);

    }

    WordListener mWordListener;

    String dictionaryId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, v);

        mPartOfSpeechSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, PartOfSpeech.displayStrings()));

        // Get dictionary ID from extras
        dictionaryId = getActivity().getIntent().getExtras().getString(KEY_DICTIONARY_ID);
        if (dictionaryId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_DICTIONARY_ID);
        }

        return v;
    }

    protected abstract int getLayout();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof WordListener) {
            mWordListener = (WordListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }


    @OnClick(R.id.word_form_cancel)
    public void onCancelClicked(View view) {
        dismiss();
    }

    public final void onSuccess() {
        mDefinitionText.getText().clear();
        mExampleSentenceText.getText().clear();
        mPartOfSpeechSpinner.setSelection(0);
        onSuccessInternal();
    }

    public void onSuccessInternal() {
        // default impl does nothing
    }
}
