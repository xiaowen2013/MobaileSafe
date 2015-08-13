package cn.itcase.safe.text;

import android.test.AndroidTestCase;
import cn.itcase.safe.db.BlackDao;
import cn.itcase.safe.utils.SmsProvider;

public class TextDb extends AndroidTestCase {

	public void add() {
		BlackDao bd = new BlackDao(getContext());
		for (int i = 0; i < 100; i++) {
			bd.add("1234567"+i, 2);
		}
	}

	public void find() {
		BlackDao bd = new BlackDao(getContext());
		int add = bd.findType("15555215556");
		//assertEquals(0, add);
		System.out.println(add);
	}

	public void up() {
		BlackDao bd = new BlackDao(getContext());
		boolean add = bd.updata("1234567", 1);
		assertEquals(true, add);
	}

	public void u() {
		BlackDao bd = new BlackDao(getContext());
		boolean add = bd.delect("1234567");
		assertEquals(true, add);
	}
	public void a() {
		SmsProvider bd = new SmsProvider();
		assertEquals(true, bd.mas(getContext()));
	}
}
