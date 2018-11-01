package com.mofa.loan.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.view.MyProgressDialog;

public class ProblemActivity extends BaseActivity implements OnClickListener {

	private String url = "http://www.lvzbao.com/androidHtml/gsdcjwt_app.html";
	private WebSettings webSettings;
	private WebView home_webview;
	private MyProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem);
		initView();
		Log.i("MOFA", "Problem---onCreate");
	}

	private long timeIn;
	private long timeOut;
	
	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "Problem---onStart:" + timeIn);
	}
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "Problem---onStop:" + timeOut);
		Log.i("MOFA", "Problem---Show:" + (timeOut - timeIn));
	}

	private void initView() {
		dialog = new MyProgressDialog(ProblemActivity.this, "");
		dialog.show();
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		TextView title_txt_center = findViewById(R.id.title_txt_center);
		title_txt_center.setText(intent.getStringExtra("title"));

		findViewById(R.id.back).setOnClickListener(this);

		home_webview = findViewById(R.id.webview);
		webSettings = home_webview.getSettings();
		// 设置WebView属性，能够执行Javascript脚本
		webSettings.setJavaScriptEnabled(true);
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		home_webview.setScrollBarStyle(0);
		// 设置支持缩放
		webSettings.setBuiltInZoomControls(true);
		// home_webview.setWebChromeClient(new WebChromeClient() {
		// public void onProgressChanged(WebView view, int progress) {//
		// 载入进度改变而触发
		// if (progress == 100) {
		// dialog.dismiss();
		// }
		// super.onProgressChanged(view, progress);
		// }
		// });

		home_webview.setWebViewClient(wvc);
		home_webview.loadUrl(url);
	}

	private WebViewClient wvc = new WebViewClient() {
		@Override
		public void onReceivedSslError(WebView view,
                                       final SslErrorHandler handler, SslError error) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					ProblemActivity.this);
			builder.setMessage(R.string.warm_SSL_error);
			builder.setPositiveButton("continue",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.proceed();
						}
					});
			builder.setNegativeButton("cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.cancel();
						}
					});
			final AlertDialog dialog = builder.create();
			dialog.show();
		}

		public void onPageFinished(WebView view, String url) {
			dialog.dismiss();
		}
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void processMessage(Message message) {

	}

}
