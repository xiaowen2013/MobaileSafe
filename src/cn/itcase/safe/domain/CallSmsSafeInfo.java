package cn.itcase.safe.domain;

public class CallSmsSafeInfo {

	public String phoneNumber;
	public int type;
	
	public final static int TYPE_NUMBER = 1;
	public final static int TYPE_SMS = 2;
	public final static int TYPE_ALL = 3;
}
