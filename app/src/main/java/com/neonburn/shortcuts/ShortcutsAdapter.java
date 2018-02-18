package com.neonburn.shortcuts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

class ShortcutsAdapter extends RecyclerView.Adapter<ShortcutsAdapter.ShortcutHolder> {

  class ShortcutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView mImage;
    TextView mDescr;
    TextView mTitle;
    ResultClickListener mClickListener;

    ShortcutHolder(View itemView, ResultClickListener listener) {
      super(itemView);
      mImage = itemView.findViewById(R.id.thumbnail);
      mTitle = itemView.findViewById(R.id.title);
      mDescr = itemView.findViewById(R.id.description);

      mClickListener = listener;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      YouTubeResult ytr = ShortcutsAdapter.this.mResults.get(getAdapterPosition());
      mClickListener.handle(ytr);
    }
  }

  private List<YouTubeResult> mResults;
  private ResultClickListener mClickListener;

  ShortcutsAdapter(List<YouTubeResult> results, ResultClickListener listener) {
    mResults = results;
    mClickListener = listener;
  }

  void setResults(List<YouTubeResult> results) {
    mResults = results;
    notifyDataSetChanged();
  }

  @Override
  public ShortcutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shortcut_holder, parent, false);
    return new ShortcutHolder(v, mClickListener);
  }

  @Override
  public void onBindViewHolder(ShortcutHolder holder, int position) {
    holder.mDescr.setText(mResults.get(position).getDescription());
    holder.mTitle.setText(mResults.get(position).getTitle());

    String url = mResults.get(position).getImageUrl();
    if(url != null) {
      Glide.with(holder.mImage.getContext())
        .load(url)
        .centerCrop()
        //      .placeholder(R.drawable.loading_spinner)
        .crossFade()
        .into(holder.mImage);
    } else {
      holder.mImage.setImageDrawable(
        holder.mImage.getContext().getDrawable(mResults.get(position).getImageResourceId())
      );
    }
  }

  @Override
  public int getItemCount() {
    return mResults.size();
  }
}
