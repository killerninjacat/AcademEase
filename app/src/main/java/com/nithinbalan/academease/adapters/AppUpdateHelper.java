package com.nithinbalan.academease.adapters;

import android.app.Activity;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class AppUpdateHelper implements DefaultLifecycleObserver {

    private final ActivityResultLauncher<IntentSenderRequest> updateResultLauncher;
    private final AppUpdateManager appUpdateManager;
    private final InstallStateUpdatedListener installStateUpdatedListener;

    public AppUpdateHelper(Activity activity, ActivityResultLauncher<IntentSenderRequest> updateResultLauncher) {
        this.updateResultLauncher = updateResultLauncher;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);

        this.installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate();
            }
        };

        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(info -> {
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate();
            } else if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdate(info);
            }
        });
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        appUpdateManager.unregisterListener(installStateUpdatedListener);
    }

    public void checkForUpdates() {
        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(info -> {
                    if (shouldUpdate(info)) {
                        startUpdate(info);
                    }
                })
                .addOnFailureListener(e -> Log.e("AppUpdateHelper", "Error checking for updates: " + e.getMessage()));
    }

    private boolean shouldUpdate(AppUpdateInfo info) {
        return info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE);
    }

    private void startUpdate(AppUpdateInfo info) {
        appUpdateManager.startUpdateFlowForResult(
                info,
                updateResultLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
        );
    }
}