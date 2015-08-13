package cn.itcase.safe.db;

public interface BlackListDb {

	String DB_NAME = "black.db";
	int version = 1;

	public interface BlackList {
		String TABLE_NAME = "black_list";
		String COLUMN_NUMBER = "number";
		String COLUMN_TYPE = "type";
		String COLUMN_ID = "_id";

//		create tableblack_list(_idinteger primary key 
//		autoincrement,number text unique,type integer)
		
		String SQL = "create table " + TABLE_NAME + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_NUMBER
				+ " text unique ," + COLUMN_TYPE + " integer" + ")";
	}

}











