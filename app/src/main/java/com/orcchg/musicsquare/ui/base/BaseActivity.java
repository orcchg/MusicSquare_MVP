package com.orcchg.musicsquare.ui.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.orcchg.musicsquare.AndroidApplication;
import com.orcchg.musicsquare.PermissionManager;
import com.orcchg.musicsquare.injection.component.ApplicationComponent;
import com.orcchg.musicsquare.injection.component.DaggerPermissionManagerComponent;
import com.orcchg.musicsquare.injection.component.PermissionManagerComponent;
import com.orcchg.musicsquare.injection.module.PermissionManagerModule;
import com.orcchg.musicsquare.navigation.Navigator;

import javax.inject.Inject;

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends AppCompatActivity implements MvpView {

    protected P presenter;
    protected @Inject Navigator navigator;
    protected PermissionManagerComponent permissionManagerComponent;

    @NonNull
    protected abstract P createPresenter();

    protected abstract void injectDependencies();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            injectPermissionManager();
            PermissionManager pm = getPermissionManagerComponent().permissionManager();
            // ask for permission
        }

        injectDependencies();
        presenter = createPresenter();
        presenter.attachView((V) this);
        presenter.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        // process permission
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }

    protected PermissionManagerComponent getPermissionManagerComponent() {
        return permissionManagerComponent;
    }

    private void injectPermissionManager() {
        permissionManagerComponent = DaggerPermissionManagerComponent.builder()
            .permissionManagerModule(new PermissionManagerModule(getApplicationContext()))
            .build();
    }
}
