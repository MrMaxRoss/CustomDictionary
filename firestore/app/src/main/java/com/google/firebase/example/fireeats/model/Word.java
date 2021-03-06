package com.google.firebase.example.fireeats.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxr on 11/20/17.
 */

public class Word implements Comparable<Word> {

    public static final String FIELD_ID = "__name__";
    public static final String FIELD_OWNER = "owner";
    public static final String FIELD_OWNER_EMAIL = "ownerEmail";
    public static final String FIELD_LAST_UPDATER = "last_updater";
    public static final String FIELD_LAST_UPDATE = "last_update";
    public static final String FIELD_DEFINITION = "definition";
    public static final String FIELD_PART_OF_SPEECH = "part_of_speech";
    private static final String FIELD_EXAMPLE_SENTENCE = "example_sentence";
    public static final String FIELD_CUSTOM_DICT_NAME = "customer_dict_name";

    String customDictName;
    String id;
    String definition;
    String exampleSentence;
    PartOfSpeech partOfSpeech;
    String owner;
    String ownerEmail;
    String lastUpdater;
    Date lastUpdate;

    public Word() {
    }

    public Word(String dictId, String id, String definition, String exampleSentence,
                PartOfSpeech partOfSpeech, String owner, String ownerEmail, String lastUpdater) {
        this(dictId, id, definition, exampleSentence, partOfSpeech, owner, ownerEmail, lastUpdater, null);
    }

    public Word(String id, String definition, String exampleSentence, PartOfSpeech partOfSpeech,
                String owner, String ownerEmail) {
        this(null, id, definition, exampleSentence, partOfSpeech, owner, ownerEmail, null);
    }

    Word(String dictId, String id, String definition, String exampleSentence, PartOfSpeech partOfSpeech,
         String owner, String ownerEmail, String lastUpdater, Date lastUpdate) {
        this.customDictName = dictId;
        this.id = id;
        this.definition = definition;
        this.exampleSentence = exampleSentence;
        this.partOfSpeech = partOfSpeech;
        this.owner = owner;
        this.ownerEmail = ownerEmail;
        this.lastUpdater = lastUpdater;
        this.lastUpdate = lastUpdate;
    }


    public String getCustomDictName() {
        return customDictName;
    }

    public void setCustomDictName(String customDictName) {
        this.customDictName = customDictName;
    }

    public String getDefinition() {
        return definition;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setDefinition(String newDefinition) {
        this.definition = newDefinition;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
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

    @Override
    public String toString() {
        return "Word{" +
                "customDictName='" + customDictName + '\'' +
                ", id='" + id + '\'' +
                ", definition='" + definition + '\'' +
                ", exampleSentence='" + exampleSentence + '\'' +
                ", partOfSpeech=" + partOfSpeech +
                ", owner='" + owner + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", lastUpdater='" + lastUpdater + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Word word) {
        return getId().compareTo(word.getId());
    }

    public Map<String, Object> toWordMap(boolean useServerTimestamp) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_OWNER, getOwner());
        map.put(FIELD_OWNER_EMAIL, getOwnerEmail());
        map.put(FIELD_LAST_UPDATER, getLastUpdater());
        map.put(FIELD_PART_OF_SPEECH, getPartOfSpeech().name());
        map.put(FIELD_DEFINITION, getDefinition());
        map.put(FIELD_EXAMPLE_SENTENCE, getExampleSentence());
        map.put(FIELD_CUSTOM_DICT_NAME, getCustomDictName());

        if (useServerTimestamp) {
            map.put(FIELD_LAST_UPDATE, FieldValue.serverTimestamp());
        } else {
            map.put(FIELD_LAST_UPDATE, getLastUpdate());
        }
        return map;
    }

    public static Word fromDocumentSnapshot(DocumentSnapshot snapshot) {
        return new Word(
                (String) snapshot.get(FIELD_CUSTOM_DICT_NAME),
                snapshot.getId(),
                (String) snapshot.get(FIELD_DEFINITION),
                (String) snapshot.get(FIELD_EXAMPLE_SENTENCE),
                PartOfSpeech.valueOf((String) snapshot.get(FIELD_PART_OF_SPEECH)),
                (String) snapshot.get(FIELD_OWNER),
                (String) snapshot.get(FIELD_OWNER_EMAIL),
                (String) snapshot.get(FIELD_LAST_UPDATER),
                (Date) snapshot.get(FIELD_LAST_UPDATE));

    }
}
