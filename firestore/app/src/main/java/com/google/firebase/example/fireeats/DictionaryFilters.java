package com.google.firebase.example.fireeats;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class DictionaryFilters {

    private String owner = null;
    private String lastUpdater = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public DictionaryFilters() {}

    public static DictionaryFilters getDefault() {
        DictionaryFilters filters = new DictionaryFilters();
//        filters.setSortBy(CustomDictionary.FIELD_TITLE); // not sure if this works
//        filters.setSortDirection(Query.Direction.DESCENDING);
        return filters;
    }

    public boolean hasOwner() {
        return !(TextUtils.isEmpty(owner));
    }

    public boolean hasLastUpdater() { return !(TextUtils.isEmpty(lastUpdater)); }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLastUpdater() { return lastUpdater; }

    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
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

        if (!hasOwner() && !hasLastUpdater()) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_dictionaries));
            desc.append("</b>");
        }

        if (hasOwner()) {
            desc.append(String.format(context.getString(R.string.owned_by_format), owner));
        }

        if (hasOwner() && hasLastUpdater()) {
            desc.append(" and ");
        }

        if (hasLastUpdater()) {
            desc.append(String.format(context.getString(R.string.last_updated_by_format), lastUpdater));
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (CustomDictionary.FIELD_OWNER.equals(sortBy)) {
            return context.getString(R.string.sort_by_dictionary_owner);
        } else if (CustomDictionary.FIELD_LAST_UPDATER.equals(sortBy)) {
            return context.getString(R.string.sort_by_dictionary_last_updater);
        } else {
            return context.getString(R.string.sort_by_dictionary_title);
        }
    }
}
