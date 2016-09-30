package com.orcchg.musicsquare.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.orcchg.musicsquare.injection.PerActivity;
import com.orcchg.musicsquare.ui.details.DetailsActivity;
import com.orcchg.musicsquare.ui.list.ListActivity;
import com.orcchg.musicsquare.ui.tab.TabActivity;

import javax.inject.Inject;

@PerActivity
public class Navigator {

    @Inject
    public Navigator() {
    }

    public void openListScreen(@NonNull Context context) {
        Intent intent = ListActivity.getCallingIntent(context);
        context.startActivity(intent);
    }

    public void openDetailsScreen(@NonNull Context context, long artistId, View view) {
        Intent intent = DetailsActivity.getCallingIntent(context, artistId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
            view != null && Activity.class.isInstance(context)) {
            Activity activity = (Activity) context;
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "profile");
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    public void openTabsScreen(@NonNull Context context) {
        Intent intent = TabActivity.getCallingIntent(context);
        context.startActivity(intent);
    }
}
