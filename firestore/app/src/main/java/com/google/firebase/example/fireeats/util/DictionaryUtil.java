package com.google.firebase.example.fireeats.util;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.example.fireeats.model.CustomDictionary;
import com.google.firebase.example.fireeats.model.PartOfSpeech;
import com.google.firebase.example.fireeats.model.Word;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for Dictionaries.
 */
public class DictionaryUtil {

    private static final String TAG = "DictionaryUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String[] NAME_FIRST_WORDS = {
            "Foo",
            "Bar",
            "Baz",
            "Qux",
            "Fire",
            "Sam's",
            "World Famous",
            "Google",
            "The Best",
    };

    private static final String[] USERS = {
            "Max",
            "Daphne",
            "Violet",
            "Sabrina",
            "Liza",
            "Abel",
            "Romulo",
            "Lyla",
            "Alden",
            "Carmen",
            "Donald",
            "Patti",
    };

    private static final String[] WORDS = {
            "foopoo",
            "chirf",
            "skoink",
            "noice",
            "blar",
            "yam",
            "abope",
            "afofe",
            "schwack",
            "stulk",
            "furn",
            "emoobis",
    };

    /**
     * Create a random CustomDictionary POJO.
     */
    public static CustomDictionary getRandomDictionary(Context context) {
        Random random = new Random();
        List<Word> words = getRandomWordList(random.nextInt(10));
        String randomOwner = getRandomUser(random);
        CustomDictionary dict = new CustomDictionary(
                getRandomName(random),
                getRandomUser(random),
                randomOwner + "@sortedunderbelly.com",
                getRandomUser(random),
                getRandomDate(random),
                words);
        return dict;
    }

    private static Date getRandomDate(Random random) {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, random.nextInt(365));
        cal.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        return cal.getTime();
    }

    /**
     * Delete all documents in a collection. Uses an Executor to perform work on a background
     * thread. This does *not* automatically discover and delete subcollections.
     */
    private static Task<Void> deleteCollection(final CollectionReference collection,
                                               final int batchSize,
                                               Executor executor) {

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Get the first batch of documents in the collection
                Query query = collection.orderBy("__name__").limit(batchSize);

                // Get a list of deleted documents
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);

                // While the deleted documents in the last batch indicate that there
                // may still be more documents in the collection, page down to the
                // next batch and delete again
                while (deleted.size() >= batchSize) {
                    // Move the query cursor to start after the last doc in the batch
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = collection.orderBy("__name__")
                            .startAfter(last.getId())
                            .limit(batchSize);

                    deleted = deleteQueryBatch(query);
                }

                return null;
            }
        });

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    private static List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (DocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }

    /**
     * Delete all restaurants.
     */
    public static Task<Void> deleteAll() {
        CollectionReference ref = FirebaseFirestore.getInstance().collection("dictionaries");
        return deleteCollection(ref, 25, EXECUTOR);
    }

    private static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " Dictionary" + random.nextInt(100);
    }

    private static String getRandomUser(Random random) {
        return getRandomString(USERS, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    /**
     * Get a list of random Word POJOs.
     */
    private static List<Word> getRandomWordList(int length) {
        List<Word> result = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            result.add(getRandomWord());
        }

        return result;
    }

    /**
     * Create a random Word POJO.
     */
    private static Word getRandomWord() {
        Random random = new Random();

        String wordName = WORDS[random.nextInt(WORDS.length)] + random.nextInt(100);
        String owner = getRandomUser(random);
        return new Word(null, wordName,
                "This is the definition",
                String.format("One time I went to the park and saw a %s.", wordName),
                getRandomPartOfSpeech(random), owner, owner + "@sortedunderbelly.com",
                owner);
    }

    private static PartOfSpeech getRandomPartOfSpeech(Random random) {
        return PartOfSpeech.values()[random.nextInt(PartOfSpeech.values().length)];
    }
}
