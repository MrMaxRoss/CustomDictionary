package com.google.firebase.example.fireeats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.example.fireeats.adapter.WordAdapter;
import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.example.fireeats.util.ActivityUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DictionaryDetailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, WordDialogCreateFragment.WordListener,
        WordAdapter.OnWordSelectedListener {

    private static final String TAG = "DictionaryDetail";

    public static final String KEY_DICTIONARY_ID = "key_dictionary_id";

    @BindView(R.id.dictionary_title)
    TextView mTitleView;

    @BindView(R.id.dictionary_owner)
    TextView mOwnerView;

    @BindView(R.id.recycler_words)
    RecyclerView mWordsRecycler;

    @BindView(R.id.view_empty_words)
    ViewGroup mEmptyView;


    private WordDialogCreateFragment mWordDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mDictionaryRef;
    private ListenerRegistration mDictionaryRegistration;

    private WordAdapter mWordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_detail);
        ButterKnife.bind(this);

        // Get dictionary ID from extras
        String dictionaryId = getIntent().getExtras().getString(KEY_DICTIONARY_ID);
        if (dictionaryId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_DICTIONARY_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the dictionary
        mDictionaryRef = mFirestore.collection(CustomDictionary.COLLECTION_DICTIONARIES).document(dictionaryId);

        // Get words
        Query wordsQuery = mDictionaryRef
                .collection(CustomDictionary.COLLECTION_WORDS)
                .limit(50);

        // RecyclerView
        mWordAdapter = new WordAdapter(wordsQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mWordsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mWordsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };
        mWordsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mWordsRecycler.setAdapter(mWordAdapter);

        mWordDialog = new WordDialogCreateFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mWordAdapter.startListening();
        mDictionaryRegistration = mDictionaryRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mWordAdapter.stopListening();

        if (mDictionaryRegistration != null) {
            mDictionaryRegistration.remove();
            mDictionaryRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Dictionary document ({@link #mDictionaryRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "dictionary:onEvent", e);
            return;
        }

        onDictionaryLoaded(CustomDictionary.fromDocumentSnapshot(snapshot));
    }

    private void onDictionaryLoaded(CustomDictionary dict) {
        mTitleView.setText(dict.getName());
        mOwnerView.setText(dict.getOwner());
    }

    @OnClick(R.id.dictionary_button_back)
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_word_create_dialog)
    public void onAddWordClicked(View view) {
        mWordDialog.show(getSupportFragmentManager(), WordDialogCreateFragment.TAG);
    }

    @Override
    public void onWord(Word word, final WordDialogFragment frag) {
        // In a transaction, add the new word and update the aggregate totals
        addWord(mDictionaryRef, word)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Word added");

                        // Hide keyboard and scroll to top
                        ActivityUtil.hideKeyboard(DictionaryDetailActivity.this);
                        mWordsRecycler.smoothScrollToPosition(0);
                        frag.onSuccess();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add word failed", e);

                        // Show failure message and hide keyboard
                        ActivityUtil.hideKeyboard(DictionaryDetailActivity.this);
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add word",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private Task<Void> addWord(final DocumentReference dictionaryRef, final Word word) {
        // Create reference for new word, for use inside the transaction
        final DocumentReference wordRef = dictionaryRef.collection("words").document(word.getId());

        // In a transaction, add the new word and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Map<String, Object> wordMap = word.toWordMap();
                CustomDictionary dict = transaction.get(dictionaryRef).toObject(CustomDictionary.class);

                // Compute new number of words
//                int newNumRatings = dict.getNumberOfWords() + 1;

//                // Compute new average rating
//                double oldRatingTotal = restaurant.getAvgRating() * restaurant.getNumRatings();
//                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;
//
//                // Set new restaurant info
//                restaurant.setNumRatings(newNumRatings);
//                restaurant.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(dictionaryRef, dict);
                transaction.set(wordRef, wordMap);

                return null;
            }
        });
    }

    @Override
    public void onWordSelected(DocumentSnapshot word) {
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra(DictionaryDetailActivity.KEY_DICTIONARY_ID, (String) word.get(Word.FIELD_CUSTOM_DICT_NAME));
        intent.putExtra(WordDetailActivity.KEY_WORD_ID, (String) word.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
