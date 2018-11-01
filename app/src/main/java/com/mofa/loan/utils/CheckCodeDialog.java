package com.mofa.loan.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mofa.loan.R;

public class CheckCodeDialog extends Dialog {
	
	private Context mContext;
	private ImageView ivCode;
	private EditText etCode;
	private Button btnSure;
	private String checkCode;

	public CheckCodeDialog(Context context) {
		super(context, R.style.custom_dialog2);
		mContext = context;
		initview();
	}

	private void initview() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.layout_check_code_dialog, null);
		ivCode = view.findViewById(R.id.iv_code);
		etCode = view.findViewById(R.id.et_code);
		btnSure = view.findViewById(R.id.btn_check);
		ivCode.setImageBitmap(Code.getInstance().createBitmap());
		checkCode = Code.getInstance().getCode();
		super.setContentView(view);
	}
	
	 //获取当前输入框对象
    public View getEditText() {
        return etCode;
    }
    
    //获取当前输入框对象
    public View getImageView() {
    	return ivCode;
    }

    //确定键监听器
    public void setOnSureListener(View.OnClickListener listener) {
        btnSure.setOnClickListener(listener);
    }
    
    //验证码图片监听器
    public void setOnChangeListener(View.OnClickListener listener) {
    	ivCode.setOnClickListener(listener);
    }
    
    public String getCode() {
    	return checkCode;
    }
    
    public void setCode(String Code) {
    	checkCode = Code;
    }

}
