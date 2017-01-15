package com.yousuf.android.sample.filescanner.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yousuf.android.sample.filescanner.R;
import com.yousuf.android.sample.filescanner.model.ScanResults;
import com.yousuf.android.sample.filescanner.utils.AppUtils;

import java.io.File;

public class ScanResultsFragment extends Fragment {

    private OnFileScanListener mListener;

    private FileScannerTask fileScannerTask;

    private boolean running;

    public ScanResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setListener(activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setListener(context);
    }

    private void setListener(Context context){
        if (context instanceof OnFileScanListener) {
            mListener = (OnFileScanListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void startFileScan() {
        if(AppUtils.isExternalStorageReadable()) {
            fileScannerTask = new FileScannerTask();
            fileScannerTask.execute();
            running = true;
        }else {
            if(mListener != null){
                mListener.showError();
            }
        }
    }

    public void cancelFileScan(){
        if(fileScannerTask != null) {
            fileScannerTask.cancel(true);
            fileScannerTask = null;
            running = false;
        }
    }

    public boolean isRunning(){
        return running;
    }

    private class FileScannerTask extends AsyncTask<Uri, Void, Void> {

        private String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

        private ScanResults mScanResults;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mListener != null) {
                mListener.onPreScan();
            }
            running = true;
        }

        @Override
        protected Void doInBackground(Uri... uri) {
            mScanResults = new ScanResults();
            File root = new File(SDCARD_ROOT);
            getFile(root);
            mScanResults.extractResults();
            return null;
        }

        public void onPostExecute(Void none) {
            super.onPostExecute(none);
            if(mListener != null) {
                mListener.onPostScan(mScanResults);
            }
            running = false;
        }

        @Override
        public void onCancelled(){
            if(mListener != null) {
                mListener.onCancelled();
            }
            running = false;
        }

        private void getFile(File dir) {
            if (dir == null) {
                return;
            }

            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (File file : listFile) {
                    if (file.isDirectory()) {
                        getFile(file);
                    } else {
                        long size = file.length();
                        String title = file.getName();
                        String ext = AppUtils.getExtension(title);
                        mScanResults.addScanResult(title, size, ext);
                    }
                }
            }
        }
    }

    public interface OnFileScanListener {
        void onPreScan();
        void onPostScan(ScanResults results);
        void onCancelled();
        void showError();
    }
}
