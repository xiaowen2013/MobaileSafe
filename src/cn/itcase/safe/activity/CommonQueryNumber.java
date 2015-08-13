package cn.itcase.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import cn.itcase.safe.db.CommonQueryDao;

public class CommonQueryNumber extends Activity {

	private ExpandableListView mElv;
	private int mCurrentOpenPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.commonquerynumber);
		mElv = (ExpandableListView) findViewById(R.id.common_elv);
		mElv.setAdapter(new CommonExpandable());
		// 设置监听事件setOnChildClickListener  OnChildClickListener
		mElv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				// 点中的某个条目
				String[] childrenText = CommonQueryDao.getChildrenText(
						getApplicationContext(), groupPosition, childPosition);
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + childrenText[0]));
				startActivity(intent);
				return true;
			}
		});
		// 设置点击事件  
		mElv.setOnGroupClickListener (new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
				if(mCurrentOpenPosition == -1){
					// 一个也没有打开,打开自己
					mElv.expandGroup(groupPosition);
					// 存储当前状态
					mCurrentOpenPosition = groupPosition;
					mElv.setSelectedGroup(groupPosition);
				}else{
					// 有打开
					// 判断点击的是不是自己
					if(mCurrentOpenPosition == groupPosition){
						// 是自己,关闭它
						mElv.collapseGroup(groupPosition);
						// 标记当前没有打开
						mCurrentOpenPosition = -1;
					}else{
						// 不是自己,关闭自己,再打开当前
						mElv.collapseGroup(mCurrentOpenPosition);
						mElv.expandGroup(groupPosition);
						mElv.setSelectedGroup(groupPosition);
						mCurrentOpenPosition = groupPosition;
					}
				}
				return true;
			}
		});
	}

	private class CommonExpandable extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {

			return CommonQueryDao.getGroupCount(getApplicationContext());
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return CommonQueryDao.getChildrenCount(getApplicationContext(),
					groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			TextView tv = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());

				tv = (TextView) convertView;

				tv.setTextSize(20f);

				tv.setPadding(12, 12, 12, 12);

				tv.setBackgroundColor(Color.parseColor("#33000000"));

				tv.setTextColor(Color.BLACK);

			} else {
				tv = (TextView) convertView;
			}

			String groupText = CommonQueryDao.getGroupText(
					getApplicationContext(), groupPosition);
			tv.setText(groupText);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			TextView tv = null;
			if (convertView == null) {

				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;
				tv.setTextColor(Color.BLACK);
				tv.setTextSize(17f);
				tv.setPadding(8, 8, 8, 8);

			} else {

				tv = (TextView) convertView;
			}

			String[] childrenText = CommonQueryDao.getChildrenText(
					getApplicationContext(), groupPosition, childPosition);

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < childrenText.length; i++) {
				sb.append(childrenText[i]);
				if (i != childrenText.length - 1) {
					sb.append("\n");
				}
			}

			tv.setText(sb.toString());
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
	}

}