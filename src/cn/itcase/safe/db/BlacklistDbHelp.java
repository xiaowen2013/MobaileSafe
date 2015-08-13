package cn.itcase.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlacklistDbHelp extends SQLiteOpenHelper {

	private static final String TAG = "BlacklistDbHelp";

	public BlacklistDbHelp(Context context) {

		super(context, BlackListDb.DB_NAME, null, BlackListDb.version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = BlackListDb.BlackList.SQL;
		Log.d(TAG, sql + "创建数据库的语句");
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
