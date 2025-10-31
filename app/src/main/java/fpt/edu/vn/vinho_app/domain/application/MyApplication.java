package fpt.edu.vn.vinho_app.domain.application;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

import java.util.concurrent.TimeUnit;

import fpt.edu.vn.vinho_app.workers.ProfileSyncWorker;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

        // Schedule profile sync worker
        PeriodicWorkRequest profileSyncWorkRequest = new PeriodicWorkRequest.Builder(ProfileSyncWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("profileSync", ExistingPeriodicWorkPolicy.KEEP, profileSyncWorkRequest);
    }
}
