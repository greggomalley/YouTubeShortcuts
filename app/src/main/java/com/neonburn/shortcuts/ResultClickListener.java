package com.neonburn.shortcuts;

import android.content.Intent;

class ResultClickListener {
  private MainActivity mActivity;
  private ShortcutsAdapter mAdapter;

  ResultClickListener(MainActivity activity) {
    mActivity = activity;
  }

  void handle(YouTubeResult yrt) {
    Intent intent  = yrt.toYouTubeIntent(mActivity);
    if(intent != null) {
      mActivity.startActivity(intent);
    }
  }
}
