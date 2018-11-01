package com.mofa.loan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.pojo.MoneyPojo;

import java.util.ArrayList;

public class NewsAdapter2 extends BaseAdapter {

	private ArrayList<MoneyPojo> pojo;
	private LayoutInflater mInflater;

	public NewsAdapter2(Context context, ArrayList<MoneyPojo> pojo) {
		this.pojo = pojo;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public ArrayList<MoneyPojo> getArrayList() {
		return pojo;
	}

	public void setArrayList(ArrayList<MoneyPojo> arrayList) {
		this.pojo = arrayList;
	}

	@Override
	public int getCount() {
		if (pojo == null) {
			return 0;
		} else {
			return pojo.size();
		}
	}

	@Override
	public Object getItem(int arg0) {

		return pojo.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int postition, View convertView, ViewGroup arg2) {
		ViewHoler viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHoler();
			convertView = mInflater.inflate(R.layout.adapter_news2, null);
			viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
			viewHolder.tvTime= convertView.findViewById(R.id.tv_time);
			viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
			viewHolder.ivTheme = convertView.findViewById(R.id.iv_theme);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHoler) convertView.getTag();
		}
		MoneyPojo data = pojo.get(postition);
		String time = data.getTime();
		viewHolder.tvTime.setText(time.substring(0, 5) + " " + time.substring(11, 16));
		viewHolder.tvContent.setText(data.getType());
		viewHolder.tvContent.setSingleLine(true);
		viewHolder.tvTitle.setText(data.getName());
		if ("Thông báo về việc trả vay".equalsIgnoreCase(data.getName())) {
			viewHolder.ivTheme.setImageResource(R.drawable.msg);
		} else {
			viewHolder.ivTheme.setImageResource(R.drawable.review);
		}
		return convertView;
	}

	static class ViewHoler {
		public TextView tvContent,tvTime,tvTitle;
		public ImageView ivTheme;
	}

}
