package com.mofa.loan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mofa.loan.R;

import java.util.ArrayList;

public class BankSelectAdapter extends BaseAdapter {
	
	private ArrayList<String> list;
	private LayoutInflater mInflater;
	
	public BankSelectAdapter (Context context, ArrayList lists) {
		list = lists;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.bank_item, null);
			holder = new ViewHolder();
			holder.tvBank = convertView.findViewById(R.id.tv_bank_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvBank.setText(list.get(position));
		return convertView;
	}
	
	private class ViewHolder {
		TextView tvBank;
	}

}
