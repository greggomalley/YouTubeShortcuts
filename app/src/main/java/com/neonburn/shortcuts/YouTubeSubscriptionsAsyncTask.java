package com.neonburn.shortcuts;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.SubscriptionSnippet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class YouTubeSubscriptionsAsyncTask extends YouTubeAsyncTaskRunner {

  YouTubeSubscriptionsAsyncTask(GoogleAccountCredential credential, MainActivity activity) {
    super(credential, activity);
  }

  public List<YouTubeResult> runImpl() throws IOException {
    List<YouTubeResult> results = new ArrayList<>();
    YouTube.Subscriptions.List subsListReq = mService.subscriptions().list("snippet").setMine(true);

    SubscriptionListResponse resp;
    try {
      resp = subsListReq.execute();
    } catch(GoogleJsonResponseException e) {
      if(e.getDetails().getCode() == 404) {
        results.add(YouTubeResult.getNotFound(
          mContext.get().getString(R.string.no_subscriptions_found))
        );
        return results;
      }
      throw e;
    }

    for (Subscription sub : resp.getItems()) {
      YouTubeResult ytr = new YouTubeResult();
      SubscriptionSnippet snippet = sub.getSnippet();

      ytr.setTitle(snippet.getTitle());
      ytr.setDescription(snippet.getDescription());
      ytr.setImageUrl(snippet.getThumbnails().getDefault().getUrl());
      ytr.setId(snippet.getResourceId().getChannelId());
      ytr.setItemType(Config.TASK_SUBSCRIPTION);

      results.add(ytr);
    }
    return results;
  }

  @Override
  public String getErrorMessage() {
    return mContext.get().getString(R.string.subscription_error);
  }
}
