package com.yousuf.android.sample.filescanner.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yousuf.android.sample.filescanner.R;
import com.yousuf.android.sample.filescanner.adapter.SectionAdapter;
import com.yousuf.android.sample.filescanner.databinding.ActivityFileScannerBinding;
import com.yousuf.android.sample.filescanner.fragment.ScanResultsFragment;
import com.yousuf.android.sample.filescanner.model.MFile;
import com.yousuf.android.sample.filescanner.model.ScanResults;
import com.yousuf.android.sample.filescanner.utils.AppUtils;
import com.yousuf.android.sample.filescanner.utils.PermissionsUtil;

import java.util.Map;

public class FileScannerActivity extends AppCompatActivity implements ScanResultsFragment.OnFileScanListener {

    private static final int REQUEST_PERMISSIONS_CODE = 100;

    private static final int NOTIFICATION_ID = 1;

    private static final String SCAN_RESULTS_KEY = "scan-results-key";

    private static final String SCAN_RESULTS_FRAGMENT_KEY = "scan-results-fragment";

    private ActivityFileScannerBinding fileScannerBinding;

    private ScanResultsFragment scanResultsFragment;

    private ScanResults mScanResults;

    private void initScanResults() {
        fileScannerBinding.scanResults.setLayoutManager(new LinearLayoutManager(this));
        fileScannerBinding.scanResults.setAdapter(new SectionAdapter());


        FragmentManager fm = getSupportFragmentManager();

        scanResultsFragment = (ScanResultsFragment) fm.findFragmentByTag(SCAN_RESULTS_FRAGMENT_KEY);
        if (scanResultsFragment == null) {
            scanResultsFragment = new ScanResultsFragment();
            fm.beginTransaction().add(scanResultsFragment, SCAN_RESULTS_FRAGMENT_KEY).commit();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileScannerBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_scanner);
        fileScannerBinding.setHandlers(this);
        initScanResults();
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress(scanResultsFragment.isRunning());
        if (PermissionsUtil.isPermissionRequired(this)) {
            showPermissionDialog();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && !scanResultsFragment.isRunning()) {
            mScanResults = savedInstanceState.getParcelable(SCAN_RESULTS_KEY);
            updateScanResults();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mScanResults != null) {
            outState.putParcelable(SCAN_RESULTS_KEY, mScanResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_share).setVisible((mScanResults != null) && (!mScanResults.isEmpty()));
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scanResultsFragment.cancelFileScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startFileScan();
                } else {
                    showEmptyMessage(getString(R.string.enable_permission));
                }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onStartButtonClicked() {
        if (PermissionsUtil.isPermissionRequired(this)) {
            Toast.makeText(this, "Storage Permission required", Toast.LENGTH_SHORT).show();
        } else {
            startFileScan();
        }
    }

    public void onStopButtonClicked() {
        scanResultsFragment.cancelFileScan();
        fileScannerBinding.actionStop.setEnabled(false);
        fileScannerBinding.progressTitle.setText(getString(R.string.preparing_to_cancel));
    }

    public void onShareClicked(MenuItem item) {
        if (mScanResults != null) {
            AppUtils.shareResults(FileScannerActivity.this,
                    getString(R.string.share_title),
                    getString(R.string.share_subject),
                    mScanResults.getResultsAsText()
            );
        }
    }

    @Override
    public void onPreScan() {
        showProgress(true);
    }

    @Override
    public void onPostScan(ScanResults results) {
        mScanResults = results;
        updateScanResults();
        showProgress(false);
    }

    @Override
    public void onCancelled() {
        showProgress(false);
        Log.v("cancelled", "cancelled");
        fileScannerBinding.progressTitle.setText(getString(R.string.file_scan_in_progress));
        showMessage(getString(R.string.scan_cancelled));
        removeNotification();
    }

    private void startFileScan() {
        if (mScanResults != null) {
            mScanResults.reset();
        }
        scanResultsFragment.startFileScan();
    }


    private void showPermissionDialog() {
        if (PermissionsUtil.isRationaleRequired(this)) { // user already denied
            showExplanationDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
        }
    }

    private void updateScanResults() {
        if (mScanResults != null && !mScanResults.isEmpty()) {
            mScanResults.setResultsAsText(getString(R.string.share_results_message,
                    mScanResults.getAvgFileSize(),
                    mScanResults.getLargestFilesInfo(),
                    mScanResults.getFrequentExtensionsInfo()));
            updateResultsAdapter();
        } else {
            showEmptyMessage(getString(R.string.empty_sdcard));
        }
        invalidateOptionsMenu();
    }

    private void updateResultsAdapter() {
        SectionAdapter adapter = (SectionAdapter) fileScannerBinding.scanResults.getAdapter();
        adapter.reset();
        adapter.addHeader(getString(R.string.avg_file_size));
        adapter.addItem(getString(R.string.avg_file_size), mScanResults.getAvgFileSize());
        adapter.addHeader(getString(R.string.top_10_largest_files));
        for (MFile file : mScanResults.getLargestFiles()) {
            adapter.addItem(file.getName(), file.getSize());
        }

        adapter.addHeader(getString(R.string.top_5_frequent_extensions));
        for (Map.Entry<String, Integer> entry : mScanResults.getFrequencies()) {
            adapter.addItem(entry.getKey(), String.valueOf(entry.getValue()));
        }

        fileScannerBinding.emptyMessage.setVisibility(View.GONE);
        fileScannerBinding.scanResults.setVisibility(View.VISIBLE);

    }

    private void showEmptyMessage(String message) {
        fileScannerBinding.emptyMessage.setText(message);
        fileScannerBinding.emptyMessage.setVisibility(View.VISIBLE);
        fileScannerBinding.scanResults.setVisibility(View.GONE);
    }

    private void showExplanationDialog() {
        AlertDialog dlg = (new AlertDialog.Builder(this))
                .setCancelable(false)
                .setMessage(R.string.enable_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(FileScannerActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                    }
                }).create();
        dlg.show();
    }

    private void showProgress(boolean showProgress) {
        fileScannerBinding.progressContainer.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        fileScannerBinding.actionStart.setEnabled(!showProgress);
        fileScannerBinding.actionStop.setEnabled(showProgress);
        if (showProgress) {
            fileScannerBinding.emptyMessage.setText("");
            showOnGoingNotification();
        } else {
            removeNotification();
        }
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(fileScannerBinding.mainContainer, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    private void showOnGoingNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.file_scan_in_progress));

        Intent resultIntent = new Intent(this, FileScannerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(FileScannerActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }


    private void removeNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }


}


