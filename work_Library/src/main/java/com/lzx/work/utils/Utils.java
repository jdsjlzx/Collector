package com.lzx.work.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;

public class Utils implements AppConstant {

	// 将数据存储进入共享参数
	public static boolean saveMsg(Context context, String fileName,
			Map<String, Object> map) {
		boolean flag = false;
		// 一般Mode都使用private,比较安全
		SharedPreferences preferences = context.getSharedPreferences(fileName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		// Map类提供了一个称为entrySet()的方法，这个方法返回一个Map.Entry实例化后的对象集。
		// 接着，Map.Entry类提供了一个getKey()方法和一个getValue()方法，
		// 因此，上面的代码可以被组织得更符合逻辑
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object object = entry.getValue();
			// 根据值得不同类型，添加
			if (object instanceof Boolean) {
				Boolean new_name = (Boolean) object;
				editor.putBoolean(key, new_name);
			} else if (object instanceof Integer) {
				Integer integer = (Integer) object;
				editor.putInt(key, integer);
			} else if (object instanceof Float) {
				Float f = (Float) object;
				editor.putFloat(key, f);
			} else if (object instanceof Long) {
				Long l = (Long) object;
				editor.putLong(key, l);
			} else if (object instanceof String) {
				String s = (String) object;
				editor.putString(key, s);
			}
		}
		flag = editor.commit();
		return flag;

	}

	// 读取数据
	public static Map<String, ?> getMsg(Context context, String fileName) {
		Map<String, ?> map = null;
		// 读取数据用不到edit
		SharedPreferences preferences = context.getSharedPreferences(fileName,
				Context.MODE_APPEND);
		// Context.MODE_APPEND可以对已存在的值进行修改
		map = preferences.getAll();
		return map;
	}

	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);

		}

		return baos.toByteArray();
	}

	/**
	 * 获取当前应用程序的版本号
	 */
	public static String getVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(),
					0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号错误";
		}
	}

	public static final int BITMAP_VERIFY_SIZE_SMALL = 100;
	/**
	 * 获取图片的缩略图
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBtimap(String filePath) {

		File file = new File(filePath);
		Bitmap bitmap = null;
		if (file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[5 * 1024]; // 设置16MB的临时存储空间（不过作用还没看出来，待验证）
			// 数字越大读出的图片占用的heap越小 不然总是溢出

			if (verifyBitmap(filePath)) {
				options.inSampleSize = getInSampleSize(filePath,
						BITMAP_VERIFY_SIZE_SMALL);
			}
			// if (file.length() < 102400) { // 0-100k
			// options.inSampleSize = 1;
			// } else if (file.length() < 409600) { // 100-400k
			// options.inSampleSize = 2;
			// } else if (file.length() < 819200) { // 400-800k
			// options.inSampleSize = 4;
			// }
			//
			// else if (file.length() < 1638400) { // 800-1600k
			// options.inSampleSize = 8;
			// } else {
			// options.inSampleSize = 10;
			// }
			try {
				bitmap = BitmapFactory.decodeFile(file.getPath(), options);
			} catch (OutOfMemoryError e) {
			}

		}

		return bitmap;
	}
	
	/**
	 * 获取图片的中等缩略图
	 * @param filePath
	 * @return
	 */
	public static Bitmap getMiddleBtimap(String filePath) {

		File file = new File(filePath);
		Bitmap bitmap = null;
		if (file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[5 * 1024]; // 设置16MB的临时存储空间（不过作用还没看出来，待验证）
			// 数字越大读出的图片占用的heap越小 不然总是溢出

			try {
				bitmap = BitmapFactory.decodeFile(file.getPath(), options);
			} catch (OutOfMemoryError e) {
			}

		}

		return bitmap;
	}
	
	/**
	 * 检测是否可以解析成位图
	 * 
	 * @param path
	 * @return
	 */
	public static boolean verifyBitmap(String path) {
		try {
			return verifyBitmap(new FileInputStream(path));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 检测是否可以解析成位图
	 * 
	 * @param input
	 * @return inSampleSize
	 */
	public static boolean verifyBitmap(InputStream input) {
		if (input == null) {
			return false;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		input = input instanceof BufferedInputStream ? input
				: new BufferedInputStream(input);
		BitmapFactory.decodeStream(input, null, options);
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// println(options.outHeight + "   options.outWidth ==== "
		// + options.outWidth);
		return (options.outHeight > 0) && (options.outWidth > 0);
	}
	
	/**
	 * 获取图片压缩比例
	 * 
	 * @param path
	 * @return
	 */
	public static int getInSampleSize(String path, int size) {
		try {
			return getInSampleSize(new FileInputStream(path), size);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 获取图片压缩比例
	 * 
	 * @param path
	 * @return
	 */
	public static int getInSampleSize(InputStream input, int size) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		input = input instanceof BufferedInputStream ? input
				: new BufferedInputStream(input);
		BitmapFactory.decodeStream(input, null, options);
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return options.outHeight / size;
	}
	
	/**
	 * Try to return the absolute file path from the given Uri
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath( final Context context, final Uri uri ) {
	    if ( null == uri ) return null;
	    final String scheme = uri.getScheme();
	    String data = null;
	    if ( scheme == null )
	        data = uri.getPath();
	    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
	        data = uri.getPath();
	    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
	        Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
	        if ( null != cursor ) {
	            if ( cursor.moveToFirst() ) {
	                int index = cursor.getColumnIndex( ImageColumns.DATA );
	                if ( index > -1 ) {
	                    data = cursor.getString( index );
	                }
	            }
	            cursor.close();
	        }
	    }
	    return data;
	}
	
	//http://blog.csdn.net/jdsjlzx/article/details/44229169
	public static Bitmap rotateBitmap(Bitmap bitmap,int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress); 
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }
	
	/*
	 * MD5加密
	 */
	public static String getMD5Text(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(text.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		// 16位加密，从第9位到25位
		/*return md5StrBuff.substring(8, 24).toString().toLowerCase();*/
		//32位加密
		return md5StrBuff.toString().toLowerCase();
	}
	
	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、185、186
		电信：133、153、180、189、（1349卫通）
		总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		*/
		String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
    }
}
