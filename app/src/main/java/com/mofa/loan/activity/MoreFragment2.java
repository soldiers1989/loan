package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.mofa.loan.R;
import com.mofa.loan.utils.BitmapUtils;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ConnectionDetector;
import com.mofa.loan.utils.LogcatHelper;
import com.mofa.loan.utils.ToastUtil;

import org.apache.commons.lang.math.RandomUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@TargetApi(23)
public class MoreFragment2 extends Fragment implements OnClickListener {

    private View view;
    private Activity ac;
    private int rzstatus;
    private android.app.AlertDialog phoneDialog;
    private ClipboardManager cmb;
    private String facebookName, facebookPictrue;
    private TextView tv;
    private ImageView iv;

    private String userId;

    private final int GET_PICTRUE = 30300;
    private final int GET_PICTRUE_FAILED = 30301;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_PICTRUE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int outWidth = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 50, ac.getResources()
                                    .getDisplayMetrics());
                    int outHeight = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 50, ac.getResources()
                                    .getDisplayMetrics());
                    int radius = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 25, ac.getResources()
                                    .getDisplayMetrics());
                    iv.setImageBitmap(BitmapUtils.roundBitmapByShader(bitmap,
                            outWidth, outHeight, radius));
                    Log.i("MOFA", "Facebook头像获取成功！");
                    break;
                case GET_PICTRUE_FAILED:
                    Log.i("MOFA", "Facebook头像获取失败！");
                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(ac,
                            getResources().getString(R.string.url_error));

                    Log.i("MOFA",
                            "Morefragment"
                                    + getResources().getString(R.string.url_error));
                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(ac,
                            getResources().getString(R.string.network_error));
                    Log.i("MOFA",
                            "Morefragment"
                                    + getResources().getString(
                                    R.string.network_error));
                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(ac, "system error");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_more2, container, false);
            ac = getActivity();
            SharedPreferences sp = ac.getSharedPreferences("config", 0x0000);
            String phone = sp.getString("phone", "");
            userId = sp.getString("userid", "");
            rzstatus = sp.getInt("rzstatus", 0);
            initView();
            cmb = (ClipboardManager) getActivity().getSystemService(
                    Context.CLIPBOARD_SERVICE);
            // initData();
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        Log.i("MOFA", "MoreFragment2---onCreate");
        return view;
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "MoreFragment2---onStart:" + timeIn);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "MoreFragment2---onStop:" + timeOut);
        Log.i("MOFA", "MoreFragment2---Show:" + (timeOut - timeIn));
    }

    @Override
    public void onResume() {
        Log.i("MOFA", "MoreFragment2---onResume:" + System.currentTimeMillis());
        super.onResume();
    }

    private void initView() {
        view.findViewById(R.id.ll_pay_information).setOnClickListener(this);
        view.findViewById(R.id.ll_bank_information).setOnClickListener(this);
        view.findViewById(R.id.ll_news).setOnClickListener(this);
        view.findViewById(R.id.ll_share).setOnClickListener(this);
        view.findViewById(R.id.ll_hongbao).setOnClickListener(this);
        view.findViewById(R.id.ll_problem).setOnClickListener(this);
        view.findViewById(R.id.ll_time).setOnClickListener(this);
        view.findViewById(R.id.ll_sendlog).setOnClickListener(this);
        view.findViewById(R.id.ll_service).setOnClickListener(this);

        SharedPreferences sp = ac.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        int isFacebook = sp.getInt("isfacebook", 0);
        if (isFacebook == 0) {

        } else {
            facebookName = sp.getString("facebookname", "MOFA");
            facebookPictrue = sp.getString("facebookpictureUrl", "MOFA");
            tv = view.findViewById(R.id.tv_facebook_name);
            tv.setText(facebookName);
            iv = view.findViewById(R.id.iv_facebook_pictrue);

            boolean fileExist = false;
            if (Build.VERSION.SDK_INT >= 23) {
                if (ac.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限

                } else {// 已授权
                    // getBestLocation();
                    fileExist = getBitmap();
                }
            } else {
                fileExist = getBitmap();
                // getBestLocation();
            }

            if (!"MOFA".equalsIgnoreCase(facebookPictrue) && !fileExist) {
                getHttpBitmap(facebookPictrue);
            }
        }
    }

    private boolean getBitmap() {
        String CACHE_PATH = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MOFA/Facebook";
        File file = new File(CACHE_PATH, "pic.jpg");

        try {
            Bitmap bitmap = BitmapFactory
                    .decodeStream(new FileInputStream(file));
            if (bitmap != null) {
                int outWidth = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 50, ac.getResources()
                                .getDisplayMetrics());
                int outHeight = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 50, ac.getResources()
                                .getDisplayMetrics());
                int radius = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 25, ac.getResources()
                                .getDisplayMetrics());
                iv.setImageBitmap(BitmapUtils.roundBitmapByShader(bitmap,
                        outWidth, outHeight, radius));
                return true;
            }
        } catch (FileNotFoundException e) {
            Log.e("MOFA", e.getMessage());
        }
        return false;
    }

    /**
     * 从服务器取图片
     *
     * @param url
     * @return
     */
    public void getHttpBitmap(final String url) {
        new Thread() {
            public void run() {
                URL myFileUrl = null;
                Bitmap bitmap = null;
                try {
                    myFileUrl = new URL(facebookPictrue);
                } catch (MalformedURLException e) {
                    Log.i("MOFA", "MoreFragment2---facebook头像路径错误：" + url);
                    Log.e("MOFA", e.getMessage());
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                            .openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        String dir = Environment.getExternalStorageDirectory()
                                .toString() + "/MOFA/Facebook";
                        File path = new File(dir);
                        if (!path.exists()) {
                            path.mkdirs();
                        }
                        File file = new File(path, "pic.jpg");
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Log.e("MOFA", e.getMessage());
                        } finally {
                            if (!file.exists()) {
                                ToastUtil.showShort(ac, "create file failed");
                            }
                        }
                        String filepath = file.getPath();
                        FileOutputStream fos = new FileOutputStream(filepath);
                        fos.write(baos.toByteArray());
                        fos.flush();
                        fos.close();
                        Message msg = Message.obtain();
                        msg.what = GET_PICTRUE;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                        is.close();
                    } else {
                        Message msg = Message.obtain();
                        msg.what = GET_PICTRUE_FAILED;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    Log.i("MOFA",
                            "MoreFragment2---facebook头像获取错误：" + e.getMessage());
                    Log.e("MOFA", e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        ConnectionDetector cd = new ConnectionDetector(ac);
        if (cd.isConnectingToInternet()) {
            switch (v.getId()) {
                case R.id.ll_pay_information:
                    Intent intent = new Intent(ac, BankPayActivity.class);
                    startActivity(intent);
                    break;

                case R.id.ll_bank_information:
                    SharedPreferences sp = ac
                            .getSharedPreferences("config", 0x0000);
                    int isyhbd = sp.getInt("isyhbd", 0);
                    if (isyhbd != 1) {
                        // Intent intent2 = new Intent(ac, BindCardActivity.class);
                        // startActivity(intent2);
                        new com.mofa.loan.utils.AlertDialog(ac)
                                .builder()
                                .setMsg(getResources().getString(
                                        R.string.bind_bankcard))
                                .setCancelable(false)
                                .setNegativeButton(
                                        getResources().getString(R.string.OK),
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        }).show();
                    } else {
                        Intent intent2 = new Intent(ac,
                                BankCardManageActivity.class);
                        startActivity(intent2);
                    }
                    break;

                case R.id.ll_news:
                    Intent intent2 = new Intent(ac, NewsActivity.class);
                    intent2.putExtra("from", "more");
                    startActivity(intent2);
                    break;

                case R.id.ll_share:
                    // Intent intentShare = new Intent(Intent.ACTION_SEND);
                    // intentShare.setType("text/plain");
                    // intentShare.putExtra(Intent.EXTRA_SUBJECT, "MOFA");
                    // intentShare.putExtra(Intent.EXTRA_TEXT,
                    // "http://olava.com.vn/?tgid=007");
                    // startActivity(Intent.createChooser(intentShare, "Chia sẻ"));
                    Intent intentshare = new Intent(ac, MoneyDetailActivity.class);
                    startActivity(intentshare);
                    break;

                case R.id.ll_hongbao:
                    Intent intent3 = new Intent(ac, HongbaoActivity.class);
                    intent3.putExtra("from", "more");
                    startActivity(intent3);
                    break;

                case R.id.ll_problem:
                    Intent intent4 = new Intent(ac, ProblemActivity.class);
                    intent4.putExtra("title",
                            getResources().getString(R.string.privacy));
                    intent4.putExtra("url", Config.PRIVACY_CODE);
                    startActivity(intent4);
                    break;

                case R.id.ll_time:
                    Intent intent5 = new Intent(ac, OutMoneyRecord3Activity.class);
                    intent5.putExtra("type", "more");
                    startActivity(intent5);
                    break;

                case R.id.ll_sendlog:
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ac.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
                            // ac.requestPermissions(new String[] {
                            // android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            // PermissionUtils.CAMERA_PERMISSION);
                            ToastUtil.showShort(ac, "no permission!");
                        } else {// 已授权
                            setDialog();
                        }
                    } else {
                        setDialog();
                    }
                    break;

                case R.id.ll_service:
                    this.phoneDialog = new android.app.AlertDialog.Builder(ac)
                            .create();
                    LayoutInflater lay2 = LayoutInflater.from(ac);
                    final View inflate2 = lay2.inflate(
                            R.layout.layout_service_dialog, null);
                    inflate2.findViewById(R.id.btn_call).setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // 拨打电话
                                    phoneDialog.dismiss();
                                    boolean a = RandomUtils.nextBoolean();
                                    if (a) {
                                        startActivity(new Intent(Intent.ACTION_DIAL,
                                                Uri.parse("tel:028-73008816")));
                                    } else {
                                        startActivity(new Intent(Intent.ACTION_DIAL,
                                                Uri.parse("tel:028-71009888")));
                                    }
                                }
                            });
                    inflate2.findViewById(R.id.btn_copy_zalo).setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    phoneDialog.dismiss();
                                    cmb.setPrimaryClip(ClipData.newPlainText(null,
                                            "0906608816"));
                                    ToastUtil.showShort(ac, getResources()
                                            .getString(R.string.copy));
                                }
                            });
                    inflate2.findViewById(R.id.btn_copy_facebook)
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    phoneDialog.dismiss();
                                    if (SplashActivity.isAvilible(ac,
                                            "com.facebook.katana")) {
                                        Intent intent1 = new Intent(
                                                Intent.ACTION_VIEW);
                                        intent1.setData(Uri
                                                .parse("fb://page/208936099910136"));
                                        // intent1.setData(Uri.parse(getFacebookPageURL(ac)));
                                        ac.startActivity(intent1);
                                    } else {
                                        cmb.setPrimaryClip(ClipData.newPlainText(
                                                null, "01255949981"));
                                        ToastUtil.showShort(ac, getResources()
                                                .getString(R.string.copy));
                                    }
                                }
                            });
                    inflate2.findViewById(R.id.btn_copy_email).setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    phoneDialog.dismiss();
                                    cmb.setPrimaryClip(ClipData.newPlainText(null,
                                            "hotro@olava.com.vn"));
                                    ToastUtil.showShort(ac, getResources()
                                            .getString(R.string.copy));
                                }
                            });
                    inflate2.findViewById(R.id.iv_cancle).setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    phoneDialog.dismiss();
                                }
                            });
                    this.phoneDialog.show();
                    this.phoneDialog.setCanceledOnTouchOutside(false);
                    this.phoneDialog.setContentView(inflate2);
                    break;

                default:
                    break;
            }
        } else {
            ToastUtil.showShort(ac,
                    getResources().getString(R.string.network_error));
        }
    }

    private void setDialog() {
        final SharedPreferences sp = ac.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        final long currentTime = System.currentTimeMillis();
        long sendLogTime = sp.getLong("sendLogTime",
                currentTime - 3L * 3600 * 1000);
        long chaTime = currentTime - sendLogTime;

        boolean cha = chaTime >= 3L * 3600 * 1000;
        if (!cha) {
            // long a = (3L * 3600 * 1000 - chaTime)/(1000L*3600) + 1;
            // ToastUtil.showShort(ac, a + "小时后可再次发送日志！");
            Log.i("MOFA", "重复发送日志！");
            return;
        }
        new com.mofa.loan.utils.AlertDialog(ac)
                .builder()
                .setMsg("Gửi nhật ký lỗi?")
                .setCancelable(true)
                .setNegativeButton(getResources().getString(R.string.OK),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String endpoint = "http://oss-cn-hongkong.aliyuncs.com";
                                // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的访问控制章节
                                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                                        "LTAIZmB1IQUSkHvU",
                                        "WZFGiWk1ferhNhdC2ZiFm76L0xYqoP");
                                ClientConfiguration conf = new ClientConfiguration();
                                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                                conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
                                conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
                                OSSClient oss = new OSSClient(ac
                                        .getApplicationContext(), endpoint,
                                        credentialProvider, conf);

                                String path2 = Environment
                                        .getExternalStorageDirectory()
                                        .toString()
                                        + "/MOFA/Log";
                                File path1 = new File(path2);
                                File log = new File(path1, "Log-mofa-"
                                        + LogcatHelper.getFileName()
                                        + ".logger");
                                if (!log.exists()) {
                                    ToastUtil.showShort(ac,
                                            "no file, exist app and retry");
                                    return;
                                }

                                String path = log.getPath();
                                String name = userId + "-" + log.getName();
                                PutObjectRequest put = new PutObjectRequest(
                                        "andlogger", name, path);

                                // 文件元信息的设置是可选的
                                ObjectMetadata metadata = new ObjectMetadata();
                                metadata.setContentType("application/octet-stream"); // 设置content-type
                                try {
                                    metadata.setContentMD5(BinaryUtil
                                            .calculateBase64Md5(path));
                                } catch (IOException e) {
                                    Log.e("MOFA", e.getMessage());
                                } // 校验MD5
                                put.setMetadata(metadata);

                                // 异步上传时可以设置进度回调
                                put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                                    @Override
                                    public void onProgress(
                                            PutObjectRequest request,
                                            long currentSize, long totalSize) {
                                        Log.i("MOFA", "currentSize: "
                                                + currentSize + " totalSize: "
                                                + totalSize);
                                    }
                                });

                                OSSAsyncTask task = oss
                                        .asyncPutObject(
                                                put,
                                                new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                                    @Override
                                                    public void onSuccess(
                                                            PutObjectRequest request,
                                                            PutObjectResult result) {
                                                        Log.i("MOFA",
                                                                "UploadSuccess");
                                                        // handler.sendEmptyMessage(0);
                                                        // dialog.dismiss();
                                                        Editor editor = sp
                                                                .edit();
                                                        editor.putLong(
                                                                "sendLogTime",
                                                                currentTime);
                                                        editor.commit();
                                                        ToastUtil
                                                                .showShort(ac,
                                                                        "Upload Success!");
                                                    }

                                                    @Override
                                                    public void onFailure(
                                                            PutObjectRequest request,
                                                            ClientException clientExcepion,
                                                            ServiceException serviceException) {

                                                        // 请求异常
                                                        if (clientExcepion != null) {
                                                            // 本地异常如网络异常等
                                                            clientExcepion
                                                                    .printStackTrace();
                                                        }
                                                        if (serviceException != null) {
                                                            // 服务异常
                                                            Log.e("MOFA",
                                                                    "ErrorCode"
                                                                            + serviceException
                                                                            .getErrorCode());
                                                            Log.e("MOFA",
                                                                    "RequestId"
                                                                            + serviceException
                                                                            .getRequestId());
                                                            Log.e("MOFA",
                                                                    "HostId"
                                                                            + serviceException
                                                                            .getHostId());
                                                            Log.e("MOFA",
                                                                    "RawMessage"
                                                                            + serviceException
                                                                            .getRawMessage());
                                                        }
                                                    }
                                                });
                            }
                        }).show();
    }

}
