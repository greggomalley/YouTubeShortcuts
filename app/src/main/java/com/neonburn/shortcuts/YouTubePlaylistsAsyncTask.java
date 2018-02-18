package com.neonburn.shortcuts;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class YouTubePlaylistsAsyncTask extends YouTubeAsyncTaskRunner {

  YouTubePlaylistsAsyncTask(GoogleAccountCredential credential, MainActivity activity) {
    super(credential, activity);
  }

  @Override
  public List<YouTubeResult> runImpl() throws IOException {
    List<YouTubeResult> results = new ArrayList<>();
    YouTube.Playlists.List subsListReq = mService.playlists().list("snippet,contentDetails").setMine(true);

    PlaylistListResponse resp;
    try {
       resp = subsListReq.execute();
    } catch(GoogleJsonResponseException e) {
      if(e.getDetails().getCode() == 404) {
        results.add(YouTubeResult.getNotFound(
          mContext.get().getString(R.string.no_playlists_found))
        );
        return results;
      }
      throw e;
    }

    for (Playlist playlist : resp.getItems()) {
      YouTubeResult ytr = new YouTubeResult();
      PlaylistSnippet snippet = playlist.getSnippet();

      ytr.setTitle(snippet.getTitle());
      ytr.setDescription(snippet.getDescription());
      ytr.setImageUrl(snippet.getThumbnails().getDefault().getUrl());
      ytr.setId(playlist.getId());
      ytr.setItemType(Config.TASK_PLAYLIST);

      results.add(ytr);
    }

    return results;
  }

  @Override
  public String getErrorMessage() {
    return mContext.get().getString(R.string.playlist_error);
  }}
