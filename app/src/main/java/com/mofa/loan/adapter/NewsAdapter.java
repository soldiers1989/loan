package com.mofa.loan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.pojo.MoneyPojo;

import java.util.ArrayList;

public class NewsAdapter extends BaseAdapter {

	private ArrayList<MoneyPojo> pojo;
	private LayoutInflater mInflater;
	private Context mContext;

	public NewsAdapter(Context context, ArrayList<MoneyPojo> pojo) {
		this.pojo = pojo;
		this.mInflater = LayoutInflater.from(context);
		mContext = context;
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
			convertView = mInflater.inflate(R.layout.adapter_news, null);
			viewHolder.txt_content = convertView.findViewById(R.id.txt_content);
			viewHolder.txt_result= convertView.findViewById(R.id.txt_result);
			viewHolder.txt_title = convertView.findViewById(R.id.txt_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHoler) convertView.getTag();
		}
		MoneyPojo data = pojo.get(postition);
		viewHolder.txt_result.setText(mContext.getResources().getString(R.string.app_name) + "    "+data.getTime());
		viewHolder.txt_content.setText(data.getType());
		viewHolder.txt_title.setText(data.getName());
		return convertView;
	}

	static class ViewHoler {
		public TextView txt_content,txt_result,txt_title;
	}

}
