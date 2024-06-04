package com.kuro.yemaadmin.views.fragments.houses;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuro.androidutils.logging.Logger;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yemaadmin.BuildConfig;
import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.data.model.House;
import com.kuro.yemaadmin.repositories.HouseRepository;
import com.kuro.yemaadmin.viewModels.HouseViewModel;
import com.kuro.yemaadmin.views.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProcessingHouseCreationFragment extends Fragment {
    private final ArrayList<String> mListOfImages = new ArrayList<>();
    private Uri[] mListOfImagesUri;
    private StorageReference mStorageReference;
    private House mHouse;
    private TextView mMessage;
    private ImageView mProcessSucceeded, mProcessFailed;
    private ProgressBar mProgressbar;
    private NavController mNavController;
    private MainActivity mMainActivity;
    private HouseViewModel mHouseViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HouseViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mHouseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(HouseViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mMainActivity = (MainActivity) requireActivity();

        mStorageReference = firebaseStorage.getReference();
        mNavController = Navigation.findNavController(view);

        mMessage = view.findViewById(R.id.loading_message);
        mProcessSucceeded = view.findViewById(R.id.process_succeeded);
        mProcessFailed = view.findViewById(R.id.process_failed);
        mProgressbar = view.findViewById(R.id.progressBar);

        mProcessSucceeded.setVisibility(View.GONE);
        mProcessFailed.setVisibility(View.GONE);

        mListOfImagesUri = ProcessingHouseCreationFragmentArgs.fromBundle(requireArguments()).getListOfImages();
        mHouse = ProcessingHouseCreationFragmentArgs.fromBundle(requireArguments()).getHouse();

        if (mListOfImagesUri != null) {
            saveImages();
        }

        HouseRepository.OnHouseCreated onHouseCreated = new HouseRepository.OnHouseCreated() {
            @Override
            public void processSuccess() {
                onProcessSuccess();
                mHouseViewModel.getHouseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<House>() {
                    @Override
                    public void onChanged(House house) {
                        if (house != null) {
                            house.setUploadedAt(null);
                            saveIntoAlgolia(house);
                        }
                    }
                });
                mHouseViewModel.getHouseListMutableLiveData().removeObservers(getViewLifecycleOwner());
            }

            @Override
            public void processFailure() {
                onProcessFailure();
            }
        };
        mHouseViewModel.setOnHouseCreated(onHouseCreated);
    }


    public void saveImages() {
        List<UploadTask> tasks = new ArrayList<>();
        StorageReference houseRef;
        UploadTask uploadTask;
        for (Uri uri : mListOfImagesUri) {
            String newUri = uri.getLastPathSegment() + UUID.randomUUID();
            houseRef = mStorageReference.child("houses/" + newUri);
            uploadTask = houseRef.putFile(uri);
            mListOfImages.add(newUri);
            tasks.add(uploadTask);
        }
        uploadImages(tasks);
    }

    private void uploadImages(List<UploadTask> tasks) {

        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            for (Task<?> task : allTasks.getResult()) {
                if (task.isSuccessful()) {
                    Logger.log("{uploadImages} - task is successful");
                } else {
                    onProcessFailure();
                    Logger.log("{uploadImages} - task is not successful " + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
            getImageUrl();
        });
    }

    private void getImageUrl() {
        List<Task<Uri>> tasks = new ArrayList<>();
        for (String uri : mListOfImages) {
            tasks.add(mStorageReference.child("houses/" + uri).getDownloadUrl());
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            for (Task<?> task : allTasks.getResult()) {
                if (task.isSuccessful()) {
                    Logger.log("{getImageUrl} - task is successful ");
                    mHouse.addImage(task.getResult().toString());
                } else {
                    onProcessFailure();
                    Logger.log("{getImageUrl} - task is not successful " + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
            saveHouse(mHouse);
        });
    }


    private void saveHouse(House house) {
        mHouseViewModel.addHouse(house, mMainActivity.getManagedCountry());
    }

    private void onProcessFailure() {
        mProgressbar.setVisibility(View.GONE);
        mProcessFailed.setVisibility(View.VISIBLE);
        mMessage.setText(R.string.process_failed);

        new Handler().postDelayed(() -> {
            NavigationUtils.navigateSafe(mNavController, ProcessingHouseCreationFragmentDirections.actionProcessingFragmentToAddHouseFragment());
        }, 1500);
    }

    private void onProcessSuccess() {
        mProgressbar.setVisibility(View.GONE);
        mProcessSucceeded.setVisibility(View.VISIBLE);
        mMessage.setText(R.string.process_succeeded);

        new Handler().postDelayed(() -> {
            NavigationUtils.navigateSafe(mNavController, ProcessingHouseCreationFragmentDirections.actionProcessingFragmentToAddHouseFragment());
        }, 1500);
    }

    private void saveIntoAlgolia(House house) {
        SearchClient client = DefaultSearchClient.create(BuildConfig.ALGOLIA_APP_KEY, BuildConfig.ALGOLIA_API_KEY);
        SearchIndex<House> index = client.initIndex("yema", House.class);

        CompletableFuture<Void> saveObjectFuture = CompletableFuture.runAsync(() -> {
            try {
                index.saveObjectAsync(house, null).join(); // Use join to block and wait for completion
            } catch (Exception e) {
                Logger.error("ALGOLIA", "Error saving object: " + e.getMessage());
            }
        });
        saveObjectFuture.join();
        Logger.log("Saved into algolia successfully ");
    }
}
