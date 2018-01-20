package com.google.firebase.example.fireeats.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView adapter for a list of {@link Word}.
 */
public class WordAdapter extends FirestoreAdapter<WordAdapter.ViewHolder> {

    public interface OnWordSelectedListener {

        void onWordSelected(DocumentSnapshot word);

    }

    private final WordAdapter.OnWordSelectedListener mListener;

    public WordAdapter(Query query, OnWordSelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.word_item_name)
        TextView nameView;

        @BindView(R.id.word_item_owner)
        TextView ownerView;

        @BindView(R.id.word_item_last_updater)
        TextView lastUpdaterView;

        @BindView(R.id.word_item_part_of_speech)
        TextView partOfSpeechView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(final DocumentSnapshot snapshot,
                         final WordAdapter.OnWordSelectedListener listener) {
            Word word = Word.fromDocumentSnapshot(snapshot);
            nameView.setText(word.getId());
            ownerView.setText(word.getOwner());
            partOfSpeechView.setText(word.getPartOfSpeech().getDisplay());
            lastUpdaterView.setText(itemView.getContext().getString(R.string.message_word_last_update_format,
                    word.getLastUpdater(),
                    word.getLastUpdate()));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onWordSelected(snapshot);
                    }
                }
            });

        }
    }
}
