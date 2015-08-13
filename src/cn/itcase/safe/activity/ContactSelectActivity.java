package cn.itcase.safe.activity;

import java.util.ArrayList;
import java.util.List;

import cn.itcase.safe.domain.ConcateItem;
import cn.itcase.safe.utils.ContaceUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactSelectActivity extends Activity {

	private ListView con_lv;
	private List<ConcateItem> list;
	protected static final String TAG = "ContactSelectActivity";
	public static final String KEY_NUM = "key_num";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cantactactivity);
		con_lv = (ListView) findViewById(R.id.contact_lv);

		list = new ArrayList<ConcateItem>();

		// 去查询所有的联系人, 并返回给List
		list = ContaceUtils.getAllContace(this);
		con_lv.setAdapter(new ConAdapter());
	}

	static class ViewHandler {
		ImageView iv;
		TextView con_name;
		TextView con_phonenum;
	}

	private class ConAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		/**
		 * 谷歌推荐的写法
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHandler handler = null;
			if (convertView == null) {
				convertView = View.inflate(ContactSelectActivity.this,
						R.layout.contactitem, null);

				handler = new ViewHandler();
				handler.iv = (ImageView) convertView.findViewById(R.id.con_iv);
				handler.con_name = (TextView) convertView
						.findViewById(R.id.con_tv_name);
				handler.con_phonenum = (TextView) convertView
						.findViewById(R.id.con_tv_phonenum);
				convertView.setTag(handler);
			} else {
				handler = (ViewHandler) convertView.getTag();
			}

			handler.con_name.setText(list.get(position).name);
			handler.con_phonenum.setText(list.get(position).phone);

			// 下面的这个语句是可以获取联系人图片的
			Bitmap bitmap = ContaceUtils.getContectIcon(
					ContactSelectActivity.this, list.get(position).imager);
			if(bitmap != null){
				handler.iv.setImageBitmap(bitmap);
			}else{
				handler.iv.setImageResource(R.drawable.ic_contact);
			}
			
			// 设置监听器
			con_lv.setOnItemClickListener(new ConOnItem());

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			if (list != null) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	/**
	 * 点击了item , 将item 的值返回去
	 * 
	 * @author 温
	 * 
	 */
	private class ConOnItem implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ConcateItem concateItem = list.get(position);
			Intent intent = new Intent();
			intent.putExtra(ContactSelectActivity.KEY_NUM, concateItem.phone);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
}
