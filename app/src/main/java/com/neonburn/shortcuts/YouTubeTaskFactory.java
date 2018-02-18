package com.neonburn.shortcuts;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

class YouTubeTaskFactory {
  static TaskRunner getTask(GoogleAccountCredential credential,
                                        MainActivity activity,
                                        String task) {
    if(task.equals(Config.TASK_SUBSCRIPTION)) {
      return new YouTubeSubscriptionsAsyncTask(credential, activity);
    }

    if(task.equals(Config.TASK_PLAYLIST)) {
      return new YouTubePlaylistsAsyncTask(credential, activity);
    }

    if(task.equals(Config.TASK_LIKED)) {
      return new YouTubeLikedVideosAsyncTask(credential, activity);
    }

    if(task.equals(Config.TASK_OTHER)) {
      return new YouTubeOtherTask(activity);
    }

    return null;
  }
}
