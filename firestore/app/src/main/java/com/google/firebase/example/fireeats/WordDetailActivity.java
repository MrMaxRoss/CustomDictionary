package com.google.firebase.example.fireeats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.example.fireeats.util.ActivityUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.example.fireeats.DictionaryDetailActivity.KEY_DICTIONARY_ID;

public class WordDetailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, WordDialogCreateFragment.WordListener {

    private static final String TAG = "WordDetail";

    public static final String KEY_WORD_ID = "key_word_id";

    @BindView(R.id.word_name)
    TextView nameView;

    @BindView(R.id.word_owner)
    TextView ownerView;

    @BindView(R.id.word_last_update_text)
    TextView lastUpdateView;

    @BindView(R.id.word_definition)
    TextView definitionView;

    @BindView(R.id.word_example_sentence)
    TextView exampleSentenceView;

    @BindView(R.id.word_part_of_speech)
    TextView partOfSpeechView;

    private FirebaseFirestore mFirestore;
    private DocumentReference mWordRef;
    private ListenerRegistration mWordRegistration;
    private WordDialogEditFragment mWordDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        ButterKnife.bind(this);

        // Get dictionary ID from extras
        String dictionaryId = getIntent().getExtras().getString(KEY_DICTIONARY_ID);
        if (dictionaryId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_DICTIONARY_ID);
        }
        String wordId = getIntent().getExtras().getString(KEY_WORD_ID);
        if (wordId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_WORD_ID);
        }
        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the word
        mWordRef = mFirestore.collection(CustomDictionary.COLLECTION_DICTIONARIES)
                .document(dictionaryId).collection(CustomDictionary.COLLECTION_WORDS)
                .document(wordId);

        mWordDialog = new WordDialogEditFragment();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Word document ({@link #mWordRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "word:onEvent", e);
            return;
        }
        if (snapshot.exists()) {
            onWordLoaded(Word.fromDocumentSnapshot(snapshot));
        } else {
            // Word was deleted, nothing to do
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mWordRegistration = mWordRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mWordRegistration != null) {
            mWordRegistration.remove();
            mWordRegistration = null;
        }
    }

    private void onWordLoaded(Word word) {
        nameView.setText(word.getId());
        ownerView.setText(word.getOwner());
        definitionView.setText(word.getDefinition());
        exampleSentenceView.setText((word.getExampleSentence()));
        partOfSpeechView.setText(String.format("(%s)", word.getPartOfSpeech().getDisplay()));
        lastUpdateView.setText(getString(R.string.message_word_last_update_format,
                word.getLastUpdater(),
                word.getLastUpdate()));

        mWordDialog.setWord(word); // this is probably the wrong way to do it
    }

    @OnClick(R.id.word_button_back)
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_word_edit_dialog)
    public void onEditWordClicked(View view) {
        mWordDialog.show(getSupportFragmentManager(), WordDialogCreateFragment.TAG);
    }

    @OnClick(R.id.fab_delete_word)
    public void onDeleteWordClicked(final View view) {
        mWordRef.delete()
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Word deleted");
                        onBackArrowClicked(view);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Delete word failed", e);

                        Snackbar.make(findViewById(android.R.id.content), "Failed to delete word",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onWord(Word word) {
        // Edit the word
        mWordRef.set(word.toWordMap(true))
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Word edited");

                        // Hide keyboard and scroll to top
                        ActivityUtil.hideKeyboard(WordDetailActivity.this);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Edit word failed", e);

                        // Show failure message and hide keyboard
                        ActivityUtil.hideKeyboard(WordDetailActivity.this);
                        Snackbar.make(findViewById(android.R.id.content), "Failed to edit word",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

}
