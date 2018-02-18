package com.neonburn.shortcuts;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class YouTubeOtherTask implements TaskRunner {

  private WeakReference<MainActivity> mActivity;
  WeakReference<Context> mContext;

  YouTubeOtherTask(MainActivity activity) {
    mActivity = new WeakReference<>(activity);
    mContext = new WeakReference<>(activity.getApplicationContext());
  }

  @Override
  public void run() {
    List<YouTubeResult> results = new ArrayList<>();

    YouTubeResult res = new YouTubeResult();
    res.setImageResourceId(R.drawable.ic_watch_later);
    res.setData("http://www.youtube.com/playlist?list=WL");
    res.setDescription(mContext.get().getString(R.string.watch_later_list));
    res.setItemType(Config.TASK_OTHER);

    results.add(res);

    res = new YouTubeResult();
    res.setImageResourceId(R.drawable.ic_history);
    res.setData("https://www.youtube.com/feed/history");
    res.setDescription(mContext.get().getString(R.string.history_list));
    res.setItemType(Config.TASK_OTHER);

    results.add(res);

    mActivity.get().updateResultsList(results);
  }

  @Override
  public String getErrorMessage() {
    return mContext.get().getString(R.string.other_error);
  }
}
