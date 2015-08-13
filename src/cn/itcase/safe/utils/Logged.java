package cn.itcase.safe.utils;

import android.util.Log;

public class Logged {

	private static boolean ENABLE = true;
	private static final int LENV_V = 0;
	private static final int LENV_D = 1;
	private static final int LENV_I = 2;
	private static final int LENV_W = 3;
	private static final int LENV_E = 4;
	private static int START = 0;

	public static void d(String tag, String msg) {
		if (ENABLE) {
			return;
		}
		if (START <= LENV_D) {
			Log.d(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (ENABLE) {
			return;
		}
		if (START <= LENV_V) {
			Log.v(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (ENABLE) {
			return;
		}
		if (START <= LENV_I) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (ENABLE) {
			return;
		}
		if (START <= LENV_W) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (ENABLE) {
			return;
		}
		if (START <= LENV_E) {
			Log.e(tag, msg);
		}
	}
}
