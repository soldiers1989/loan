package com.mofa.loan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.activity.CameraActivity;
import com.mofa.loan.pojo.MoneyPojo;

import java.util.ArrayList;

public class Out_MoneyAdapter2 extends BaseAdapter {

	private ArrayList<MoneyPojo> pojo;
	private LayoutInflater mInflater;
	private Context context;
	public Out_MoneyAdapter2(Context context, ArrayList<MoneyPojo> pojo) {
		this.pojo = pojo;
		this.context = context;
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
			convertView = mInflater.inflate(R.layout.adapter_out_money2, null);
			viewHolder.borrow_money = convertView.findViewById(R.id.tv_money);
			viewHolder.borrow_time = convertView
					.findViewById(R.id.tv_time);
			viewHolder.borrow_type= convertView.findViewById(R.id.tv_title);
			viewHolder.borrow_date= convertView.findViewById(R.id.tv_day);
			viewHolder.borrow_Status= convertView.findViewById(R.id.iv_type);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHoler) convertView.getTag();
		}
		final MoneyPojo data = pojo.get(postition);
		viewHolder.borrow_time.setText(data.getTime().substring(0, 5) + " " + data.getTime().substring(11, 16));
		viewHolder.borrow_money.setText("₫ " + " " + data.getMoney());
		viewHolder.borrow_date.setText(data.getDate());
		if (context.getResources().getString(R.string.loan_check).equalsIgnoreCase(data.getType()) || context.getResources().getString(R.string.waiting_for_the_loan).equalsIgnoreCase(data.getType())) {
			viewHolder.borrow_Status.setImageResource(R.drawable.redround);
		} else {
			viewHolder.borrow_Status.setImageResource(R.drawable.blueround);
		}
		viewHolder.borrow_type.setText(data.getType());
		viewHolder.borrow_type.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (data.getType().equals(context.getResources().getString(R.string.upload_video))) {
					Intent intentv = new Intent(context, CameraActivity.class);
					intentv.putExtra("jkid", data.getId()+"");
					context.startActivity(intentv);
				}				
			}
		});
		return convertView;
	}

	static class ViewHoler {
		public TextView borrow_money,borrow_type,borrow_date;
		public TextView borrow_time;
		public ImageView borrow_Status;
	}

}
