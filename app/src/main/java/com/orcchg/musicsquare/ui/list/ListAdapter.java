package com.orcchg.musicsquare.ui.list;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.list.viewholder.AbstractViewHolder;
import com.orcchg.musicsquare.ui.list.viewholder.ArtistViewHolder;
import com.orcchg.musicsquare.ui.list.viewholder.LoadingViewHolder;
import com.orcchg.musicsquare.ui.list.viewholder.NormalViewHolder;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

import java.util.ArrayList;
import java.util.List;

class ListAdapter extends RecyclerView.Adapter<AbstractViewHolder> {
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private final List<ArtistListItemVO> artists;
    private final ItemClickListener listener;

    private boolean isThereMore = false;

    ListAdapter(ItemClickListener listener) {
        this.artists = new ArrayList<>();
        this.listener = listener;
    }

    @Nullable @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ArtistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_musician, parent, false), listener);
            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_loading, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING) return;
        ArtistListItemVO artist = artists.get(position);
        NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
        normalViewHolder.bind(artist);
    }

    @Override
    public int getItemCount() {
        return artists.isEmpty() ? 0 : artists.size() + (isThereMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        boolean isLoading = isThereMore && position == getItemCount() - 1;
        return isLoading ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
    }

    void populate(List<ArtistListItemVO> items, boolean isThereMore) {
        if (items != null && !items.isEmpty()) {
            this.artists.addAll(items);
            this.isThereMore = isThereMore;
            notifyDataSetChanged();
        }
    }

    void clear() {
        artists.clear();
        notifyDataSetChanged();
    }
}
