package com.yousuf.android.sample.filescanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yousuf.android.sample.filescanner.R;
import com.yousuf.android.sample.filescanner.model.ListData;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by u471637 on 1/14/17.
 */

public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    private Set<Integer> mHeaderSet;

    private ArrayList<ListData> mListData;

    public SectionAdapter() {
        mHeaderSet = new TreeSet<>();
        mListData = new ArrayList<>();
    }

    public void reset() {
        mListData.clear();
        mHeaderSet.clear();
    }

    public void addItem(String item, String count) {
        mListData.add(new ListData(item, count));
    }

    public void addHeader(String title) {
        mListData.add(new ListData(title, null));
        mHeaderSet.add(mListData.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
            return new SectionAdapter.HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new SectionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ListData data = mListData.get(position);
        Log.v("SECTION_HEADER", "position: " + position);
        if (isHeader(position)) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.mTitle.setText(data.getName());
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.mName.setText(data.getName());
            itemViewHolder.mCount.setText(data.getCount());
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    private boolean isHeader(int position) {
        return mHeaderSet.contains(position);
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mCount;

        ItemViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.name);
            mCount = (TextView) view.findViewById(R.id.count);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;

        HeaderViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
        }
    }
}
