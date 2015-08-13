package cn.itcase.safe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import cn.itcase.safe.domain.ConcateItem;

public class ContaceUtils {

	/**
	 * 如果返回的list为空 , 说明没有查询到数据
	 * 
	 * @param context
	 * @return
	 */
	public static List<ConcateItem> getAllContace(Context context) {

		// 获取内容解析者
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		// 列出需要获取的内容
		String[] projection = {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		// 去数据库查询
		// 第一个空:表示查询的要求 即where从句
		// 第二个空:表示查询的where的值
		// 第三个空 : 查询后排序
		Cursor cursor = resolver.query(uri, projection, null, null, null);

		List<ConcateItem> list = new ArrayList<ConcateItem>();
		ConcateItem concateItem = null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				Long imgId = cursor.getLong(2);

				concateItem = new ConcateItem();
				concateItem.name = name;
				concateItem.phone = number;
				concateItem.imager = imgId;

				list.add(concateItem);
			}

		}
		return list;
	}

	public static Bitmap getContectIcon(Context context, Long contactId) {

		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
				contactId + "");

		InputStream in = null;
		try {
			in = ContactsContract.Contacts.openContactPhotoInputStream(
					resolver, uri);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			return bitmap;
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

	}
}
