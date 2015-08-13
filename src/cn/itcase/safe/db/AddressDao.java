package cn.itcase.safe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {

	private static final String TAG = null;

	/**
	 * 将需要查询的号码传进来
	 * 
	 * @param context
	 * @param nmber
	 * @return
	 */
	public static String findAddress(Context context, String number) {

		// 获取数据库的路径
		String path = new File(context.getFilesDir(), "address.db")
				.getAbsolutePath();
		// 获取数据库操作对象
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		String address = null;
		// 将号码进行判断,判断是手机还是座机
		// 1[34578]\d{9}$
		boolean isPhone = number.matches("^1[34578]\\d{9}$");
		if (isPhone) {
			// 是电话
			String prefix = number.substring(0, 7);
			String sql = "select cardtype  from info where mobileprefix = ?";
			Cursor cursor = database.rawQuery(sql, new String[] { prefix });
			if (cursor != null) {
				while (cursor.moveToNext()) {
					address = cursor.getString(0);
					return address;
				}
			}
		} else {
			// 判断输入的位数
			// 获取输入的位数
			int length = number.length();
			switch (length) {
			case 3:
				address = "紧急电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务号码";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				// 区号肯是三位或者4位
				String prefix = number.substring(0, 3);
				String sql = "select city  from info where area=?";
				Cursor cursor = database.rawQuery(sql, new String[] { prefix });
				if (cursor != null) {
					while (cursor.moveToNext()) {
						address = cursor.getString(0);
						return address;
					}
				}
				// 走到这里, 说明3位的没有
				prefix = number.substring(0, 4);
				cursor = database.rawQuery(sql, new String[] { prefix });
				if (cursor != null) {
					while (cursor.moveToNext()) {
						address = cursor.getString(0);
						return address;
					}
				}
				// 走到这里也没有,说明为未知
				return null;
			default:
				break;
			}
			
			return address;
		}
		return null;
	}
}
