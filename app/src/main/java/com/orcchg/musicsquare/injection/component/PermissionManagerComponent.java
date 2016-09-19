package com.orcchg.musicsquare.injection.component;

import com.orcchg.musicsquare.PermissionManager;
import com.orcchg.musicsquare.injection.module.PermissionManagerModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PermissionManagerModule.class})
public interface PermissionManagerComponent {

    PermissionManager permissionManager();
}
