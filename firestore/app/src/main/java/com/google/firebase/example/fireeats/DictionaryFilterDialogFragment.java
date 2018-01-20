package com.google.firebase.example.fireeats;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog Fragment containing dictionary filter form.
 */
public class DictionaryFilterDialogFragment extends DialogFragment {

    public static final String TAG = "DictionaryFilterDialog";

    interface FilterListener {

        void onFilter(DictionaryFilters filters);

    }

    private View mRootView;

    @BindView(R.id.dictionary_filter_owner)
    EditText mDictionaryOwner;

    @BindView(R.id.dictionary_filter_last_updater)
    EditText mDictionaryLastUpdater;

    @BindView(R.id.dictionary_spinner_sort)
    Spinner mSortSpinner;


    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dictionary_dialog_filters, container, false);
        ButterKnife.bind(this, mRootView);

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

    @OnClick(R.id.dictionary_button_search)
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
        return mDictionaryOwner.getText().toString();
    }

    private String getSelectedLastUpdater() { return mDictionaryLastUpdater.getText().toString(); }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_dictionary_owner).equals(selected)) {
            return CustomDictionary.FIELD_OWNER;
        } else if (getString(R.string.sort_by_dictionary_last_updater).equals(selected)) {
            return CustomDictionary.FIELD_LAST_UPDATER;
        } else if (getString(R.string.sort_by_dictionary_most_recent_update).equals(selected)) {
            return CustomDictionary.FIELD_LAST_UPDATE;
        } else if (getString(R.string.sort_by_dictionary_title).equals(selected)) {
            // This is the default sort, so return nothing.
            return null;
        }
        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_dictionary_owner).equals(selected)) {
            return Query.Direction.ASCENDING;
        } else if (getString(R.string.sort_by_dictionary_last_updater).equals(selected)) {
            return Query.Direction.ASCENDING;
        } else if (getString(R.string.sort_by_dictionary_most_recent_update).equals(selected)) {
            return Query.Direction.DESCENDING;
        } else if (getString(R.string.sort_by_dictionary_title).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mDictionaryOwner.getText().clear();
            mDictionaryLastUpdater.getText().clear();
            mSortSpinner.setSelection(0);
        }
    }

    public DictionaryFilters getFilters() {
        DictionaryFilters filters = new DictionaryFilters();

        if (mRootView != null) {
            filters.setOwner(getSelectedOwner());
            filters.setLastUpdater(getSelectedLastUpdater());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }
}
