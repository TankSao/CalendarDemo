package com.example.calendardemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import com.example.calendardemo.db.ScheduleDAO;
import com.example.calendardemo.db.ScheduleVO;
import com.example.calendardemo.util.LunarCalendar;
import com.example.calendardemo.view.BorderTextView;
import com.example.calendardemo.view.CalendarView;
import com.example.calendardemo.view.TopBar;


public class MainActivity extends ActionBarActivity  implements OnGestureListener,OnClickListener,OnLongClickListener {
	private static final String Tag="MainActivity";
	private LunarCalendar lcCalendar = null;
	private LunarCalendar calendar;
	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private CalendarView calV = null;
	private GridView gridView = null;
	private TopBar topText = null;
//	private TextView foot_tv = null;
	private Drawable draw = null;
	private static int jumpMonth = 0;      //ÿ�λ��������ӻ��ȥһ����,Ĭ��Ϊ0������ʾ��ǰ�£�
	private static int jumpYear = 0;       //������Խһ�꣬�����ӻ��߼�ȥһ��,Ĭ��Ϊ0(����ǰ��)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO;
	private String[] scheduleIDs;
	private  ArrayList<String> scheduleDate;
	private Dialog builder;
	private ScheduleVO scheduleVO_del;
	private String scheduleitems[];
	//С����item�Ŀؼ�
	private BorderTextView schdule_tip;
	private Button add;
	private Button quit;
	private TextView day_tv;
	private TextView launarDay;
	private ListView listView;
	private TextView weekday;
	private TextView lunarTime;
	private ListView list;
	private String dateInfo;//���gridview��������Ϣ
	private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        gestureDetector = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.removeAllViews();
        calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        addGridView();
        gridView.setAdapter(calV);
        //flipper.addView(gridView);
        flipper.addView(gridView,0);
		topText = (TopBar) findViewById(R.id.schedule_toptext);
		addTextToTopTextView(topText);
    }


    private void addGridView() {
		// TODO �Զ����ɵķ������
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
		//ȡ����Ļ�Ŀ�Ⱥ͸߶�
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth(); 
        int Height = display.getHeight();
        
        Log.d(Tag, "��Ļ�ֱ���=="+"height*weight"+Height+Width);
        
		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(46);
	//	gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if(Width == 480 && Height == 800){
			gridView.setColumnWidth(69);
		}else if(Width==800&&Height==1280){
			gridView.setColumnWidth(69);
		}
		
		
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // ȥ��gridView�߿�
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
        gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setOnTouchListener(new OnTouchListener() {
            //��gridview�еĴ����¼��ش���gestureDetector
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return MainActivity.this.gestureDetector
						.onTouchEvent(event);
			}
		});

		
		gridView.setOnItemClickListener(new OnItemClickListener() {
            //gridView�е�ÿһ��item�ĵ���¼�
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				  //����κ�һ��item���õ����item������(�ų�����������յ�����(�������Ӧ))
				  int startPosition = calV.getStartPositon();
				  int endPosition = calV.getEndPosition();
				  if(startPosition <= position  && position <= endPosition){
					  String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //��һ�������
					  //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //��һ�������
	                  String scheduleYear = calV.getShowYear();
	                  String scheduleMonth = calV.getShowMonth();
	                  String week = "";
	                 
	                  Log.i("�ճ���ʷ���", scheduleDay);
	                  
	                  //ͨ�����ڲ�ѯ��һ���Ƿ񱻱�ǣ����������ճ̾Ͳ�ѯ������������ճ���Ϣ
	                  scheduleIDs = dao.getScheduleByTagDate(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
	                  
	                  //�õ���һ�������ڼ�
	                  switch(position%7){
	                  case 0:
	                	  week = "������";
	                	  break;
	                  case 1:
	                	  week = "����һ";
	                	  break;
	                  case 2:
	                	  week = "���ڶ�";
	                	  break;
	                  case 3:
	                	  week = "������";
	                	  break;
	                  case 4:
	                	  week = "������";
	                	  break;
	                  case 5:
	                	  week = "������";
	                	  break;
	                  case 6:
	                	  week = "������";
	                	  break;
	                  }
					 
	                  scheduleDate = new ArrayList<String>();
	                  scheduleDate.add(scheduleYear);
	                  scheduleDate.add(scheduleMonth);
	                  scheduleDate.add(scheduleDay);
	                  scheduleDate.add(week);
	                  
	                  /**
	                   * 
	                   * ͨ��scheduleIDs�Ƿ񱻱�ǣ������ͨ��listview��ʾ���� 
	                   */
                	   LayoutInflater inflater=getLayoutInflater();
	              		View linearlayout= inflater.inflate(R.layout.schedule_details, null);
	              		 add=(Button)linearlayout.findViewById(R.id.btn_add);
	              		 quit=(Button) linearlayout.findViewById(R.id.btn_back);
	              	 day_tv=(TextView) linearlayout.findViewById(R.id.todayDate);
	              		launarDay=(TextView)linearlayout.findViewById(R.id.tv_launar);
	                  schdule_tip=(BorderTextView)linearlayout.findViewById(R.id.schdule_tip);
	              	 listView=(ListView)linearlayout.findViewById(R.id.schedulelist);
	              		//����
	              		 weekday=(TextView)linearlayout.findViewById(R.id.dayofweek);
	              		//ũ������
	              		 lunarTime=(TextView)linearlayout.findViewById(R.id.lunarTime);
	              		list=(ListView)linearlayout.findViewById(R.id.schedulelist);
	              	
	              	 dateInfo=scheduleYear+"��"+scheduleMonth+"��"+scheduleDay+"��";
	              	//���ũ����Ϣ	
	              	String scheduleLunarDay = getLunarDay(Integer.parseInt(scheduleYear),
	        				Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
	              	
	              	Log.i("LunarDay", scheduleLunarDay);
	              	//����ѡ�е����ڵ�����,���ں�ũ����Ϣ
	              		day_tv.setText(dateInfo);
	              		weekday.setText(week);
	              		addLunarDayInfo(lunarTime);
	              		launarDay.setText( scheduleLunarDay);
	              		
	              		Log.i("scheduleDate", "scheduleDate��������Ϣ��"+scheduleDate);
	              		//����ճ̰�ť
	              		//TableLayout dialog_tab=(TableLayout) linearlayout.findViewById(R.id.dialog_tab);
	              		add.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(builder!=null&&builder.isShowing()){
									builder.dismiss();
									Intent intent = new Intent();
					                  intent.putStringArrayListExtra("scheduleDate", scheduleDate);
					                  intent.setClass(MainActivity.this, ScheduleViewAddActivity.class);
					                  startActivity(intent);
								}
							}
						});
	              		//���ذ�ť
	              		quit.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(builder!=null&&builder.isShowing()){
									builder.dismiss();
								}
							}
						});
	                  
	                  //�������ǣ��������Ӧ���ճ���Ϣ�б�
                  if(scheduleIDs != null && scheduleIDs.length > 0){
                	  
                	  
		              		//list.setAdapter(new MyAdapter());
		              		View inflate=getLayoutInflater().inflate(R.layout.schedule_detail_item, null);
                        //ͨ��arraylist�����ݵ�listview��ȥ
		              		ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String, String>>();  
							ScheduleDAO dao=new ScheduleDAO(MainActivity.this);
							 String time="";
		                	  String content="";
	                	  for(int i=0;i<scheduleIDs.length;i++){
	                	  scheduleVO=dao.getScheduleByID(MainActivity.this,Integer.parseInt(scheduleIDs[i]));
	                	 time="";
	                	 content="";
	                	  
	                	  time=dateInfo+" "+scheduleVO.getTime();
	                	  content=scheduleVO.getScheduleContent();
	                		
	                	 
	                	 
	                		  HashMap<String, String> map=new HashMap<String, String>();
	                		  map.put("date", time);
	                		  map.put("content", content);
          	              	  Data.add(map);
          	              	  
	                	  }
	                	 String  from[]={"date","content"};
	                	  int to[]={R.id.itemTime,R.id.itemContent};
	                	  
	                	  SimpleAdapter adapter=new SimpleAdapter(MainActivity.this, Data, R.layout.schedule_detail_item, from, to);
	                	  
	                	  list.setAdapter(adapter);
	                	  
	                  }else{ //���û�б��λֱ�������Ϊ�����ް��š�
	                	  
	                	  schdule_tip.setText("���ް���");
	                	  listView.setVisibility(View.INVISIBLE);
	                  }
	                  
           	   //��dialog����ʽ��ʾ��windows��
            	  builder =	new Dialog(MainActivity.this,R.style.FullScreenDialog);
            	  builder.setContentView(linearlayout);
            	  WindowManager windowManager = getWindowManager();
            	  Display display = windowManager.getDefaultDisplay();
            	  WindowManager.LayoutParams lp = builder.getWindow().getAttributes();
            	  lp.width = (display.getWidth()); //���ÿ��
            	  lp.height=display.getHeight();
            	  builder.getWindow().setAttributes(lp); 
            	  builder.setCanceledOnTouchOutside(true);
            	  builder.show();
				  }
			}
		});
		gridView.setLayoutParams(params);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
	 * �������ڵ������շ�����������
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String getLunarDay(int year, int month, int day) {
		lcCalendar=new LunarCalendar();
		String lunar = lcCalendar.getLunarDate(year, month, day, true);
		// {������ȡ��������Ӧ����������ʱ������������ڶ�Ӧ����������Ϊ"��һ"���ͱ����ó����·�(��:���£����¡�������)},�����ڴ˾�Ҫ�жϵõ������������Ƿ�Ϊ�·ݣ�������·ݾ�����Ϊ"��һ"
		if (lunar.substring(1, 2).equals("��")) {
			lunar = "��һ";
		}
//		
//		Log.i("lunar", lunar);
//		String lunarDay=lunar.substring(2);
		
		return lunar;
	}
	//���ũ����Ϣ
		public void addLunarDayInfo(TextView text){
			StringBuffer textDate = new StringBuffer();
			if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
				textDate.append("��").append(calV.getLeapMonth()).append("��")
						.append("\t");
			}
			textDate.append(calV.getAnimalsYear()).append("��").append("(").append(
					calV.getCyclical()).append("��)");
			text.setText(textDate);
		}
    public MainActivity(){

		Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    	currentDate = sdf.format(date);  //��������
    	year_c = Integer.parseInt(currentDate.split("-")[0]);
    	month_c = Integer.parseInt(currentDate.split("-")[1]);
    	day_c = Integer.parseInt(currentDate.split("-")[2]);
    	
    	
    	
    	dao = new ScheduleDAO(this);
	}
    /**
	 * ��ӻ���ͷ������� �����µ���Ϣ
	 * */
	public void addTextToTopTextView(TextView view){
		StringBuffer textDate = new StringBuffer();
		draw = getResources().getDrawable(R.drawable.schedule_title_bg);
		view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("��").append(
				calV.getShowMonth()).append("��").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("��").append(calV.getLeapMonth()).append("��")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("��").append("(").append(
				calV.getCyclical()).append("��)");
		view.setText(textDate);
		view.setTextColor(Color.WHITE);
		view.setTextSize(15.0f);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return this.gestureDetector.onTouchEvent(event);
	}

	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0;         //ÿ�����gridview��viewflipper��ʱ���ı��
		if (e1.getX() - e2.getX() > 50) {
            //���󻬶�
			addGridView();   //���һ��gridView
			jumpMonth++;     //��һ����
			
			calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        //flipper.addView(gridView);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView, gvFlag);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -50) {
            //���һ���
			addGridView();   //���һ��gridView
			jumpMonth--;     //��һ����
			
			calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        gvFlag++;
	        addTextToTopTextView(topText);
	        //flipper.addView(gridView);
	        flipper.addView(gridView,gvFlag);
	        
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}


	@Override
	public boolean onLongClick(View arg0) {
		// TODO �Զ����ɵķ������
		return false;
	}


	@Override
	public void onClick(View arg0) {
		// TODO �Զ����ɵķ������
		
	}
}
