package com.mofa.loan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

	public BitmapUtils() {

	}

	// 图片按比例大小压缩方法（根据路径获取图片并压缩）：
	public Bitmap getSmallBitmap(File file, int reqWidth, int reqHeight) {
		try {
			String filePath = file.getAbsolutePath();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;// 开始读入图片，此时把options.inJustDecodeBounds
												// 设回true了
			BitmapFactory.decodeFile(filePath, options);// 此时返回bm为空
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);// 设置缩放比例
			options.inJustDecodeBounds = false;// 重新读入图片，注意此时把options.inJustDecodeBounds
												// 设回false了
			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			// 压缩好比例大小后不进行质量压缩
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到bos中
			// 压缩好比例大小后再进行质量压缩
			compressImage2(bitmap, filePath);
			return bitmap;
		} catch (Exception e) {
			Log.e("MOFA", "类:" + this.getClass().getName() + " 方法："
					+ Thread.currentThread().getStackTrace()[0].getMethodName()
					+ " 异常 " + e);
			Log.e("MOFA", e.getMessage());
			return null;
		}
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		try {
			int height = options.outHeight;
			int width = options.outWidth;
			int inSampleSize = 1; // 1表示不缩放
			if (height > reqHeight || width > reqWidth) {
				int heightRatio = Math
						.round((float) height / (float) reqHeight);
				int widthRatio = Math.round((float) width / (float) reqWidth);
				inSampleSize = heightRatio < widthRatio ? heightRatio
						: widthRatio;
			}
			return inSampleSize;
		} catch (Exception e) {
			Log.e("MOFA", "类:" + this.getClass().getName() + " 方法："
					+ Thread.currentThread().getStackTrace()[0].getMethodName()
					+ " 异常 " + e);
			Log.e("MOFA", e.getMessage());
			return 1;
		}
	}

	// 质量压缩法：
	public static Bitmap compressImage2(Bitmap image, String filepath) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			int count = 0;
			while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				options -= 20;// 每次都减少20
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				Log.i("MOFA", "BitmapUtil -> compressImage2---count:" + count ++);
			}
			// 压缩好后写入文件中
			FileOutputStream fos = new FileOutputStream(filepath);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
			return image;
		} catch (IOException e) {
			Log.e("MOFA", e.getMessage());
			return null;
		}
	}

	// 质量压缩法：
	public static Bitmap compressImage(Bitmap image) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				options -= 30;// 每次都减少20
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				if (options <= 0) {
					break;
				}

			}
			// 压缩好后写入文件中
			// FileOutputStream fos = new FileOutputStream(filepath);
			// fos.write(baos.toByteArray());
			// fos.flush();
			// fos.close();
			baos.close();
		} catch (IOException e) {
			Log.e("MOFA", e.getMessage());
		}
		return image;
	}
	
	/**
	 * 利用BitmapShader绘制圆角图片
	 *
	 * @param bitmap
	 *              待处理图片
	 * @param outWidth
	 *              结果图片宽度，一般为控件的宽度
	 * @param outHeight
	 *              结果图片高度，一般为控件的高度
	 * @param radius
	 *              圆角半径大小
	 * @return
	 *              结果图片
	 */
	public static Bitmap roundBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius) {
	    if(bitmap == null) {
	        throw new NullPointerException("Bitmap can't be null");
	    }
	    // 初始化缩放比
	    float widthScale = outWidth * 1.0f / bitmap.getWidth();
	    float heightScale = outHeight * 1.0f / bitmap.getHeight();
	    Matrix matrix = new Matrix();
	    matrix.setScale(widthScale, heightScale);

	    // 初始化绘制纹理图
	    BitmapShader bitmapShader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
	    // 根据控件大小对纹理图进行拉伸缩放处理
	    bitmapShader.setLocalMatrix(matrix);

	    // 初始化目标bitmap
	    Bitmap targetBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);

	    // 初始化目标画布
	    Canvas targetCanvas = new Canvas(targetBitmap);

	    // 初始化画笔
	    Paint paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setShader(bitmapShader);

	    // 利用画笔将纹理图绘制到画布上面
	    targetCanvas.drawRoundRect(new RectF(0, 0, outWidth, outWidth), radius, radius, paint);

	    return targetBitmap;
	}

}
