package com.orcchg.musicsquare.ui.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final List<ArtistListItemVO> artists;
    private final ItemClickListener listener;

    public interface ItemClickListener {
        void onItemClick(View view, long artistId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @Nullable @BindView(R.id.fl_grid_item) View gridItemView;  // only on large screens
        @BindView(R.id.pb_loading) CircularProgressBar progressBar;
        @BindView(R.id.iv_cover) ImageView iconView;
        @BindView(R.id.tv_musician_title) TextView titleView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }

    public ListAdapter(List<ArtistListItemVO> artists, ItemClickListener listener) {
        this.artists = artists;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_musician, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = holder.rootView.getContext();
        final ArtistListItemVO artist = this.artists.get(position);

        holder.rootView.setOnClickListener((view) -> this.listener.onItemClick(holder.iconView, artist.getId()));

        if (holder.gridItemView != null) {
            holder.gridItemView.setOnClickListener((view) -> this.listener.onItemClick(holder.iconView, artist.getId()));
        }

        holder.titleView.setText(artist.getName());

        Glide.with(context)
                .load(artist.getCoverSmall())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.iconView);
    }

    @Override
    public int getItemCount() {
        return this.artists.size();
    }

    public void populate(List<ArtistListItemVO> items) {
        this.artists.addAll(items);
    }

    public void clear() {
        this.artists.clear();
    }
}
