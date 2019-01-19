package com.example.calendardemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.example.calendardemo.db.ScheduleDAO;
import com.example.calendardemo.db.ScheduleVO;
import com.example.calendardemo.view.BorderTextView;

public class ScheduleInfoDetailActivity extends Activity {


	private TextView scheduledetailsTextView;
	private BorderTextView scheduledetailsType,scheduledetailsDate;
	
	
	private ScheduleDAO dao = null;
	private ArrayList<String> scheduleDate;
	private String[] scheduleIDs;
	private ScheduleVO scheduleVO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_schedule_details);
		
		dao = new ScheduleDAO(this);
		
		
		initView();
		
		Intent intent = getIntent();
		//scheduleID = Integer.parseInt(intent.getStringExtra("scheduleID"));
		//һ�����ڿ��ܶ�Ӧ�������ճ�(scheduleID)
		scheduleDate=intent.getStringArrayListExtra("scheduleDate");
		
		Log.i("scheduinfodetails", "����ճ���ϸ����?="+scheduleDate);
		scheduledetailsDate.setText(""+scheduleDate);
		scheduleVO=(ScheduleVO) intent.getExtras().getSerializable("scheduleVO");
		//��ʾ�ճ���ϸ��Ϣ
		//handlerInfo(scheduleVO);
		//setContentView(layout);
		
				
	}
	
	private void initView() {
		scheduledetailsTextView=(TextView)this.findViewById(R.id.scheduledetailsTextView);
		scheduledetailsType=(BorderTextView)this.findViewById(R.id.scheduledetailsType);
		scheduledetailsDate=(BorderTextView)this.findViewById(R.id.scheduledetailsDate);
	}

	/**
	 * ��ʾ�ճ�������Ϣ
	 */
	public void handlerInfo(ScheduleVO scheduleVO){
		//scheduledetailsType.setText(CalendarConstant.sch_type[scheduleVO.getScheduleTypeID()]);
		//scheduledetailsTextView.setText(scheduleVO.getScheduleContent());
		scheduledetailsDate.setText(scheduleVO.getScheduleDate());
		
		
		//��ʱ�䰴ס�ճ�����textview����ʾ�Ƿ�ɾ���ճ���Ϣ
//		scheduledetailsTextView.setOnLongClickListener(new OnLongClickListener() {
//			
//			
//			public boolean onLongClick(View v) {
//
//				scheduleVO = (ScheduleVO) v.getTag();
//				
//				new AlertDialog.Builder(ScheduleInfoDetailActivity.this).setTitle("ɾ���ճ�").setMessage("ȷ��ɾ��").setPositiveButton("ȷ��", new OnClickListener() {
//					
//					
//					public void onClick(DialogInterface dialog, int which) {
//						dao.delete(scheduleVO.getScheduleID());
//						Toast.makeText(ScheduleInfoDetailActivity.this, "�ճ���ɾ��", 0).show();
//						ScheduleViewAddActivity.setAlart(ScheduleInfoDetailActivity.this);
//						finish();
//					}
//				}).setNegativeButton("ȡ��", null).show();
//				
//				return true;
//			}
//		});
		
		
	}
}
