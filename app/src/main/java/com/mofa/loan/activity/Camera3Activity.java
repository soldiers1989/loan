package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.Formatdou;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

@TargetApi(23)
public class Camera3Activity extends BaseActivity implements OnClickListener {
	private String userId;
	private String realname;
	private int all;
	private MyProgressDialog dialog;

	private double inverst15 = 0.0045;
	private double inverst30 = 0.009;
	private double inverst;
	private double serviceFee;
	private String day30;
	private String day15;
	private String day30_2;
	private String day15_2;
	private String TSLoanMoney;

	private String hongbaoje;

	private boolean checked;

	private TextView tvLoanMoney;
	private TextView tvLoanDays;
	private TextView tvBank;
	private TextView tvActulMoney;
	private TextView tvServiceFee;
	private TextView tvInverstFee;
	private TextView tvLoanProtocol;
	private ImageView ivCheck;
	private Button btnLoan;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera3);
		Log.i("MOFA", "Camera---onCreate");
		initview();

		SharedPreferences sp = getSharedPreferences("config", 0x0000);
		userId = sp.getString("userid", "");
		day30 = sp.getString("day30", "0");
		day15 = sp.getString("day15", "0");
		day30_2 = sp.getString("day30_2", "0");
		day15_2 = sp.getString("day15_2", "0");
		TSLoanMoney = sp.getString("TSLoanMoney", "1.0");

		hongbaoje = sp.getString("hongbaoje", "30,000");

		dialog = new MyProgressDialog(Camera3Activity.this, "");
		String jkid = getIntent().getStringExtra("jkid");
		HttpUtils.doGetAsyn(Config.BACKMONEYINIT_CORD + "&jkid="
				+ jkid + "&hv=" + MD5Util.md5(jkid), mHandler,
				Config.CODE_BACKMONEYINIT);
		Log.i("MOFA", Config.BACKMONEYINIT_CORD + "&jkid="
				+ getIntent().getStringExtra("jkid"));
	}

	private long timeIn;
	private long timeOut;

	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "Camera---onStart:" + timeIn);
	}
	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "Camera---onStop:" + timeOut);
		Log.i("MOFA", "Camera---Show:" + (timeOut - timeIn));
	}

	private void initview() {
	    mHandler = new MyHandler(this);
		tvLoanMoney = findViewById(R.id.tv_loan_money);
		tvLoanDays = findViewById(R.id.tv_loan_days);
		tvBank = findViewById(R.id.tv_bankcard);
		tvActulMoney = findViewById(R.id.tv_actul_money);
		tvServiceFee = findViewById(R.id.tv_service_fee);
		tvInverstFee = findViewById(R.id.tv_inverst_fee);
		tvLoanProtocol = findViewById(R.id.tv_loan_protocol);
		tvLoanProtocol.setOnClickListener(this);
		ivCheck = findViewById(R.id.iv_check);
		ivCheck.setOnClickListener(this);
		btnLoan = findViewById(R.id.btn_loan);
		btnLoan.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);

		TextView center = findViewById(R.id.title_txt_center);
		center.setText("Thông tin khoản vay");

		checked = true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& grantResults[1] == PackageManager.PERMISSION_GRANTED
				&& grantResults[2] == PackageManager.PERMISSION_GRANTED) {
			// 权限申请成功
			enter();
		} else {
			ToastUtil.showShort(Camera3Activity.this, getResources().getString(R.string.record_auto_permission));
			Log.i("MOFA", "权限拒绝：拍摄视频时的麦克风，录像，存储权限");
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	protected void enter() {
		new AlertDialog(Camera3Activity.this)
				.builder()
				.setMsg(getResources().getString(R.string.camera_speak1)
						+ " " + realname + " "
						+ getResources().getString(R.string.camera_speak2)
						+ " " +  Formatdou.formatdou(all + "") + " VND")
				.setNegativeButton(getResources().getString(R.string.OK),
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// finish();

								Intent intent = new Intent(Camera3Activity.this,
										Camera2Activity.class);
								intent.putExtra("name", realname);
								intent.putExtra("money",all + "");
								startActivity(intent);
							}
						}).show();
	}

	private MyHandler mHandler;

	private void setDialog(String Message) {
		new AlertDialog(Camera3Activity.this)
				.builder()
				.setMsg(Message)
				.setNegativeButton(getResources().getString(R.string.OK),
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// finish();
							}
						}).show();
	}

	private Cursor c;

	@Override
	public void processMessage(Message message) {

	}

	@Override
	protected void onDestroy() {
		if (c != null) {
			c.close();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_loan:
			if (!checked) {
				new AlertDialog(Camera3Activity.this)
						.builder()
						.setMsg(getResources().getString(
								R.string.warm_check_protocol))
						.setNegativeButton(
								getResources().getString(R.string.OK),
								new OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								}).show();
				return;
			}
			if (Build.VERSION.SDK_INT >= 23) {
				if (Camera3Activity.this
						.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
						|| Camera3Activity.this
								.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
								|| Camera3Activity.this
								.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					// 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
					Camera3Activity.this.requestPermissions(new String[] {
							android.Manifest.permission.CAMERA,
							android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
							PermissionUtils.LOCATION_PERMISSION);
				} else {// 已授权
					enter();
				}
			} else {
				enter();
			}
			break;

		case R.id.iv_check:
			if (checked) {
				checked = false;
				ivCheck.setImageResource(R.drawable.ring);
			} else {
				checked = true;
				ivCheck.setImageResource(R.drawable.round);
			}
			break;
			
		case R.id.tv_loan_protocol:
			
			break;
			
		case R.id.back:
			finish();
			break;
			
		default:
			break;
		}
	}
private class MyHandler extends Handler {
		private WeakReference<Activity> activityWeakReference;
		public MyHandler (Activity activity) {
			activityWeakReference = new WeakReference<>(activity);
		}

	@Override
	public void handleMessage(Message msg) {

        switch (msg.what) {
            case Config.CODE_ERROR:
                ToastUtil.showShort(activityWeakReference.get(), "system error");
                break;
            case Config.CODE_URL_ERROR:
                ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.url_error));

                break;
            case Config.CODE_NET_ERROR:
                ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.network_error));

                break;
            case 11:
                ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.upload_failture));

                dialog.dismiss();
                break;
            case Config.CODE_BACKMONEYINIT:
                String result = msg.obj.toString();
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int hongbaoid = jsonObject.getInt("hongbaoid");

                    all = Integer.valueOf(jsonObject
                            .getString("sjsh_money").replace(",", ""));
                    int real = Integer.valueOf(jsonObject.getString(
                            "sjds_money").replace(",", ""));
                    tvLoanMoney.setText(Formatdou.formatdou(all + "").replace(".", ","));
                    tvActulMoney.setText(getResources().getString(R.string.yuan) + Formatdou.formatdou(real + "").replace(".", ","));
                    int jk_dates = jsonObject.getInt("jk_date");
                    int annualrate = jsonObject.getInt("annualrate");
                    String cardno = jsonObject.getString("cardno");
                    tvBank.setText(cardno);

                    // double low = 0.0,fw=0.0;
                    if (Double.valueOf(TSLoanMoney)*100000 >= all*1.0) {
                        if (jk_dates == 1) {
                            inverst = all * inverst15;
                            serviceFee = all * Double.valueOf(day15_2);
                            tvLoanDays.setText(getResources()
                                    .getString(R.string.day15));
                        } else {
                            inverst = all * inverst30;
                            serviceFee = all * Double.valueOf(day30_2);
                            tvLoanDays.setText(getResources()
                                    .getString(R.string.day30));
                        }
                    } else {
                        if (jk_dates == 1) {
                            inverst = all * inverst15;
                            serviceFee = all * Double.valueOf(day15);
                            tvLoanDays.setText(getResources()
                                    .getString(R.string.day15));
                        } else {
                            inverst = all * inverst30;
                            serviceFee = all * Double.valueOf(day30);
                            tvLoanDays.setText(getResources()
                                    .getString(R.string.day30));
                        }
                    }
                    tvInverstFee.setText(getResources().getString(R.string.yuan) + Formatdou.formatdou(inverst + "").replace(".", ","));
                    if (hongbaoid == 1) {
                        tvServiceFee.setText(Html.fromHtml(getResources().getString(R.string.yuan) + Formatdou.formatdou(serviceFee + "").replace(".", ",") + "<font color=\"#DA3636\"> - " + "VND " + hongbaoje + " </font>"));
                    } else {
                        tvServiceFee.setText(getResources().getString(R.string.yuan) + Formatdou.formatdou(serviceFee + "").replace(".", ","));
                    }
                    realname = jsonObject.getString("realname");

                } catch (JSONException e) {
                    Log.e("MOFA", e.getMessage());
                }
                break;
            case Config.CODE_VIDEO:
                String result2 = msg.obj.toString();
                try {
                    JSONObject json = new JSONObject(result2);
                    if (json.getInt("err") == 0) {
                        // tv.setText(getResources().getString(R.string.upload_successed));
                        // pb.setVisibility(ProgressBar.GONE);
                        startActivity(new Intent(activityWeakReference.get(),
                                IndexActivity.class));
                        finish();
                        dialog.dismiss();
                    } else {
                        // setDialog(json.getString("msg"));
                        new AlertDialog(activityWeakReference.get())
                                .builder()
                                .setMsg(json.getString("msg"))
                                .setNegativeButton(
                                        getResources().getString(R.string.OK),
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        }).show();
                    }

                } catch (JSONException e) {
                    ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.data_parsing_error));

                    Log.e("MOFA", e.getMessage());
                }

                break;
            default:
                break;
        }
    }
}
}