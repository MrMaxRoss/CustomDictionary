package com.google.firebase.example.fireeats.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.google.firebase.example.fireeats.DictionaryFilters;

/**
 * ViewModel for {@link com.google.firebase.example.fireeats.MainActivity}.
 */

public class MainActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;
    private DictionaryFilters mFilters;

    public MainActivityViewModel() {
        mIsSigningIn = false;
        mFilters = DictionaryFilters.getDefault();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public DictionaryFilters getFilters() {
        return mFilters;
    }

    public void setFilters(DictionaryFilters mFilters) {
        this.mFilters = mFilters;
    }
}
