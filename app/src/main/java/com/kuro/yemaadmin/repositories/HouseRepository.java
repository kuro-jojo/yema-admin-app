package com.kuro.yemaadmin.repositories;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.Query;
import com.algolia.search.models.indexing.SearchResult;
import com.algolia.search.models.settings.IndexSettings;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kuro.androidutils.logging.Logger;
import com.kuro.yemaadmin.BuildConfig;
import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.data.model.House;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * The type House repository.
 */
public class HouseRepository {
    private final OnFirestoreTaskComplete onFirestoreTaskComplete;
    private final SearchIndex<House> algoliaSearchIndex;
    private final CollectionReference mRootCollection;
    private final FirebaseFirestore mFirestore;
    private OnHouseCreated onHouseCreated;

    /**
     * Instantiates a new House repository.
     *
     * @param onFirestoreTaskComplete the on firestore task complete
     */
    public HouseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        mFirestore = FirebaseFirestore.getInstance();
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
        mRootCollection = mFirestore.collection("houses");

        // algolia configuration
        SearchClient algoliaSearchClient = DefaultSearchClient.create(BuildConfig.ALGOLIA_APP_KEY, BuildConfig.ALGOLIA_API_KEY);
        String INDEX_NAME = "yema";
        algoliaSearchIndex = algoliaSearchClient.initIndex(INDEX_NAME, House.class);
        algoliaSearchIndex.setSettingsAsync(
                new IndexSettings().setSearchableAttributes(Collections.singletonList(
                        "location"
                ))
        );
    }

    public void addHouse(House house, String managedCountry) {
        mRootCollection.document(managedCountry)
                .collection(house.getHouseType().type.toLowerCase())
                .add(house)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        house.setHouseId(task.getResult().getId());
                        house.setUploadedAt(new Timestamp(new Date()));
                        mRootCollection.document(managedCountry)
                                .collection(house.getHouseType().type.toLowerCase())
                                .document(house.getHouseId()).set(house)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        onHouseCreated.processSuccess();
                                        onFirestoreTaskComplete.houseDataLoaded(house);
                                        Logger.log("house updated successfully");
                                    } else {
                                        onHouseCreated.processFailure();
                                        Logger.error("error while updating the house " + Objects.requireNonNull(task1.getException()).getMessage());
                                    }
                                });
                    } else {
                        onHouseCreated.processFailure();
                        Logger.error("error while adding the house " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }


    /**
     * Gets house by house id.
     *
     * @param houseId the house id
     */
    public void getHouseByHouseId(String houseId) {
        getHouse("objectID:" + houseId);
    }


    private void getHouse(String filter) {
        CompletableFuture.runAsync(() -> {
            try {
                SearchResult<House> result = algoliaSearchIndex.searchAsync(new Query()
                                .setFilters(filter))
                        .join();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!result.getHits().isEmpty()) {
                        onFirestoreTaskComplete.houseDataLoaded(result.getHits().get(0));
                    }
                });
            } catch (Exception e) {
                onFirestoreTaskComplete.onError(e);
                Logger.error("ALGOLIA", "err : " + e.getMessage());
            }
        });
    }

    /**
     * Gets all houses.
     */
    public void getAllHouses() {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (HouseType houseType : HouseType.values()) {
            Task<QuerySnapshot> task = mFirestore.collectionGroup(houseType.typeLowerCase).get();
            tasks.add(task);
        }

        getAllHouses(tasks);
    }

    // TODO : get houses by newest
    private void getAllHouses(List<Task<QuerySnapshot>> tasks) {

        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            List<House> houses = new ArrayList<>();

            for (Task<?> task : allTasks.getResult()) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = (QuerySnapshot) task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        houses.addAll(querySnapshot.toObjects(House.class));
                    }
                } else {
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }

            try {
                onFirestoreTaskComplete.housesDataLoaded(houses);
            } catch (RuntimeException e) {
                onFirestoreTaskComplete.onRuntimeError(e);
                Logger.error("AndroidRuntime", "err : " + e.getMessage());
            }
        });
    }

    /**
     * Gets houses by the house type.
     *
     * @param houseType the house type
     */
    public void getHousesByHouseType(HouseType houseType) {
        mFirestore.collectionGroup(houseType.typeLowerCase).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    onFirestoreTaskComplete.housesDataLoaded(task.getResult().toObjects(House.class));
                } catch (RuntimeException e) {
                    onFirestoreTaskComplete.onRuntimeError(e);
                }
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }

    public void getHousesByHouseTypeAndCountry(HouseType houseType, String managedCountry) {
        mRootCollection.document(managedCountry).collection(houseType.typeLowerCase).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    onFirestoreTaskComplete.housesDataLoaded(task.getResult().toObjects(House.class));
                } catch (RuntimeException e) {
                    onFirestoreTaskComplete.onRuntimeError(e);
                }
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }

    public void getAllHousesByCountry(String managedCountry) {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (HouseType houseType : HouseType.values()) {
            Task<QuerySnapshot> task = mRootCollection.document(managedCountry).collection(houseType.typeLowerCase).get();
            tasks.add(task);
        }

        getAllHousesByCountry(tasks);
    }

    // TODO : get houses by newest
    private void getAllHousesByCountry(List<Task<QuerySnapshot>> tasks) {

        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            List<House> houses = new ArrayList<>();

            for (Task<?> task : allTasks.getResult()) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = (QuerySnapshot) task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        houses.addAll(querySnapshot.toObjects(House.class));
                    }
                } else {
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }

            try {
                onFirestoreTaskComplete.housesDataLoaded(houses);
            } catch (RuntimeException e) {
                onFirestoreTaskComplete.onRuntimeError(e);
                Logger.error("AndroidRuntime", "err : " + e.getMessage());
            }
        });
    }

    public void setOnHouseCreated(OnHouseCreated onHouseCreated) {
        this.onHouseCreated = onHouseCreated;
    }

    /**
     * The interface On firestore task complete.
     */
    public interface OnFirestoreTaskComplete {
        void housesDataLoaded(List<House> houseList);

        void houseDataLoaded(House house);

        void onError(Exception e);

        void onRuntimeError(RuntimeException e);
    }

    public interface OnHouseCreated {
        void processSuccess();

        void processFailure();
    }
}
