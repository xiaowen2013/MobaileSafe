package cn.itcase.safe.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class GZipUtils {

	/**
	 * 将一个zip解压成一个文件
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @return
	 * @throws IOException
	 */
	public static void unZip(InputStream in, OutputStream out)
			throws IOException {

		GZIPInputStream gzip = null;
		try {
			gzip = new GZIPInputStream(in);
			//gis = new GZIPInputStream(is);
			int len = 0;
			byte[] bys = new byte[1024];
			while ((len = gzip.read(bys)) != -1) {
				out.write(bys, 0, len);
			}
			return;
		} finally{
			close(gzip);
			close(out);
		}
	}

	private static void close(Closeable is) {
		if(is!=null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
