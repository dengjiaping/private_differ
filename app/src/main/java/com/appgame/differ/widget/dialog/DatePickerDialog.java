package com.appgame.differ.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.widget.WheelView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.attr.data;
import static com.appgame.differ.R.id.day;
import static com.appgame.differ.R.id.month;
import static com.appgame.differ.R.id.year;

/**
 * 日期选择dialog
 * Created by lzx on 2017/4/7.
 * 386707112@qq.com
 */

public class DatePickerDialog extends BaseDialog implements WheelView.OnSelectListener, View.OnClickListener {

    private Calendar calendar;
    private WheelView yearView, monthView, dayView;
    private TextView mBtnDismiss, mBtnEnter;
    private int currYear = 1900;
    private int currMonth = 1;
    private int currDay = 1;
    private OnSelectDateListener mListener;
    private String currData = "";

    public DatePickerDialog(Context context,String data) {
        super(context);
        currData = data;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_date_picker;
    }

    @Override
    protected void init() {
        calendar = Calendar.getInstance();

        yearView = (WheelView) findViewById(year);
        monthView = (WheelView) findViewById(R.id.month);
        dayView = (WheelView) findViewById(day);
        mBtnDismiss = (TextView) findViewById(R.id.btn_dismiss);
        mBtnEnter = (TextView) findViewById(R.id.btn_enter);

        currYear = calendar.get(Calendar.YEAR);
        currMonth = calendar.get(Calendar.MONTH) + 1;
        currDay = calendar.get(Calendar.DAY_OF_MONTH);

        // 设置默认年月日为当前日期
        yearView.setData(getYearData(currYear));
        monthView.setData(getMonthData());
        dayView.setData(getDayData(getLastDay(currYear, currMonth)));

        if (currData.equals("未设置")) {
            yearView.setDefault(0);
            monthView.setDefault(currMonth - 1);
            dayView.setDefault(currDay - 1);
        } else {
            try {
                String[] dateArray = currData.split("-");
                int year = Integer.parseInt(dateArray[0]);
                int month = Integer.parseInt(dateArray[1]);
                int day = Integer.parseInt(dateArray[2]);
                yearView.setDefault(year);
                monthView.setDefault(month - 1);
                dayView.setDefault(day - 1);
            } catch (Exception e) {
                yearView.setDefault(currYear);
                monthView.setDefault(currMonth - 1);
                dayView.setDefault(currDay - 1);
            }
        }

        yearView.setOnSelectListener(this);
        monthView.setOnSelectListener(this);
        dayView.setOnSelectListener(this);
        mBtnDismiss.setOnClickListener(this);
        mBtnEnter.setOnClickListener(this);
    }



    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    /**
     * 年范围在：今年-100年~今年
     */
    private ArrayList<String> getYearData(int currentYear) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 100; i--) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    private ArrayList<String> getMonthData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String month = String.valueOf(i);
            if (month.length() == 1) {
                month = "0" + month;
            }
            list.add(month);
        }
        return list;
    }

    /**
     * 日范围在1~lastDay
     */
    private ArrayList<String> getDayData(int lastDay) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= lastDay; i++) {
            String day = String.valueOf(i);
            if (day.length() == 1) {
                day = "0" + day;
            }
            list.add(day);
        }
        return list;
    }

    /**
     * 判断是否闰年
     */
    private boolean isLeapYear(int year) {
        return (year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0);
    }

    /**
     * 获取特定年月对应的天数
     */
    private int getLastDay(int year, int month) {
        if (month == 2) {
            // 2月闰年的话返回29，防止28
            return isLeapYear(year) ? 29 : 28;
        }
        return month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12 ? 31 : 30;
    }

    /**
     * 获取选择的年
     */
    public String getYear() {
        return yearView.getSelectedText();
    }

    /**
     * 获取选择的月
     */
    public String getMonth() {
        return monthView.getSelectedText();
    }

    /**
     * 获取选择的日
     */
    public String getDay() {
        return dayView.getSelectedText();
    }

    @Override
    public void endSelect(View view, int id, String text) {
        try {
            // 滚轮滑动停止后调用
            switch (view.getId()) {
                case year:
                case month:
                    // 记录当前选择的天数
                    int selectDay = Integer.parseInt(getDay());
                    // 根据当前选择的年月获取对应的天数
                    int lastDay = getLastDay(Integer.parseInt(getYear()), Integer.parseInt(getMonth()));
                    // 设置天数
                    dayView.setData(getDayData(lastDay));
                    // 如果选中的天数大于实际天数，那么将默认天数设为实际天数;否则还是设置默认天数为选中的天数
                    if (selectDay > lastDay) {
                        dayView.setDefault(lastDay - 1);
                    } else {
                        dayView.setDefault(selectDay - 1);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selecting(View view, int id, String text) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dismiss:

                break;
            case R.id.btn_enter:
                String date = getYear() + "-" + getMonth() + "-" + getDay();
                if (mListener != null) {
                    mListener.OnSelect(date);
                }
                break;
        }
        dismiss();
    }

    public void setOnSelectListener(OnSelectDateListener listener) {
        mListener = listener;
    }

    public interface OnSelectDateListener {
        void OnSelect(String text);
    }
}
