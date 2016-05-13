package com.lindo.collector.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class FileUtil {

	public static boolean saveUserInfo2Rom(Context context, String mobile,String password,
			String fileName) throws IOException {
		// 以私有的方式打开一个文件
		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password)) {
			String result = mobile + ":" + password;
			fos.write(result.getBytes());
			fos.flush();
			fos.close();
		}
		return false;

	}

	public static Map<String, String> getUserInfo(Context context,
			String fileName) throws IOException {
		/*
		 * File file = new File("data/data/com.haier.jdb/files/"+fileName);
		 * FileInputStream fis = new FileInputStream(file);
		 */
		// 以上的两句代码也可以通过以下的代码实现：
		FileInputStream fis = context.openFileInput(fileName);
		byte[] data = com.lzx.work.utils.Utils.getBytes(fis);
		String content = new String(data);
		String results[] = content.split(":");
		Map<String, String> map = new HashMap<String, String>();
		if (results.length >=2 ) {
			map.put("username", results[0]);
			map.put("password", results[1]);
		}
		
		return map;
	}

	public static void copyFile(File source, File target) {
		if (!target.getParentFile().exists()) {
			target.getParentFile().mkdirs();
		}
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(target);
			in = inStream.getChannel();
			out = outStream.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				inStream.close();
				in.close();
				outStream.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static String generatePicName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS",
				Locale.CHINA);
		String filename = sdf.format(new Date(System.currentTimeMillis()))
				+ ".jpg";
		File destDir = new File(Constant.IMG_CACHE_DIR);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		String picPath = Constant.IMG_CACHE_DIR + filename;
		return picPath;
	}

	public static void compressPicture(String srcPath, String desPath) {
		FileOutputStream fos = null;
		BitmapFactory.Options op = new BitmapFactory.Options();

		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		op.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
		op.inJustDecodeBounds = false;

		// 缩放图片的尺寸
		float w = op.outWidth;
		float h = op.outHeight;
		float hh = 1024f;//
		float ww = 1024f;//
		// 最长宽度或高度1024
		float be = 1.0f;
		if (w > h && w > ww) {
			be = (float) (w / ww);
		} else if (w < h && h > hh) {
			be = (float) (h / hh);
		}
		if (be <= 0) {
			be = 1.0f;
		}
		op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, op);
		int desWidth = (int) (w / be);
		int desHeight = (int) (h / be);
		bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
		try {
			fos = new FileOutputStream(desPath);
			if (bitmap != null) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (file.exists()) {
			flag = file.delete();
		}
		return flag;
	}

	// 读取文件
	public static String readTextFromFile(File file) throws IOException {
		String text = null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			text = readTextFromInputStream(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return text;
	}
	
	//从流中读取文件
    public static String readTextFromInputStream(InputStream is) throws IOException {
    	StringBuffer sb = new StringBuffer();
    	String line;
    	BufferedReader reader = null;
    	try {
			reader = new BufferedReader( new InputStreamReader(is));
			while((line = reader.readLine()) != null){
				sb.append(line).append("\r\n");
			}
		} finally{
			if(reader != null){
				reader.close();
			}
		}
    	return sb.toString();
    }
    
  //将文本内容写入文件
    public static void writeText2File(File file, String str)throws IOException {
    	DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
    	dos.write(str.getBytes());
    }
    
}
