package us.sosia.snapview.Test;

import us.sosia.snapview.SnapView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SnapActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ListView listView = new ListView( this);
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,new String[]{"aa","bb","cc","dd","aa","bb","cc","dd","aa","bb","cc","dd","aa","bb","cc","dd","aa","bb","cc","dd",});
		listView.setAdapter(arrayAdapter);
		
		
		
		TextView textView2 = new TextView(this);
		textView2.setTextSize(40);
		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		textView2.setLayoutParams(layoutParams);
		textView2.setText("<------------------  --------------------->");
		textView2.setTextColor(Color.RED);		
		
		
		ListView listView2 = new ListView(this);
		listView2.setAdapter(arrayAdapter);
		listView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("gg", "i am touched");
				return false;
			}
		});
		SnapView snapView = new SnapView(this);
		snapView.setLeftView(listView);
		//snapView.setRightView(listView2);
		snapView.setTopView(listView2);
		setContentView(snapView);
 		super.onCreate(savedInstanceState);
 		}
}