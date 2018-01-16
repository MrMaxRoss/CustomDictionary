package com.google.firebase.example.fireeats;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fireeats.adapter.DictionaryAdapter;
import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.example.fireeats.util.ActivityUtil;
import com.google.firebase.example.fireeats.util.DictionaryUtil;
import com.google.firebase.example.fireeats.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        DictionaryFilterDialogFragment.FilterListener,
        DictionaryAdapter.OnDictionarySelectedListener,
        DictionaryDialogCreateFragment.DictionaryListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.text_current_search)
    TextView mCurrentSearchView;

    @BindView(R.id.text_current_sort_by)
    TextView mCurrentSortByView;

    @BindView(R.id.recycler_dictionaries)
    RecyclerView mDictionariesRecycler;

    @BindView(R.id.view_empty)
    ViewGroup mEmptyView;

    DictionaryDialogCreateFragment mDictionaryCreateFragment;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private DictionaryFilterDialogFragment mFilterDialog;
    private DictionaryAdapter mAdapter;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} dictionaries
        mQuery = mFirestore.collection("dictionaries")
                .orderBy("title", Query.Direction.ASCENDING)
                .limit(LIMIT);

        // RecyclerView
        mAdapter = new DictionaryAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mDictionariesRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mDictionariesRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mDictionariesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mDictionariesRecycler.setAdapter(mAdapter);

        // Filter Dialog
        mFilterDialog = new DictionaryFilterDialogFragment();
        mDictionaryCreateFragment = new DictionaryDialogCreateFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Apply filters
        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_dictionaries:
                onAddDictionariesClicked();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
            case R.id.menu_delete:
                DictionaryUtil.deleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @OnClick(R.id.filter_bar)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), DictionaryFilterDialogFragment.TAG);
    }

    @OnClick(R.id.button_clear_filter)
    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(DictionaryFilters.getDefault());
    }

    @Override
    public void onDictionarySelected(DocumentSnapshot dict) {
        // Go to the details page for the selected dictionary
        Intent intent = new Intent(this, DictionaryDetailActivity.class);
        intent.putExtra(DictionaryDetailActivity.KEY_DICTIONARY_ID, dict.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onFilter(DictionaryFilters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("dictionaries");

        // Equality filters on owner or last updater
        if (filters.hasOwner()) {
            query = query.whereEqualTo(CustomDictionary.FIELD_OWNER, filters.getOwner());
        } else if (filters.hasLastUpdater()) {
            query = query.whereEqualTo(CustomDictionary.FIELD_LAST_UPDATER, filters.getLastUpdater());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mAdapter.setQuery(query);

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
        mCurrentSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private void onAddDictionariesClicked() {
        // Add a bunch of dictionaries
        WriteBatch batch = mFirestore.batch();
        for (int i = 0; i < 10; i++) {
            // Create random dictionaries and words
            CustomDictionary randomDictionary = DictionaryUtil.getRandomDictionary(this);
            DocumentReference dictRef = mFirestore.collection("dictionaries").document(randomDictionary.getName());

            // Add dictionary
            batch.set(dictRef, randomDictionary.toDocumentMap());

            // Add words to subcollection
            for (Word word : randomDictionary.getWordMap().values()) {
                batch.set(dictRef.collection("words").document(word.getId()), word.toWordMap());
            }
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Write batch succeeded.");
                } else {
                    Log.w(TAG, "write batch failed.", task.getException());
                }
            }
        });
    }

    @OnClick(R.id.fab_create_dictionary)
    public void onCreateDictionaryClicked(View view) {
        mDictionaryCreateFragment.show(getSupportFragmentManager(), DictionaryDialogCreateFragment.TAG);
    }

    @Override
    public void onDictionary(CustomDictionary dict, final DictionaryDialogFragment frag) {
        DocumentReference dictRef = mFirestore.collection(
                CustomDictionary.COLLECTION_DICTIONARIES).document(dict.getName());
        Map<String, Object> dictMap = dict.toDocumentMap();
        dictRef.set(dictMap)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Dictionary added");

                        // Hide keyboard and scroll to top
                        ActivityUtil.hideKeyboard(MainActivity.this);
                        mDictionariesRecycler.smoothScrollToPosition(0);
                        frag.onSuccess();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add dictionary failed", e);

                        // Show failure message and hide keyboard
                        ActivityUtil.hideKeyboard(MainActivity.this);
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add dictionary",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
