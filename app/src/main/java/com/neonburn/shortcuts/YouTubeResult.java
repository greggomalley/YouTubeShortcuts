package com.neonburn.shortcuts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.youtube.player.YouTubeIntents;

public class YouTubeResult {
  private String mDescr;
  private String mTitle;
  private String mImageUrl;
  private String mId;
  private String mItemType;
  private int mImageResourceId;
  private String mData;

  YouTubeResult() {
  }

  String getTitle() {
    return mTitle;
  }

  void setTitle(String title) {
    this.mTitle = title;
  }

  String getDescription() {
    return mDescr;
  }

  void setDescription(String descr) {
    this.mDescr = descr;
  }

  String getImageUrl() {
    return mImageUrl;
  }

  void setImageUrl(String imageUrl) {
    this.mImageUrl = imageUrl;
  }

  String getId() {
    return mId;
  }

  void setId(String id) {
    this.mId = id;
  }

  String getItemType() {
    return mItemType;
  }

  int getImageResourceId() {
    return mImageResourceId;
  }

  void setImageResourceId(int resourceId) {
    mImageResourceId = resourceId;
  }

  void setItemType(String itemType) {
    this.mItemType = itemType;
  }

  String getData() {
    return mData;
  }

  void setData(String data) {
    mData = data;
  }

  Intent toYouTubeIntent(Context context) {
    if(mItemType.equals(Config.TASK_NOT_FOUND)) {
      return null;
    }

    if(mItemType.equals(Config.TASK_OTHER)) {
      Uri uri = Uri.parse(mData);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(uri);
      return intent;
    }

    if(mItemType.equals(Config.TASK_SUBSCRIPTION) && YouTubeIntents.canResolveChannelIntent(context)) {
      return YouTubeIntents.createChannelIntent(context, mId);
    }

    if(mItemType.equals(Config.TASK_PLAYLIST) && YouTubeIntents.canResolveOpenPlaylistIntent(context)) {
      return YouTubeIntents.createOpenPlaylistIntent(context, mId);
    }

    if(mItemType.equals(Config.TASK_LIKED) && YouTubeIntents.canResolvePlayVideoIntent(context)) {
      return YouTubeIntents.createPlayVideoIntent(context, mId);
    }

    return null;
  }

  static YouTubeResult getNotFound(String msg) {
    YouTubeResult ytr = new YouTubeResult();
    ytr.setItemType(Config.TASK_NOT_FOUND);
    ytr.setImageResourceId(R.drawable.not_found);
    ytr.setDescription(msg);
    return ytr;
  }
}
