package com.neonburn.shortcuts;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

abstract public class YouTubeAsyncTaskRunner extends AsyncTask<Void, Void, List<YouTubeResult>> implements TaskRunner {
  com.google.api.services.youtube.YouTube mService;

  private WeakReference<MainActivity> mActivity;
  WeakReference<Context> mContext;

  private static HttpTransport HTTP = AndroidHttp.newCompatibleTransport();
  private static JacksonFactory JSON = JacksonFactory.getDefaultInstance();

  private static Exception mLastError;

  YouTubeAsyncTaskRunner(GoogleAccountCredential credential, MainActivity activity) {
    mService = new com.google.api.services.youtube.YouTube.Builder(HTTP, JSON, credential)
      .setApplicationName(activity.getString(R.string.app_name)).build();
    mActivity = new WeakReference<>(activity);
    mContext = new WeakReference<>(activity.getApplicationContext());
  }

  abstract List<YouTubeResult> runImpl() throws IOException;

  @Override
  public void run() {execute();}

  @Override
  protected List<YouTubeResult> doInBackground(Void... args) {
    try {
      return runImpl();
    } catch(Exception e) {
      mLastError = e;
      cancel(true);
      return null;
    }
  }

  @Override
  protected void onPreExecute() {
  }

  @Override
  protected void onPostExecute(List<YouTubeResult> output) {
    MainActivity activity = mActivity.get();
    if(activity == null) {
      return;
    }

    activity.updateResultsList(output);
  }

  @Override
  protected void onCancelled() {
    if(mLastError == null) {
      return;
    }

    MainActivity activity = mActivity.get();
    if(activity == null) {
      return;
    }

    if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
      GooglePlayServicesAvailabilityIOException e = (GooglePlayServicesAvailabilityIOException) mLastError;
      activity.showPlayServicesAvailErrorDialog(e.getConnectionStatusCode());
      return;
    }

    if (mLastError instanceof UserRecoverableAuthIOException) {
      activity.startActivityForResult(
        ((UserRecoverableAuthIOException) mLastError).getIntent(),
        MainActivity.REQUEST_AUTHORIZATION);
      return;
    }

    Snackbar.make(activity.findViewById(R.id.container), getErrorMessage(), Snackbar.LENGTH_SHORT);
    mLastError.printStackTrace();
  }
}
