package com.orcchg.data.source.repository;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RepoUtils {
    public static final int SOURCE_REMOTE = 0;
    public static final int SOURCE_LOCAL = 1;
    @IntDef({SOURCE_REMOTE, SOURCE_LOCAL})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface SourceType {}
}
