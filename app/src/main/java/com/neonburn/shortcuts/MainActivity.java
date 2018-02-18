package com.neonburn.shortcuts;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
  GoogleAccountCredential mCredential;

  final static int REQUEST_ACCOUNT_PERMS = 1000;
  final static int REQUEST_ACCOUNT_PICKER = 1001;
  final static int REQUEST_PLAY_SERVICES = 1002;
  final static int REQUEST_AUTHORIZATION = 1003;

  private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};

  private RecyclerView mShortcutsList;
  private ShortcutsAdapter mShortcutsAdapter;
  private List<YouTubeResult> mResults;
  private ActionBar mToolbar;

  private String mCurrentTask;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
    = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      mCurrentTask = item.getTitle().toString();
      queryYouTubeApi();
      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mToolbar = getSupportActionBar();

    BottomNavigationView navigation = findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    mCurrentTask = Config.TASK_SUBSCRIPTION;

    mShortcutsList = findViewById(R.id.shortcuts);

    LinearLayoutManager llm = new LinearLayoutManager(this);
    mShortcutsList.setLayoutManager(llm);

    mShortcutsAdapter = new ShortcutsAdapter(new ArrayList<YouTubeResult>(),
                                             new ResultClickListener(this));
    mShortcutsList.setAdapter(mShortcutsAdapter);

    mToolbar.setTitle(mCurrentTask);

    mCredential = GoogleAccountCredential
      .usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
      .setBackOff(new ExponentialBackOff());

    queryYouTubeApi();
  }

  private void queryYouTubeApi() {
    if (!isGooglePlayServicesAvailable()) {
      acquireGooglePlayServices();
      return;
    }

    if (mCredential.getSelectedAccountName() == null && !getGoogleAccount()) {
      return;
    }

    TaskRunner task = YouTubeTaskFactory.getTask(mCredential, this, mCurrentTask);
    if(task != null) {
      task.run();
    } else {
      updateResultsList(new ArrayList<YouTubeResult>());
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(
      requestCode, permissions, grantResults, this);
  }

  @AfterPermissionGranted(REQUEST_ACCOUNT_PERMS)
  private boolean getGoogleAccount() {

    if (!EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
      EasyPermissions.requestPermissions(
        this,
        getString(R.string.get_account_msg),
        REQUEST_ACCOUNT_PERMS,
        Manifest.permission.GET_ACCOUNTS
      );
      return false;
    }

    String accountName = getPreferences(Context.MODE_PRIVATE).getString(Config.KEY_ACCOUNT_NAME, null);
    if (accountName != null) {
      mCredential.setSelectedAccountName(accountName);
      return true;
    }

    startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    return false;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(requestCode == REQUEST_PLAY_SERVICES && resultCode != RESULT_OK ) {
      return;
    }

    if(requestCode == REQUEST_ACCOUNT_PICKER) {
      if (resultCode != RESULT_OK || data == null || data.getExtras() == null) {
        return;
      }

      String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
      if(accountName == null) {
        return;
      }

      SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString(Config.KEY_ACCOUNT_NAME, accountName);
      editor.apply();

      mCredential.setSelectedAccountName(accountName);
    }

    queryYouTubeApi();
  }

  private boolean isGooglePlayServicesAvailable() {
    return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
  }

  private void acquireGooglePlayServices() {
    GoogleApiAvailability apiAvail = GoogleApiAvailability.getInstance();
    final int connectionStatusCode = apiAvail.isGooglePlayServicesAvailable(this);
    showPlayServicesAvailErrorDialog(connectionStatusCode);
  }

  void showPlayServicesAvailErrorDialog(final int connectionStatusCode) {
    GoogleApiAvailability.getInstance().getErrorDialog(
      this,
      connectionStatusCode,
      REQUEST_PLAY_SERVICES).show();
  }

  void updateResultsList(List<YouTubeResult> results) {
    mShortcutsAdapter.setResults(results);
  }
}