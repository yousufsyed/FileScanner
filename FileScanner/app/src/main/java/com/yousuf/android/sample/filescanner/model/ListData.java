package com.yousuf.android.sample.filescanner.model;

/**
 * @author Yousuf S. on 1/14/17.
 */

public class ListData {

    private String mName;
    private String mCount;

    public ListData(String name, String count){
        mName = name;
        mCount = count;
    }

    public String getName() {
        return mName;
    }

    public String getCount() {
        return mCount;
    }
}
