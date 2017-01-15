package com.yousuf.android.sample.filescanner.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.yousuf.android.sample.filescanner.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Yousuf S. on 1/12/17.
 */
public class ScanResults implements Parcelable {

    private static final int LARGEST_FILES_LIMIT = 10;

    private static final int FILES_EXTENSION_LIMIT = 5;

    private TreeSet<MFile> mLargestFiles;

    private LinkedHashMap<String, Integer> mFrequencyMap;

    private String shareResults = "none";

    private String mAverageFileSize ="";

    //Temp local store:
    private PriorityQueue<MFile> mTempFileQueue;

    private HashMap<String, Integer> mTempFrequencyMap;

    private long avgSize;

    private long totalFiles=0;

    public ScanResults() {
        mTempFileQueue = new PriorityQueue<>();
        mTempFrequencyMap = new HashMap<>();
        mLargestFiles = new TreeSet<>(Collections.reverseOrder());
        mFrequencyMap = new LinkedHashMap<>();
    }

    /**
     * Add file details to data store.
     *
     * @param name file title
     * @param size file size
     * @param extension file extension
     */
    public void addScanResult(String name, long size, String extension) {
        if (mTempFileQueue == null || mTempFrequencyMap == null) {
            return;
        }
        avgSize += size;
        addFileDetails(mTempFileQueue, name, size);
        updateFrequency(mTempFrequencyMap, extension);
        totalFiles++;
        Log.d("SCAN_RESULT_ADDED", "name: " + name + "\tsize: " + size + "\textension: " + extension);
        Log.d("SCAN_RESULT_ADDED", "size: " + avgSize);

    }

    /**
     * Extract results from temp data stores and save them in the appropriate data structures.
     */
    public void extractResults() {
        if(mTempFileQueue.isEmpty())
            return;

        avgSize = avgSize / totalFiles;

        Log.d("SCAN_RESULT_ADDED", "total files: " +  totalFiles);

        mAverageFileSize = AppUtils.getConvertedSize(avgSize);
        saveLargestFilesInfo(mTempFileQueue);
        saveTopFileExtensionFrequencies(mTempFrequencyMap);
        resetTempStore();
    }

    private void saveLargestFilesInfo(PriorityQueue<MFile> fileList) {
        if (fileList == null || fileList.isEmpty()) {
            return;
        }
        while (mLargestFiles.size() < LARGEST_FILES_LIMIT) {
            mLargestFiles.add(fileList.poll());
        }
    }

    /**
     * Re-order extensions by frequencies and save them in a Map
     *
     * @param frequencyMap: map containing total occurrance of each extension.
     */
    private void saveTopFileExtensionFrequencies(Map<String, Integer> frequencyMap) {
        if (frequencyMap == null || frequencyMap.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        for (Map.Entry<String, Integer> entry : list) {
            mFrequencyMap.put(entry.getKey(), entry.getValue());
            if (mFrequencyMap.size() == FILES_EXTENSION_LIMIT) {
                break;
            }
        }
    }

    private void resetTempStore() {
        avgSize = 0;
        mTempFileQueue.clear();
        mTempFrequencyMap.clear();
    }

    private void addFileDetails(PriorityQueue<MFile> queue, String name, long size) {
        queue.add(new MFile(name, size));
        if (queue.size() > LARGEST_FILES_LIMIT) {
            queue.poll();
        }
    }

    private void updateFrequency(Map<String, Integer> frequencyMap, String extension) {
        int count = 0;
        if (frequencyMap.containsKey(extension)) {
            count = frequencyMap.get(extension);
        }
        frequencyMap.put(extension, ++count);
    }

    /**
     * Get Average file size by
     * @return average file size
     */
    public String getAvgFileSize() {
        return mAverageFileSize;
    }

    /**
     * Get list of largest files.
     * @return set of MFiles
     */
    public Set<MFile>  getLargestFiles(){
        return mLargestFiles;
    }

    /**
     * Get largest files info as String
     * @return string will 10 most largest files.
     */
    public String getLargestFilesInfo() {
        StringBuilder sb = new StringBuilder();
        if (mLargestFiles != null) {
            for (MFile file : mLargestFiles) {
                sb.append("\n");
                sb.append(file.getName());
                sb.append("\t\t");
                sb.append(file.getSize());
            }
        }
        return sb.toString();
    }

    /**
     * Get most frequent file extension info as String
     * @return string will 5 most frequent file extensions.
     */
    public String getFrequentExtensionsInfo() {
        StringBuilder sb = new StringBuilder();
        if (mFrequencyMap != null) {
            for (Map.Entry<String, Integer> entry : mFrequencyMap.entrySet()) {
                sb.append("\n");
                sb.append(entry.getKey());
                sb.append("\t\t");
                sb.append(entry.getValue());
            }
        }
        return sb.toString();
    }

    /**
     * Get most frequent extensions
     * @return set of frequent file extensions
     */
    public Set<Map.Entry<String,Integer>> getFrequencies(){
        return mFrequencyMap.entrySet();
    }

    public void reset() {
        mAverageFileSize = "";
        shareResults ="";
        mFrequencyMap.clear();
        mLargestFiles.clear();
    }


    public void setResultsAsText(String resultsAsText) {
        shareResults = resultsAsText;
    }


    public String getResultsAsText() {
        return shareResults;
    }

    /**
     * Check to see if files are empty or not.
     *
     * @return
     */
    public boolean isEmpty() {
        return (mFrequencyMap == null || mFrequencyMap.isEmpty() || mLargestFiles == null || mLargestFiles.isEmpty());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.avgSize);
        dest.writeSerializable(this.mLargestFiles);
        dest.writeSerializable(this.mFrequencyMap);
        dest.writeString(this.shareResults);
        dest.writeString(this.mAverageFileSize);
        dest.writeSerializable(this.mTempFileQueue);
        dest.writeInt(this.mTempFrequencyMap.size());
        dest.writeMap(mTempFrequencyMap);

//        for (Map.Entry<String, Integer> entry : this.mTempFrequencyMap.entrySet()) {
//            dest.writeString(entry.getKey());
//            dest.writeValue(entry.getValue());
//        }
    }

    protected ScanResults(Parcel in) {
        this();
        this.avgSize = in.readLong();
        this.mLargestFiles = (TreeSet<MFile>) in.readSerializable();
        this.mFrequencyMap = (LinkedHashMap<String, Integer>) in.readSerializable();
        this.shareResults = in.readString();
        this.mAverageFileSize = in.readString();
        this.mTempFileQueue = (PriorityQueue<MFile>) in.readSerializable();
        int mTempFrequencyMapSize = in.readInt();
        this.mTempFrequencyMap = in.readHashMap(HashMap.class.getClassLoader());

//              //  new HashMap<String, Integer>(mTempFrequencyMapSize);
//
//        for (int i = 0; i < mTempFrequencyMapSize; i++) {
//            String key = in.readString();
//            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
//            this.mTempFrequencyMap.put(key, value);
//        }
    }

    public static final Parcelable.Creator<ScanResults> CREATOR = new Parcelable.Creator<ScanResults>() {
        @Override
        public ScanResults createFromParcel(Parcel source) {
            return new ScanResults(source);
        }

        @Override
        public ScanResults[] newArray(int size) {
            return new ScanResults[size];
        }
    };
}
