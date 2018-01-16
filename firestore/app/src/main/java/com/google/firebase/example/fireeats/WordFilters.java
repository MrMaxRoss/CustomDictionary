package com.google.firebase.example.fireeats;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.example.fireeats.util.RestaurantUtil;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class WordFilters {

    private String owner = null;
    private String partOfSpeech = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public WordFilters() {}

    public static WordFilters getDefault() {
        WordFilters filters = new WordFilters();
//        filters.setSortBy(Word.FIELD_ID);
//        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasOwner() {
        return !(TextUtils.isEmpty(owner));
    }

    public boolean hasPartOfSpeech() {
        return !(TextUtils.isEmpty(partOfSpeech));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (owner == null && partOfSpeech == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_words));
            desc.append("</b>");
        }

        if (owner != null) {
            desc.append("<b>");
            desc.append(owner);
            desc.append("</b>");
        }

        if (partOfSpeech != null) {
            desc.append("<b>");
            desc.append(partOfSpeech);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Word.FIELD_OWNER.equals(sortBy)) {
            return context.getString(R.string.sort_by_word_owner);
        } else {
            return context.getString(R.string.sort_by_word_id);
        }
    }
}
