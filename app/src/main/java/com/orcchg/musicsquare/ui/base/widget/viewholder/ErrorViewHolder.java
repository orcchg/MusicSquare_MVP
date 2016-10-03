package com.orcchg.musicsquare.ui.base.widget.viewholder;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.orcchg.musicsquare.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ErrorViewHolder extends BaseViewHolder {

    private final View.OnClickListener listener;

    @BindView(R.id.btn_retry) Button retryButton;
    @OnClick(R.id.btn_retry)
    public void onRetryClick() {
        if (listener != null) listener.onClick(retryButton);
    }

    public ErrorViewHolder(View view, @Nullable View.OnClickListener listener) {
        super(view);
        this.listener = listener;
        ButterKnife.bind(this, view);
    }
}
