package com.orcchg.musicsquare.ui;

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

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends AppCompatActivity implements MvpView {

    protected P presenter;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        return this.permissionManagerComponent;
    }

    private void injectPermissionManager() {
        this.permissionManagerComponent = DaggerPermissionManagerComponent.builder()
                .permissionManagerModule(new PermissionManagerModule(this.getApplicationContext()))
                .build();
    }
}
