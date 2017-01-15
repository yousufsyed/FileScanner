package com.yousuf.android.sample.filescanner.model;

/**
 * Created by u471637 on 1/14/17.
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
