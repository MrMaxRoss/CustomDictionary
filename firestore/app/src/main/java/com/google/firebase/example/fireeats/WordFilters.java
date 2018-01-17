package com.google.firebase.example.fireeats;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.example.fireeats.model.PartOfSpeech;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class WordFilters {

    private String owner = null;
    private String lastUpdater = null;
    private PartOfSpeech partOfSpeech = null;
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
        return partOfSpeech != null;
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

    public boolean hasLastUpdater() { return !(TextUtils.isEmpty(lastUpdater));}

    public String getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
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

        if (!hasOwner() && !hasLastUpdater() && !hasPartOfSpeech()) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_words));
            desc.append("</b>");
        }

        if (hasOwner()) {
            desc.append(String.format(context.getString(R.string.owned_by_format), owner));
        }

        if (hasLastUpdater()) {
            if (desc.length() > 0) {
                desc.append(" and ");
            }
            desc.append(String.format(context.getString(R.string.last_updated_by_format), lastUpdater));
        }

        if (hasPartOfSpeech()) {
            if (desc.length() > 0) {
                desc.append(" and ");
            }
            desc.append(String.format(context.getString(R.string.part_of_speech_format), partOfSpeech));
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Word.FIELD_OWNER.equals(sortBy)) {
            return context.getString(R.string.sort_by_word_owner);
        } else if (Word.FIELD_LAST_UPDATER.equals(sortBy)) {
            return context.getString(R.string.sort_by_word_last_updater);
        } else if (Word.FIELD_PART_OF_SPEECH.equals(sortBy)) {
            return context.getString(R.string.sort_by_word_last_updater);
        } else {
            return context.getString(R.string.sort_by_word_id);
        }
    }
}
