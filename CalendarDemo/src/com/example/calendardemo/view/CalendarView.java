package com.example.calendardemo.view;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.calendardemo.R;
import com.example.calendardemo.db.ScheduleDAO;
import com.example.calendardemo.util.LunarCalendar;
import com.example.calendardemo.util.ScheduleDateTag;
import com.example.calendardemo.util.SpecialCalendar;

/**
 * 锟斤拷锟斤拷gridview锟叫碉拷每一锟斤拷item锟斤拷示锟斤拷textview
 * @author jack_peng
 *
 */
public class CalendarView extends BaseAdapter {
	
	private static final String Tag="CalendarView";

	private ScheduleDAO dao = null;
	private boolean isLeapyear = false;  
	private int daysOfMonth = 0;      
	private int firstDayOfMonth = 0;        
	private int lastDaysOfMonth = 0;  
	private Context context;
	private String[] dayNumber = new String[49];  
	private static String week[] = {"周日","周一","周二","周三","周四","周五","周六"};
	//private static String week[] = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
	private SpecialCalendar specialCalendar = null;
	private LunarCalendar lunarCalendar = null; 
	private Resources res = null;
	private Drawable drawable = null;
	
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1;     //���ڱ�ǵ���
	private int[] schDateTagFlag = null;  //�洢�������е��ճ̰��ŵ�����
	
	private String showYear = "";   //������ͷ����ʾ�����
	private String showMonth = "";  //������ͷ����ʾ���·�
	private String animalsYear = ""; 
	private String leapMonth = "";   //����һ����
	private String cyclical = "";   //��ɵ�֧
	//ϵͳ��ǰʱ��
	private String sysDate = "";  
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	
	//�ճ�ʱ��(��Ҫ��ǵ��ճ�����)
	private String sch_year = "";
	private String sch_month = "";
	private String sch_day = "";
	
	public CalendarView(){
		Date date = new Date();
		sysDate = sdf.format(date);  //��������
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
		
	}
	
	public CalendarView(Context context,Resources rs,int jumpMonth,int jumpYear,int year_c,int month_c,int day_c){
		this();
		this.context= context;
		specialCalendar = new SpecialCalendar();
		lunarCalendar = new LunarCalendar();
		this.res = rs;
		
		int stepYear = year_c+jumpYear;
		int stepMonth = month_c+jumpMonth ;
		if(stepMonth > 0){
			//����һ���»���
			if(stepMonth%12 == 0){
				stepYear = year_c + stepMonth/12 -1;
				stepMonth = 12;
			}else{
				stepYear = year_c + stepMonth/12;
				stepMonth = stepMonth%12;
			}
		}else{
			//����һ���»���
			stepYear = year_c - 1 + stepMonth/12;
			stepMonth = stepMonth%12 + 12;
			if(stepMonth%12 == 0){
				
			}
		}
	
		currentYear = String.valueOf(stepYear);;  //�õ���ǰ�����
		currentMonth = String.valueOf(stepMonth);  //�õ����� ��jumpMonthΪ�����Ĵ���ÿ����һ�ξ�����һ�»��һ�£�
		currentDay = String.valueOf(day_c);  //�õ���ǰ����������
		
		getCalendar(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));
		
	}
	
	public CalendarView(Context context,Resources rs,int year, int month, int day){
		this();
		this.context= context;
		specialCalendar = new SpecialCalendar();
		lunarCalendar = new LunarCalendar();
		this.res = rs;
		currentYear = String.valueOf(year); //�õ���ת�������
		currentMonth = String.valueOf(month);  //�õ���ת�����·�
		currentDay = String.valueOf(day);  //�õ���ת������
		
		getCalendar(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dayNumber.length;
	}

	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	//��Gridview���ֵ
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar, null);
		 }
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		String d = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];
		Log.i("calendarview", d+","+dv);
		//Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
		//textView.setTypeface(typeface);
		SpannableString sp = new SpannableString(d+"\n"+dv);
		
		Log.i(Tag, "SpannableString---"+sp);
		
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f) , 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new TypefaceSpan("monospace"), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//sp.setSpan(new BackgroundColorSpan(Color.RED), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(dv != null || dv != ""){
			//ũ����ʾ����ʽ
            sp.setSpan(new RelativeSizeSpan(0.75f), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //sp.setSpan(new BackgroundColorSpan(Color.RED), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		textView.setText(sp);
		//textView.setTextColor(R.color.little_grey);
		textView.setTextColor(Color.RED);
		
		// ��ǰ���������ԣ�������ͱ���
		if (position < daysOfMonth + firstDayOfMonth+7 && position >= firstDayOfMonth+7) {
			
			
			
			textView.setTextColor(Color.BLACK);// �����������
			drawable = res.getDrawable(R.drawable.item);
			textView.setBackgroundDrawable(drawable);
			//textView.setBackgroundColor(Color.WHITE);
			//����һ��������Ӻ�
			if(position%7==0||position%7==6){
				textView.setTextColor(Color.rgb(255,120,20));
			}
			
		}else {

			//�����ܵ���������,���positionΪ0-6
			if(position<7){
				
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(14.0f);
//				textView.setGravity(45);
				drawable = res.getDrawable(R.drawable.week_top);
				textView.setBackgroundDrawable(drawable);
			}
			//���õ���������������ʾ������Ϊǳ��ɫ
			else{
		
				textView.setTextColor(Color.rgb(200, 195, 200));
			}
		}
		if(schDateTagFlag != null && schDateTagFlag.length >0){
			for(int i = 0; i < schDateTagFlag.length; i++){
				if(schDateTagFlag[i] == position){
					//�������ճ̰��ŵı�Ǳ���
					
					textView.setBackgroundResource(R.drawable.mark);
				}
			}
		}
		//���õ���ı���
		if(currentFlag == position){ 
			
			drawable = res.getDrawable(R.drawable.current_day_bgc);
			textView.setBackgroundDrawable(drawable);
			textView.setTextColor(Color.WHITE);
		}
		//���ÿ���µ���ĩ
		Calendar calendar=Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			textView.setTextColor(Color.rgb(255, 145, 90));
		}
		
		return convertView;
	}
	
	//�õ�ĳ���ĳ�µ����������µĵ�һ�������ڼ�
	public void getCalendar(int year, int month){
		isLeapyear = specialCalendar.isLeapYear(year);              //�Ƿ�Ϊ����
		daysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month);  //ĳ�µ�������
		firstDayOfMonth = specialCalendar.getWeekdayOfMonth(year, month);      //ĳ�µ�һ��Ϊ���ڼ�
		lastDaysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month-1);  //��һ���µ�������
		
		Log.d("DAY", isLeapyear+" ======  "+daysOfMonth+"  ============  "+firstDayOfMonth+"  =========   "+lastDaysOfMonth);
		getweek(year,month);
	}
	
	//��һ�����е�ÿһ���ֵ���������dayNuMber��
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		
		//�õ���ǰ�µ������ճ�����(��Щ������Ҫ��ǲ�����)
		dao = new ScheduleDAO(context);
		ArrayList<ScheduleDateTag> dateTagList = dao.getTagDate(year,month);
		if(dateTagList != null && dateTagList.size() > 0){
			schDateTagFlag = new int[dateTagList.size()];
		}
		
		for (int i = 0; i < dayNumber.length; i++) {
			// ��һ
			if(i<7){
				dayNumber[i]=week[i]+"."+" ";
			}
			else if(i < firstDayOfMonth+7){  //ǰһ����
				int temp = lastDaysOfMonth - firstDayOfMonth+1-7;
				//��������Ӧ��ũ��
				lunarDay = lunarCalendar.getLunarDate(year, month-1, temp+i,false);
				dayNumber[i] = (temp + i)+"."+lunarDay;
			}else if(i < daysOfMonth + firstDayOfMonth+7){   //����
				String day = String.valueOf(i-firstDayOfMonth+1-7); 
				//�õ�������
				lunarDay = lunarCalendar.getLunarDate(year, month, i-firstDayOfMonth+1-7,false);
				dayNumber[i] = i-firstDayOfMonth+1-7+"."+lunarDay;
				//���ڵ�ǰ�²�ȥ��ǵ�ǰ����
				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
					//��ǵ�ǰ����
					currentFlag = i;
				}
				
				//����ճ�����
				if(dateTagList != null && dateTagList.size() > 0){
					for(int m = 0; m < dateTagList.size(); m++){
						ScheduleDateTag dateTag = dateTagList.get(m);
						int matchYear = dateTag.getYear();
						int matchMonth = dateTag.getMonth();
						int matchDay = dateTag.getDay();
						if(matchYear == year && matchMonth == month && matchDay == Integer.parseInt(day)){
							schDateTagFlag[flag] = i;
							flag++;
						}
					}
				}
				
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lunarCalendar.animalsYear(year));
				setLeapMonth(lunarCalendar.leapMonth == 0?"":String.valueOf(lunarCalendar.leapMonth));
				setCyclical(lunarCalendar.cyclical(year));
			}else{   //��һ����
				lunarDay = lunarCalendar.getLunarDate(year, month+1, j,false);
				dayNumber[i] = j+"."+lunarDay;
				j++; 
			}
		}
        
        String dayStr = "";
        for(int i = 0; i < dayNumber.length; i++){
        	dayStr = dayStr+dayNumber[i]+":";
        }
        Log.d("calendarview",dayStr);


	}
	
	
	public void matchScheduleDate(int year, int month, int day){
		
	}
	
	/**
	 * ���ÿһ��itemʱ����item�е�����
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * �ڵ��gridViewʱ���õ�������е�һ���λ��
	 * @return
	 */
	public int getStartPositon(){
		return firstDayOfMonth+7;
	}
	
	/**
	 * �ڵ��gridViewʱ���õ�����������һ���λ��
	 * @return
	 */
	public int getEndPosition(){
		return  (firstDayOfMonth+daysOfMonth+7)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
