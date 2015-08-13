package cn.itcase.safe.activity;

import android.content.Intent;
import android.os.Bundle;

public class LostSetup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup1);
	}
	@Override
	protected boolean performPre() {
		return true;
	}

	@Override
	protected boolean performNext() {
		Intent intent = new Intent(this, LostSetup2Activity.class);
		startActivity(intent);
		return false;
	}

}
