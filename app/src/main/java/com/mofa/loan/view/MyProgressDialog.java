package com.mofa.loan.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mofa.loan.R;


public class MyProgressDialog extends ProgressDialog {
	private ImageView lading;
	private TextView lading_text;
	private String string;
	private AnimationDrawable rocketAnimation;

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public MyProgressDialog(Context context, String string) {
		super(context);
		this.string = string;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_progress_dialog);
		lading = findViewById(R.id.lading);
		lading_text = findViewById(R.id.lading_text);
		rocketAnimation = (AnimationDrawable) lading.getBackground();
		lading_text.setText(string);
		this.setCanceledOnTouchOutside(false);
		this.setCancelable(false);
	}

	@Override
	public void show() {
		super.show();
		if (rocketAnimation != null) {
			rocketAnimation.start();
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (rocketAnimation != null) {
			rocketAnimation.stop();
		}
	}
}
