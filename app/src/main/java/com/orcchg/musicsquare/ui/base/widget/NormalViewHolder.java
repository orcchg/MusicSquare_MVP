package com.orcchg.musicsquare.ui.base.widget;

import android.view.View;

public abstract class NormalViewHolder<Model> extends BaseViewHolder {

    public NormalViewHolder(View view) {
        super(view);
    }

    public abstract void bind(Model model);
}
