package com.mofa.loan.pojo;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.mofa.loan.R;

public class MyDialog extends Dialog {

	  Context context;
	    public MyDialog(Context context) {
	        super(context);
	        this.context = context;
	    }
	    public MyDialog(Context context, int theme){
	        super(context, theme);
	        this.context = context;
	    }
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.pop_out_money_time);
	    }

}
