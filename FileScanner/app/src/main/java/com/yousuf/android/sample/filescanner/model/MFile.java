package com.yousuf.android.sample.filescanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.yousuf.android.sample.filescanner.utils.AppUtils;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by u471637 on 1/12/17.
 */

public class MFile implements Comparable<MFile>, Serializable {
    private String mName;
    private long mSize; // size in Kb

    private String sizeString;

    public MFile(String name, long count) {
        mName = name;
        mSize = count;
    }

    @Override
    public int compareTo(MFile o) {
        if (mSize > o.mSize) {
            return 1;
        } else if (mSize < o.mSize) {
            return -1;
        }
        return 0;
    }

    public String getName() {
        return mName;
    }

    public long getSizeCount() {
        return mSize;
    }

    public String getSize() {
        return AppUtils.getConvertedSize(mSize);
    }

}
