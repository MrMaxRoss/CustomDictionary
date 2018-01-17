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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog Fragment containing word filter form.
 */
public class WordFilterDialogFragment extends DialogFragment {

    public static final String TAG = "WordFilterDialog";

    interface FilterListener {
        void onFilter(WordFilters filters);
    }

    private View mRootView;

    @BindView(R.id.word_filter_owner)
    EditText mWordOwner;

    @BindView(R.id.word_filter_last_updater)
    EditText mWordLastUpdater;

    @BindView(R.id.word_filter_part_of_speech)
    Spinner mWordPartOfSpeechSpinner;

    @BindView(R.id.word_spinner_sort)
    Spinner mSortSpinner;


    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.word_dialog_filters, container, false);
        ButterKnife.bind(this, mRootView);

        List<String> partOfSpeechStrings = new ArrayList<>();
        partOfSpeechStrings.add(getContext().getString(R.string.any_part_of_speech));
        partOfSpeechStrings.addAll(PartOfSpeech.displayStrings());
        mWordPartOfSpeechSpinner.setAdapter(
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, partOfSpeechStrings));


        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.word_button_search)
    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    private String getSelectedOwner() {
        return mWordOwner.getText().toString();
    }

    private String getSelectedLastUpdater() { return mWordLastUpdater.getText().toString(); }

    private PartOfSpeech getSelectedPartOfSpeech() {
        int pos = mWordPartOfSpeechSpinner.getSelectedItemPosition();
        if (pos == 0) {
            return null;
        }
        return PartOfSpeech.values()[pos - 1];
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_word_owner).equals(selected)) {
            return Word.FIELD_OWNER;
        } else if (getString(R.string.sort_by_word_last_updater).equals(selected)) {
            return Word.FIELD_LAST_UPDATER;
        } else if (getString(R.string.sort_by_word_part_of_speech).equals(selected)) {
            return Word.FIELD_PART_OF_SPEECH;
        } else if (getString(R.string.sort_by_word_id).equals(selected)) {
            // This is the default sort, so return nothing.
            return null;
        }
        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_word_owner).equals(selected)) {
            return Query.Direction.ASCENDING;
        } else if (getString(R.string.sort_by_word_last_updater).equals(selected)) {
            return Query.Direction.ASCENDING;
        } else if (getString(R.string.sort_by_word_part_of_speech).equals(selected)) {
            return Query.Direction.ASCENDING;
        } else if (getString(R.string.sort_by_word_id).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mWordOwner.getText().clear();
            mWordLastUpdater.getText().clear();
            mWordPartOfSpeechSpinner.setSelection(0);
            mSortSpinner.setSelection(0);
        }
    }

    public WordFilters getFilters() {
        WordFilters filters = new WordFilters();

        if (mRootView != null) {
            filters.setOwner(getSelectedOwner());
            filters.setLastUpdater(getSelectedLastUpdater());
            filters.setPartOfSpeech(getSelectedPartOfSpeech());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }
}
