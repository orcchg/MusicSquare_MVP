package com.orcchg.musicsquare.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.base.widget.BaseAdapter;
import com.orcchg.musicsquare.ui.list.viewholder.ArtistViewHolder;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

class ListAdapter extends BaseAdapter<ArtistViewHolder, ArtistListItemVO> {

    private final ItemClickListener listener;

    ListAdapter(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArtistViewHolder createModelViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_musician, parent, false);
        return new ArtistViewHolder(view, listener);
    }
}
