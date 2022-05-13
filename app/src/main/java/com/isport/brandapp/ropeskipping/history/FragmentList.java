package com.isport.brandapp.ropeskipping.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.R;
import com.isport.brandapp.bean.DeviceBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import brandapp.isport.com.basicres.commonutil.MessageEvent;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;


public class FragmentList extends Fragment {

    public static final int TYPE_DAY = 1;
    public static final int TYPE_WEEK = 2;
    public static final int TYPE_MONTH = 3;
    private int type;
    private int count = 100;

    private ViewPager viewPager;

    private FragmentStatePagerAdapter pagerAdapter;
    private DeviceBean deviceBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type", TYPE_DAY);
        deviceBean = (DeviceBean) getArguments().getSerializable(JkConfiguration.DEVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, null);
        viewPager = (ViewPager) view;

        EventBus.getDefault().register(this);
        pagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (type) {
                    case TYPE_DAY:
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -(getCount() - (position + 1)));
                        int date = (int) (cal.getTimeInMillis() / 1000);
                        Logger.myLog("---history--FragmentList------(position + 1)" + (position + 1) + "cal:" + TimeUtils.getTimeByyyyyMMddhhmmss(cal.getTimeInMillis()));
                        Fragment fragment = new RopeDayFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("date", date);
                        bundle.putSerializable(JkConfiguration.DEVICE, deviceBean);
                        fragment.setArguments(bundle);
                        return fragment;
                    case TYPE_WEEK:
                        Calendar weekcal = getCurretWeek(getCount() - (position + 1));
                        Date firstDayWeek = getFirstDayOfWeek(weekcal.getTime());
                        String startWeek = DateUtil.dataToString(firstDayWeek, "yyyy-MM-dd");
                        String endWeek = DateUtil.dataToString(getLastDayOfWeek(weekcal.getTime()), "yyyy-MM-dd");
                        int weekdate = (int) (weekcal.getTimeInMillis() / 1000);
                        Fragment weekfragment = new RopeWeeKFragment();
                        Bundle weekbundle = new Bundle();
                        weekbundle.putBoolean("isLast", (getCount() - (position + 1) == 0) ? true : false);
                        weekbundle.putInt("date", (int) (firstDayWeek.getTime() / 1000));
                        weekbundle.putSerializable(JkConfiguration.DEVICE, deviceBean);
                        weekbundle.putString("startdate", startWeek);
                        weekbundle.putString("enddate", endWeek);
                        weekfragment.setArguments(weekbundle);
                        return weekfragment;
                    case TYPE_MONTH:
                        Calendar monthcal = Calendar.getInstance();
                        monthcal.add(Calendar.MONTH, -(getCount() - (position + 1)));
                        int monthdate = (int) (monthcal.getTimeInMillis() / 1000);
                        Fragment monthfragment = new RopeMonthFragment();
                        Bundle monthbundle = new Bundle();
                        monthbundle.putSerializable(JkConfiguration.DEVICE, deviceBean);
                        monthbundle.putBoolean("isLast", (getCount() - (position + 1) == 0) ? true : false);
                        monthbundle.putInt("date", monthdate);
                        monthfragment.setArguments(monthbundle);
                        return monthfragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                if (type == TYPE_DAY) {
                    return count;
                } else {
                    return count;
                }
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

        };

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Logger.myLog("onPageSelected:" + position + "viewPager.getAdapter().getCount()" + viewPager.getAdapter().getCount());
                if (position == 0) {
                    int temp = viewPager.getAdapter().getCount();
                    int pagesize = viewPager.getAdapter().getCount() / 100;
                    int pageyu = viewPager.getAdapter().getCount() % 100;
                    if (pageyu != 0) {
                        pagesize++;
                    }
                    count = 100 * pagesize + 100;
                    viewPager.getAdapter().notifyDataSetChanged();
                    viewPager.setCurrentItem(temp);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(count);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsg()) {
            case MessageEvent.DayAdd:
                int currentCount = viewPager.getCurrentItem();
                Logger.myLog("currentCount=" + currentCount);
                if (currentCount - 1 < 0) {
                    currentCount = 0;
                }
                viewPager.setCurrentItem(currentCount - 1);
                break;
            case MessageEvent.DaySub:
                int currentSubCount = viewPager.getCurrentItem();
                if (viewPager.getAdapter().getCount() < currentSubCount) {
                    currentSubCount = currentSubCount - 1;
                }
                Logger.myLog("currentCount=" + currentSubCount);
                viewPager.setCurrentItem(currentSubCount + 1);
                break;
            case MessageEvent.viewPageChage:
                //如果有翻页，需要把count变大

                String dateStr = (String) messageEvent.getObj();
                //选择的日期
                Calendar calendar = Calendar.getInstance();//当前日期
                Calendar selectCalendar = Calendar.getInstance();//选择的日期


                String[] strings = dateStr.split("-");
                try {
                    selectCalendar.set(Calendar.YEAR, Integer.parseInt(strings[0]));
                    selectCalendar.set(Calendar.MONTH, Integer.parseInt(strings[1]) - 1);
                    selectCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strings[2]));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Logger.myLog("FragmentList------calendar:" + TimeUtils.getTimeByyyyyMMdd(calendar) + ",,selectCalendar:" + TimeUtils.getTimeByyyyyMMdd(selectCalendar));
                    int days = daysBetween(calendar, selectCalendar);
                    int page = days / 31;
                    int pageYu = days % 31;
                    if (pageYu > 0) {
                        page = page + 1;
                    }
                    if (page == 0) {
                        page = 1;
                    }

                    count = 30 * page;
                    viewPager.getAdapter().notifyDataSetChanged();
                    viewPager.setCurrentItem(viewPager.getAdapter().getCount() - days - 1);
                    Logger.myLog("FragmentList------ days:" + days + "position:" + (viewPager.getAdapter().getCount() - days - 1) + "----count:" + count + "page:" + page);


                }
                break;

        }


    }

    public static int daysBetween(Calendar date1, Calendar date2) {

        //同一年的,跨年的需要特殊处理
        int days;
        if (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)) {
            int day1 = date1.get(Calendar.DAY_OF_YEAR);
            int day2 = date2.get(Calendar.DAY_OF_YEAR);
            days = day1 - day2;
        } else {
            days = (int) ((date1.getTimeInMillis() - date2.getTimeInMillis())
                    / (1000 * 60 * 60 * 24));
        }
        return days;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    public Calendar getCurretWeek(int positon) {

        Calendar weekcal = Calendar.getInstance();

        //代码编写时间：2015年11月17日14:40:12
        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        cal.setFirstDayOfWeek(Calendar.SUNDAY); // 设置每周的第一天为星期一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 每周从周一开始
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        cal.setTime(new Date());
        weekcal.add(Calendar.WEEK_OF_YEAR, -positon);
        int weeks = cal.get(Calendar.WEEK_OF_YEAR);

        System.out.println(weeks);
        return weekcal;
    }

    public static String getWeek(Calendar calendar) {
        String Week = "周";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "日";
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "一";
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "二";
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "三";
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "四";
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "五";
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "六";
        }
        return Week;
    }


    /**
     * 取得当前日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek()); // Sunday
        return calendar.getTime();
    }

    /**
     * 取得当前日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek() + 6); // Saturday
        return calendar.getTime();
    }


}
