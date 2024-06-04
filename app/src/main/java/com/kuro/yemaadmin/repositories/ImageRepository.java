//package com.kuro.yemaadmin.repositories;
//
//import android.net.Uri;
//
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.Tasks;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.kuro.androidutils.logging.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
///**
// * The type User repository.
// */
//public class ImageRepository {
//
//    public static final String HOUSE_PATH = "houses/";
//    private final StorageReference mStorageReference;
//    private final OnImageUploaded onImageUploaded;
//
//    public ImageRepository(OnImageUploaded onImageUploaded) {
//        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
//
//        mStorageReference = FirebaseStorage.getInstance().getReference();
//        this.onImageUploaded = onImageUploaded;
//    }
//
//
//    public void saveImages(Uri[] listOfImagesUri, ArrayList<String> listOfImages) {
//        List<UploadTask> tasks = new ArrayList<>();
//        StorageReference houseRef;
//        UploadTask uploadTask;
//        for (Uri uri : listOfImagesUri) {
//            String newUri = uri.getLastPathSegment() + UUID.randomUUID();
//            houseRef = mStorageReference.child(HOUSE_PATH + newUri);
//            uploadTask = houseRef.putFile(uri);
//            listOfImages.add(newUri);
//            tasks.add(uploadTask);
//        }
//        uploadImages(tasks, listOfImages);
//    }
//
//    private void uploadImages(List<UploadTask> tasks, ArrayList<String> listOfImages) {
//
//        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
//            for (Task<?> task : allTasks.getResult()) {
//                if (task.isSuccessful()) {
//                    Logger.log("{uploadImages} - task is successful");
//                } else {
//                    onImageUploaded.processError();
//                    Logger.log("{uploadImages} - task is not successful " + Objects.requireNonNull(task.getException()).getMessage());
//                }
//            }
//            getImageUrl(listOfImages);
//        });
//    }
//
//    private void getImageUrl(ArrayList<String> listOfImages) {
//        List<Task<Uri>> tasks = new ArrayList<>();
//        for (String uri : listOfImages) {
//            tasks.add(mStorageReference.child("houses/" + uri).getDownloadUrl());
//        }
//
//        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
//            for (Task<?> task : allTasks.getResult()) {
//                if (task.isSuccessful()) {
//                    Logger.log("{getImageUrl} - task is successful ");
//                    mHouse.addImage(task.getResult().toString());
//                } else {
//                    onImageUploaded.processError();
//                    Logger.log("{getImageUrl} - task is not successful " + Objects.requireNonNull(task.getException()).getMessage());
//                }
//            }
//            saveHouse(mHouse);
//        });
//    }
//
//    public interface OnImageUploaded {
//
//        void processSuccess();
//
//        void processError();
//    }
//}
