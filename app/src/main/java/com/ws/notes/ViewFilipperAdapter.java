package com.ws.notes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ViewFilipperAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mData;

    public ViewFilipperAdapter(Context context, int[] data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return mData[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView=null;
        if(convertView==null) {
            imageView=new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            convertView=imageView;
        }else {
            imageView=(ImageView) convertView;
        }
        imageView.setImageResource(mData[position]);
        return imageView;
    }
}
