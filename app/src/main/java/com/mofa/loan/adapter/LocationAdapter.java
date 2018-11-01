package com.mofa.loan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mofa.loan.R;

import java.util.ArrayList;

public class LocationAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private ArrayList<String> mList;

	public LocationAdapter (Context context, ArrayList list) {
		mList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_location, null);
			holder.tv = convertView.findViewById(R.id.location);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv.setText(mList.get(position));
		return convertView;
	}
	
	private class ViewHolder {
		private TextView tv;
	}

}
