package com.google.firebase.example.fireeats.model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by maxr on 11/20/17.
 */

public class CustomDictionary {

    public static final String FIELD_TITLE = "__name__";
    public static final String FIELD_OWNER = "owner";
    public static final String FIELD_OWNER_EMAIL = "ownerEmail";
    public static final String FIELD_LAST_UPDATER = "lastUpdater";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
    public static final String COLLECTION_DICTIONARIES = "dictionaries";
    public static final String COLLECTION_WORDS = "words";

    private String name;
    private String owner;
    private String ownerEmail;
    private String lastUpdater;
    private Date lastUpdate;
    private Map<String, Word> wordMap = new TreeMap<>();

    public CustomDictionary() {

    }

    public CustomDictionary(String name, String owner, String ownerEmail, List<Word> words) {
        this(name, owner, owner, null, null, words);
    }

    public CustomDictionary(String name, String owner, String ownerEmail, String lastUpdater,
                            Date lastUpdate, List<Word> words) {
        this.name = name;
        this.owner = owner;
        this.ownerEmail = ownerEmail;
        this.lastUpdater = lastUpdater;
        this.lastUpdate = lastUpdate;
        for (Word w : words) {
            w.setCustomDictName(name);
            wordMap.put(w.getId(), w);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Map<String, Word> getWordMap() {
        return wordMap;
    }

    public void setWordMap(Map<String, Word> words) {
        this.wordMap = words;
    }

    @Override
    public String toString() {
        return "CustomDictionary{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", owner='" + ownerEmail + '\'' +
                ", lastUpdater='" + lastUpdater + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", wordMap=" + wordMap +
                '}';
    }

    public Word findWord(String word) {
        return wordMap.get(word);
    }

    public static CustomDictionary fromDocumentSnapshot(DocumentSnapshot snapshot) {
        return new CustomDictionary(
                snapshot.getId(),
                snapshot.getString(FIELD_OWNER),
                snapshot.getString(FIELD_OWNER_EMAIL),
                snapshot.getString(FIELD_LAST_UPDATER),
                snapshot.getDate(FIELD_LAST_UPDATE),
                new ArrayList<Word>());
    }

    public Map<String, Object> toDocumentMap(boolean useServerTimestamp) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_OWNER, getOwner());
        map.put(FIELD_OWNER_EMAIL, getOwnerEmail());
        map.put(FIELD_LAST_UPDATER, getLastUpdater());
        if (useServerTimestamp) {
            map.put(FIELD_LAST_UPDATE, FieldValue.serverTimestamp());
        } else {
            map.put(FIELD_LAST_UPDATE, getLastUpdate());
        }
        return map;
    }
}
