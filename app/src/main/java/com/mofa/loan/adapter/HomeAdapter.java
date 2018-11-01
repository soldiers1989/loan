package com.mofa.loan.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.pojo.PersonPojo;

import java.util.ArrayList;

public class HomeAdapter extends BaseAdapter {

	private ArrayList<PersonPojo> pojo;
	private LayoutInflater mInflater;

	public HomeAdapter(Context context, ArrayList<PersonPojo> pojo) {
		this.pojo = pojo;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int arg0) {
		return pojo.get(arg0 % pojo.size());
//		return pojo.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
//		return arg0;
		if (pojo.size()==0) {
			return 0;
		}else {
			return arg0 % pojo.size();
		}
		
	}

	@Override
	public View getView(int postition, View convertView, ViewGroup arg2) {
		ViewHoler viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHoler();
			convertView = mInflater.inflate(R.layout.adapter_home, null);
			viewHolder.time = convertView.findViewById(R.id.time);
			viewHolder.content = convertView
					.findViewById(R.id.content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHoler) convertView.getTag();
		}
		if (pojo.size()==0) {
			
		}else {
			PersonPojo data = pojo.get(postition%pojo.size());
			viewHolder.time.setText(data.getCreate_date());

			viewHolder.content.setText(
					Html.fromHtml(data.getCardusername()+ "  成功申请借款   "+"<font color=\"#2375FF\">"+"￥"+data.getJk_money()+"</font>")	);
		}
		
		return convertView;
	}

	static class ViewHoler {
		public TextView time;
		public TextView content;
	}

}
