package com.orcchg.musicsquare.ui.list.viewholder;

import android.view.View;

import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

public abstract class NormalViewHolder extends AbstractViewHolder {

    public NormalViewHolder(View view) {
        super(view);
    }

    public abstract void bind(ArtistListItemVO viewObject);
}
