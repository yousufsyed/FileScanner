package com.yousuf.android.sample.filescanner.model;

import com.yousuf.android.sample.filescanner.utils.AppUtils;
import java.io.Serializable;

/**
 * @author Yousuf S. on 1/12/17.
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
    public int compareTo(MFile fileInfo) {
        if (mSize > fileInfo.mSize) {
            return 1;
        } else if (mSize < fileInfo.mSize) {
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
