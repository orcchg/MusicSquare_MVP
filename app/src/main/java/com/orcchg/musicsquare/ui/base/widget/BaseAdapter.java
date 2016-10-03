package com.orcchg.musicsquare.ui.base.widget;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orcchg.musicsquare.R;
import com.orcchg.musicsquare.ui.base.widget.viewholder.BaseViewHolder;
import com.orcchg.musicsquare.ui.base.widget.viewholder.ErrorViewHolder;
import com.orcchg.musicsquare.ui.base.widget.viewholder.LoadingViewHolder;
import com.orcchg.musicsquare.ui.base.widget.viewholder.NormalViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<ModelViewHolder extends NormalViewHolder<Model>, Model> extends RecyclerView.Adapter<BaseViewHolder> {

    protected static final int VIEW_TYPE_NORMAL = 0;
    protected static final int VIEW_TYPE_LOADING = 1;
    protected static final int VIEW_TYPE_ERROR = 2;

    protected final List<Model> models;
    protected boolean isThereMore = false;
    protected boolean isInError = false;

    protected View.OnClickListener onErrorClickListener;

    public BaseAdapter() {
        models = new ArrayList<>();
    }

    @Nullable @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:   return createModelViewHolder(parent);
            case VIEW_TYPE_LOADING:  return createLoadingViewHolder(parent);
            case VIEW_TYPE_ERROR:    return createErrorViewHolder(parent);
            default:                 return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case VIEW_TYPE_NORMAL:
                ((ModelViewHolder) holder).bind(models.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return models.isEmpty() ? 0 : models.size() + (isThereMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        boolean isLoading = isThereMore && position == getItemCount() - 1;
        return isLoading ? (isInError ? VIEW_TYPE_ERROR : VIEW_TYPE_LOADING) : VIEW_TYPE_NORMAL;
    }

    /* Error state */
    // ------------------------------------------
    public void setOnErrorClickListener(View.OnClickListener onErrorClickListener) {
        this.onErrorClickListener = onErrorClickListener;
    }

    public void onError(boolean isInError) {
        if (!isThereMore) return;
        this.isInError = isInError;
        notifyItemChanged(getItemCount() - 1);
    }

    /* Data access */
    // ------------------------------------------
    public void populate(List<Model> items, boolean isThereMore) {
        isInError = false;
        if (items != null && !items.isEmpty()) {
            this.models.addAll(items);
            this.isThereMore = isThereMore;
            notifyDataSetChanged();
        }
    }

    public void clear() {
        isInError = false;
        models.clear();
        notifyDataSetChanged();
    }

    /* Customization */
    // --------------------------------------------------------------------------------------------
    protected BaseViewHolder createLoadingViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_loading, parent, false);
        return new LoadingViewHolder(view);
    }

    protected BaseViewHolder createErrorViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_error, parent, false);
        return new ErrorViewHolder(view, onErrorClickListener);
    }

    protected abstract ModelViewHolder createModelViewHolder(ViewGroup parent);
}
