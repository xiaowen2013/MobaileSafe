package cn.itcase.safe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class Md5Utils {

	/**
	 * 获取文件的特征码
	 * 
	 * @param in
	 * @return
	 */
	public static String deMd5(InputStream in) {

		try {
			// 获取病毒对象
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// 读取数据量
			byte[] bys = new byte[8192];
			int len = 0;
			while ((len = in.read(bys)) != -1) {
				digest.update(bys, 0, len);
			}
			// 获取读取数据的字节数组
			byte[] bs = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : bs) {
				int a = b & 0xff;
				String hexString = Integer.toHexString(a);
				if (hexString.length() == 1) {
					hexString = 0 + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;

	}
}
