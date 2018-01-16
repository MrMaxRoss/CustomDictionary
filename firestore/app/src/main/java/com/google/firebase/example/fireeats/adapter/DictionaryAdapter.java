package com.google.firebase.example.fireeats.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maxr on 1/5/18.
 */

public class DictionaryAdapter extends FirestoreAdapter<DictionaryAdapter.ViewHolder> {

    public interface OnDictionarySelectedListener {

        void onDictionarySelected(DocumentSnapshot dictionary);

    }

    private final DictionaryAdapter.OnDictionarySelectedListener mListener;

    public DictionaryAdapter(Query query, DictionaryAdapter.OnDictionarySelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public DictionaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DictionaryAdapter.ViewHolder(inflater.inflate(R.layout.item_dictionary, parent, false));
    }

    @Override
    public void onBindViewHolder(DictionaryAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dictionary_item_image)
        ImageView imageView;

        @BindView(R.id.dictionary_item_title)
        TextView titleView;

        @BindView(R.id.dictionary_item_owner)
        TextView ownerView;

        @BindView(R.id.dictionary_item_last_updater)
        TextView lastUpdaterView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final DictionaryAdapter.OnDictionarySelectedListener listener) {


            CustomDictionary dict = CustomDictionary.fromDocumentSnapshot(snapshot);
            Resources resources = itemView.getResources();

            // Load image
//            Glide.with(imageView.getContext())
//                    .load(dictionary.getPhoto())
//                    .into(imageView);

            titleView.setText(dict.getName());
            ownerView.setText(dict.getOwner());
            lastUpdaterView.setText(dict.getLastUpdater());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDictionarySelected(snapshot);
                    }
                }
            });
        }

    }

}
