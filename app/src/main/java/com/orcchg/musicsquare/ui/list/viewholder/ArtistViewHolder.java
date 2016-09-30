package com.orcchg.musicsquare.ui.list.viewholder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.list.ItemClickListener;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ArtistViewHolder extends NormalViewHolder {

    private final ItemClickListener listener;

    View rootView;
    @Nullable @BindView(R.id.fl_grid_item) View gridItemView;  // only on large screens
    @BindView(R.id.pb_loading) CircularProgressBar progressBar;
    @BindView(R.id.iv_cover) ImageView iconView;
    @BindView(R.id.tv_musician_title) TextView titleView;

    public ArtistViewHolder(View view, ItemClickListener listener) {
        super(view);
        this.rootView = view;
        this.listener = listener;
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(ArtistListItemVO viewObject) {
        final Context context = rootView.getContext();

        rootView.setOnClickListener((view) -> this.listener.onItemClick(iconView, viewObject.getId()));

        if (gridItemView != null) {
            gridItemView.setOnClickListener((view) -> this.listener.onItemClick(iconView, viewObject.getId()));
        }

        titleView.setText(viewObject.getName());

        Glide.with(context)
                .load(viewObject.getCoverSmall())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(iconView);
    }
}
