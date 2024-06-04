package com.kuro.yemaadmin.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuro.yemaadmin.data.model.User;

/**
 * The type User repository.
 */
public class UserRepository {

    private final CollectionReference mCollectionReference;
    private final OnFirestoreTaskComplete onFirestoreTaskComplete;

    public UserRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mCollectionReference = mFirestore.collection("adminUsers");
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public void getUser(String uid) {
        mCollectionReference.document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    onFirestoreTaskComplete.userDataLoaded(task.getResult().toObject(User.class));
                } catch (RuntimeException e) {
                    onFirestoreTaskComplete.onRuntimeError(e);
                }
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }

    public interface OnFirestoreTaskComplete {

        void userDataLoaded(User user);

        void onError(Exception e);

        void onRuntimeError(RuntimeException e);
    }
}
