package com.neonburn.shortcuts;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeLikedVideosAsyncTask extends YouTubeAsyncTaskRunner {

  YouTubeLikedVideosAsyncTask(GoogleAccountCredential credential, MainActivity activity) {
    super(credential, activity);
  }

  @Override
  public List<YouTubeResult> runImpl() throws IOException {
    List<YouTubeResult> results = new ArrayList<>();
    YouTube.Videos.List videosListReq = mService.videos().list("snippet").setMyRating("like");

    VideoListResponse resp;
    try {
      resp = videosListReq.execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        results.add(YouTubeResult.getNotFound(
          mContext.get().getString(R.string.no_liked_videos_found))
        );
        return results;
      }
      throw e;
    }

    for (Video video : resp.getItems()) {
      YouTubeResult ytr = new YouTubeResult();
      VideoSnippet snippet = video.getSnippet();

      ytr.setTitle(snippet.getTitle());
      ytr.setDescription(snippet.getDescription());
      ytr.setImageUrl(snippet.getThumbnails().getDefault().getUrl());
      ytr.setId(video.getId());
      ytr.setItemType(Config.TASK_LIKED);

      results.add(ytr);
    }

    return results;
  }

  @Override
  public String getErrorMessage() {
    return mContext.get().getString(R.string.liked_video_error);
  }
}
