package com.google.firebase.example.fireeats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxr on 1/5/18.
 */

public enum PartOfSpeech {
    ADJECTIVE,
    ADVERB,
    ARTICLE,
    CONJUNCTION,
    INTERJECTION,
    NOUN,
    PREPOSITION,
    PRONOUN,
    VERB;

    private final String display;

    PartOfSpeech() {
        display = name().toLowerCase();
    }

    public String getDisplay() {
        return display;
    }

    static final List<String> displayStrings = new ArrayList<>();

    static {
        for (PartOfSpeech pos : values()) {
            displayStrings.add(pos.display);
        }
    }
    public static List<String> displayStrings() {
        return displayStrings;
    }
}
