package com.isport.brandapp.device.sleep.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.isport.blelibrary.db.action.sleep.Sleep_Sleepace_DataModelAction;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.R;
import com.isport.brandapp.device.sleep.SleepConfig;
import com.isport.brandapp.device.sleep.TimeUtil;
import com.isport.brandapp.device.sleep.bean.Summary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bike.gymproject.viewlibray.DptopxUtil;

import com.isport.blelibrary.utils.CommonDateUtil;

public class WatchPopCalendarView extends ImageView {

    private static final String TAG = WatchPopCalendarView.class.getSimpleName();
    private Context context;
    private MonthDisplayHelper mHelper;
    private Calendar mRightNow = null;
    private Calendar activeC = null;
    private Cell[][] mCells = new Cell[6][7];
    private Cell[] weekCells = new Cell[7];
    private Cell mToday = null;
    private OnCellTouchListener mOnCellTouchListener = null;

    private int CELL_WIDTH;
    private int CELL_HEIGH;
    private int CELL_MARGIN_TOP;
    private int CELL_MARGIN_LEFT;
    private float CELL_TEXT_SIZE;

    private int drawablePad = 3;
    private int drawablePadTop = 2;

    public static final int CURRENT_MOUNT = 0;
    public static final int NEXT_MOUNT = 1;
    public static final int PREVIOUS_MOUNT = -1;

    private String sn;
    //private SummaryDao summaryDao;

    public interface OnCellTouchListener {
        void onTouch(Cell cell);
    }

    public WatchPopCalendarView(Context context) {
        this(context, null);
    }

    public WatchPopCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchPopCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        drawablePad = DptopxUtil.dp2px(drawablePad, getContext());
        drawablePadTop = DptopxUtil.dp2px(drawablePadTop, getContext());
        // summaryDao = SummaryDao.getInstance(context);
        initCalendarView();
    }

    public List<Summary> data;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
//				LogUtil.log(TAG+" handleMessage----------");
                data = (List<Summary>) msg.obj;
                initCells();
                invalidate();
            }
        }
    };

    private void initCalendarView() {
        activeC = TimeUtil.getCalendar(-1);
        mRightNow = TimeUtil.getCalendar(-1);
        // prepare static vars
        Resources res = getResources();

        // CELL_WIDTH = (int)
        // (ActivityUtil.getScreenHeight(getContext())*0.92f/7);//(int)
        // res.getDimension(R.dimen.cell_width);
        CELL_WIDTH = (int) res.getDimension(R.dimen.cell_width);
        CELL_HEIGH = CELL_WIDTH;// (int) res.getDimension(R.dimen.cell_heigh);
        CELL_MARGIN_TOP = (int) res.getDimension(R.dimen.cell_margin_top);
        CELL_MARGIN_LEFT = (int) res.getDimension(R.dimen.cell_margin_left);

        CELL_TEXT_SIZE = res.getDimension(R.dimen.cell_text_size);
        // set background
        // setImageResource(R.drawable.background_calender);

        mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH), mRightNow
                .getFirstDayOfWeek());

    }

    public void clearActiveCalendar() {
        activeC = null;
    }

    public void setActiveC(long time) {
        activeC = TimeUtil.getCalendar(-100);
        activeC.setTimeInMillis(time);
    }

    private int titleHeight;

    /**
     * 描述：CELL_MARGIN_TOP会变动，用此来保存最初值
     */
    // private int LAST_MARGON_TOP = 0;
    private void setWeekCells() {
        // CELL_MARGIN_TOP = LAST_MARGON_TOP;
        String[] weekday = getResources().getStringArray(R.array.arr_week);
        Paint paint = new Paint();
        paint.setTextSize(CELL_TEXT_SIZE);

        titleHeight = (int) (paint.descent() - paint.ascent());
        // LogUtil.logE(TAG+" setBlueCells
        // h:"+titleHeight+",cellTop:"+CELL_MARGIN_TOP+",cellH:"+CELL_HEIGH);
        int weekTitleColor = Color.parseColor("#9399A5");
        Rect Bound = new Rect(CELL_MARGIN_LEFT, 0, CELL_WIDTH + CELL_MARGIN_LEFT, titleHeight);
        for (int i = 0; i < weekCells.length; i++) {
            weekCells[i] = new ColorCell(weekday[i], new Rect(Bound), CELL_TEXT_SIZE, weekTitleColor);
            Bound.offset(CELL_WIDTH, 0);
        }

        CELL_MARGIN_TOP = (int) (CELL_HEIGH * 0.7);
    }

    private void initCells() {
        class _calendar {
            public int day;
            public int whichMonth; // -1 为上月 1为下月 0为此月

            public _calendar(int d, int b) {
                day = d;
                whichMonth = b;
            }

            public _calendar(int d) { // 上个月 默认为
                this(d, PREVIOUS_MOUNT);
            }
        }
        _calendar tmp[][] = new _calendar[6][7];

        for (int i = 0; i < tmp.length; i++) {
            int n[] = mHelper.getDigitsForRow(i);
            for (int d = 0; d < n.length; d++) {
                if (mHelper.isWithinCurrentMonth(i, d))
                    tmp[i][d] = new _calendar(n[d], CURRENT_MOUNT);
                else if (i == 0) {
                    tmp[i][d] = new _calendar(n[d]);
                } else {
                    tmp[i][d] = new _calendar(n[d], NEXT_MOUNT);
                }
            }
        }

        Calendar today = activeC;
        int thisDay = 0;
        int thisMonth = -1;

        if (today != null && mHelper.getYear() == today.get(Calendar.YEAR) && mHelper.getMonth() == today.get
                (Calendar.MONTH)) {
            thisDay = today.get(Calendar.DAY_OF_MONTH);
            thisMonth = today.get(Calendar.MONTH);
        }

        // build cells
        Rect Bound = new Rect(CELL_MARGIN_LEFT, CELL_MARGIN_TOP, CELL_WIDTH + CELL_MARGIN_LEFT, CELL_HEIGH +
                CELL_MARGIN_TOP);

        int dayIdx = 0;
        int dataSize = data == null ? 0 : data.size();

        for (int week = 0; week < mCells.length; week++) {
            for (int day = 0; day < mCells[week].length; day++) {
                if (tmp[week][day].whichMonth == CURRENT_MOUNT) { // 此月 开始设置cell
                    mCells[week][day] = new Cell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
                    mCells[week][day].setDateStr(mHelper.getYear() + "-" + CommonDateUtil.formatTwoStr(mHelper.getMonth() + 1) + "-" + CommonDateUtil.formatTwoStr(tmp[week][day].day));
                    if (thisMonth != -1 && tmp[week][day].day == thisDay) {
                        mToday = mCells[week][day];
                    }
//                    mCells[week][day].setStartTime(2);
                    //LogUtil.log(TAG+" initCell dataSize:"+dataSize+",dayIdx:" + dayIdx +",tmpDay:"+ tmp[week][day]
                    // .day);
                    if (data == null) {
                        return;
                    }

//                    Logger.myLog(data.get(dayIdx).getDay() + " 進入 ***  " +tmp[week][day].day);

                    if (dataSize > 0 && dayIdx < dataSize && data.get(dayIdx).getDay() == tmp[week][day].day) {
                        Logger.myLog(data.get(dayIdx).getDay() + "  ***  " + tmp[week][day].day);
                        mCells[week][day].setStartTime(data.get(dayIdx).getStartTime());
                        mCells[week][day].setScore(data.get(dayIdx).getScore());
                        boolean b = (int) (data.get(dayIdx).getMonth()) < 10;
                        String monthStr = b ? ("0" + ((int) data.get(dayIdx).getMonth())) : ((int) data.get(dayIdx)
                                .getMonth
                                        ()) + "";
                        boolean T = (int) (data.get(dayIdx).getDay()) < 10;
                        String dayStr = T ? ("0" + ((int) data.get(dayIdx).getDay())) : ((int) data.get(dayIdx).getDay
                                ()) + "";
                        mCells[week][day].setDateStr(data.get(dayIdx).getYear() + "-" + monthStr + "-" + dayStr);
                        dayIdx++;
                    }
                } else if (tmp[week][day].whichMonth == PREVIOUS_MOUNT) { // 上月为gray
                    mCells[week][day] = new GrayCell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
                } else { // 下月为LTGray
                    mCells[week][day] = new LTGrayCell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
                }

                Bound.offset(CELL_WIDTH, 0); // move to next column
            }

            Bound.offset(0, CELL_HEIGH); // move to next row and first column
            Bound.left = CELL_MARGIN_LEFT;
            Bound.right = CELL_MARGIN_LEFT + CELL_WIDTH;
        }
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        CELL_WIDTH = (right - left - CELL_MARGIN_LEFT * 2) / 7;
        setWeekCells();
        initCells();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int h1 = MeasureSpec.getSize(heightMeasureSpec);
        boolean isLastRowNextMonth = (mCells[5][0] instanceof LTGrayCell);
        int height = 0;
        if (isLastRowNextMonth) {
            height = titleHeight + CELL_MARGIN_TOP + CELL_HEIGH * 5;
        } else {
            height = titleHeight + CELL_MARGIN_TOP + CELL_HEIGH * 6;
        }
        // LogUtil.log(TAG + " onMeasure isLastRowNextMonth:" + isLastRowNextMonth + ",height:" + height + ",h1:" + h1);
        setMeasuredDimension(width, height);
    }

    public void setDeviceSN(String sn) {
        this.sn = sn;
    }

    public void setTimeInMillis(long milliseconds) {
        mRightNow.setTimeInMillis(milliseconds);
        data = new ArrayList<>();
        // LogUtil.log(TAG + " setTimeInMillis :" + StringUtil.DATE_FORMAT.format(mRightNow.getTime()));
        mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH), mRightNow
                .getFirstDayOfWeek());
        initCells();
        requestLayout();
//        if (!TextUtils.isEmpty(sn)) {
//        }
        //获取当前页
        if (monthDataListen != null) {
            String strMonth = mRightNow.get(Calendar.YEAR) + "-" + CommonDateUtil.formatTwoStr(mRightNow.get(Calendar.MONTH) + 1);
            monthDataListen.getMontData(strMonth);
        }
        // new QueryDataTask(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH)).start();
    }

    public int getYear() {
        return mHelper.getYear();
    }

    public int getMonth() {
        return mHelper.getMonth();
    }
    public void clearSummary() {
        if (data != null && data.size() > 0) {
            data.clear();
        }
    }

    public void goCurrentMonth() {
        activeC = TimeUtil.getCalendar(-1);
        mRightNow = TimeUtil.getCalendar(-1);
        mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH), mRightNow
                .getFirstDayOfWeek());
        initCells();
        requestLayout();
        if (monthDataListen != null) {
            String strMonth = mHelper.getYear() + "-" + CommonDateUtil.formatTwoStr(mHelper.getMonth() + 1);
            monthDataListen.getMontData(strMonth);
        }
        // new QueryDataTask(mHelper.getYear(), mHelper.getMonth()).start();
    }

    public void nextMonth() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        //展示年小于当前年;或者展示年为当前年,月是小于当前月的才会移动
        if (mRightNow.get(Calendar.YEAR) < y || (mRightNow.get(Calendar.YEAR) == y && mRightNow.get(Calendar.MONTH) < m)) {
            mHelper.nextMonth();
            mRightNow.add(Calendar.MONTH, 1);
            initCells();
            requestLayout();
            if (monthDataListen != null) {
                String strMonth = mHelper.getYear() + "-" + CommonDateUtil.formatTwoStr(mHelper.getMonth() + 1);
                monthDataListen.getMontData(strMonth);
            }
            // new QueryDataTask(mHelper.getYear(), mHelper.getMonth()).start();
        }
    }

    public void previousMonth() {
        mHelper.previousMonth();
        mRightNow.add(Calendar.MONTH, -1);
        initCells();
        requestLayout();

        if (monthDataListen != null) {
            String strMonth = mHelper.getYear() + "-" + CommonDateUtil.formatTwoStr(mHelper.getMonth() + 1);
            monthDataListen.getMontData(strMonth);
        }

        //new QueryDataTask(mHelper.getYear(), mHelper.getMonth()).start();
    }

    public boolean firstDay(int day) {
        return day == 1;
    }

    public boolean lastDay(int day) {
        return mHelper.getNumberOfDaysInMonth() == day;
    }

    public void goToday() {
        Calendar cal = Calendar.getInstance();
        mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        initCells();
        invalidate();
    }

    public Calendar getDate() {
        return mRightNow;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnCellTouchListener != null) {
            for (Cell[] week : mCells) {
                for (Cell day : week) {
                    if (day.hitTest((int) event.getX(), (int) event.getY()) && event.getAction() == MotionEvent.ACTION_DOWN) {
                        mOnCellTouchListener.onTouch(day);
                        mToday = day;
                        long time = (day.getStartTime() - SleepConfig.SLEEP_DIFF * 3600) * 1000l;
                        //LogUtil.log(TAG + " onTouchEvent date:" + StringUtil.DATE_FORMAT.format(new Date(time)));
                        setActiveC(time);
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnCellTouchListener(OnCellTouchListener p) {
        mOnCellTouchListener = p;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        super.onDraw(canvas);

        // LogUtil.log(TAG + " onDraw w:" + getWidth() + ",h:" + getHeight()+",goodD:"+goodD);

        for (Cell d : weekCells) {
            d.draw(canvas);
        }

        // draw cells
        for (Cell[] week : mCells) {
            for (Cell day : week) {
                /**
                 * 先画背景人
                 */
                if (day == null) {
                    return;
                }
                if (day.getStartTime() > 0) {
                    int color = getResources().getColor(R.color.common_item_line_color);
                    day.mCirclePaint.setColor(color);
                    //day.mPaint.setColor(getResources().getColor(R.color.bg_date_report_item));
                    canvas.drawCircle(day.getBound().centerX(), day.getBound().centerY(), CELL_WIDTH / 3f, day
                            .mCirclePaint);
                }
                if (day == mToday) {
                    int color = getResources().getColor(R.color.common_view_color);
                    day.mCirclePaint.setColor(color);
                    //day.mPaint.setColor(getResources().getColor(R.color.bg_date_report_item));
                    day.mCirclePaint.setStrokeWidth(DptopxUtil.dp2px(1, context));
                    canvas.drawCircle(day.getBound().centerX(), day.getBound().centerY(), CELL_WIDTH / 3f, day
                            .mCirclePaint);
                }
                if (day == mToday) {
                    day.mPaint.setColor(Color.WHITE);
                } else {
                    day.mPaint.setColor(getResources().getColor(R.color.common_blank));
                }

                day.draw(canvas);

//				LogUtil.log(TAG+" onDraw stime:" + day.getStartTime());


            }
        }
    }

    private class GrayCell extends Cell {
        public GrayCell(int dayOfMon, Rect rect, float s) {
            super(dayOfMon, rect, 0);
            mPaint.setColor(Color.BLACK);
        }
    }

    private class LTGrayCell extends Cell {
        public LTGrayCell(int dayOfMon, Rect rect, float s) {
            super(dayOfMon, rect, 0);
            mPaint.setColor(Color.BLACK);
        }
    }

    private class ColorCell extends Cell {
        public ColorCell(String dayOfMon, Rect rect, float s, int color) {
            super(dayOfMon, rect, s, false);
            mPaint.setColor(color);
        }
    }

    /**
     * 描述：由于要日历和 数据挂钩，所以要异步查找数据
     */
    public class QueryDataTask extends Thread {

        private int year;
        private int month;

        public QueryDataTask(int year, int month) {
            this.year = year;
            this.month = month;
        }

        @Override
        public void run() {
            //查询当月有数据的月的列表
            List<Summary> list = new ArrayList<>();
            List<String> strings = Sleep_Sleepace_DataModelAction.listCurrentDateStr(year + "-" + (month + 1 < 10 ?
                    "0" +
                            (month + 1) : (month + 1) + ""));
            Logger.myLog(strings.toString() + "  ***  " + year + "-" + (month + 1 < 10 ? "0" +
                    (month + 1) : (month + 1) + ""));
            for (int i = 0; i < strings.size(); i++) {
                String dateStr = strings.get(i);
                String[] split = dateStr.split(" ");
                for (int j = 0; j < split.length; j++) {
                    Logger.myLog(split[j]);
                }
                String[] yyyyDDmm = split[0].split("-");
                for (int j = 0; j < yyyyDDmm.length; j++) {
                    Logger.myLog(yyyyDDmm[j]);
                }
                Summary summary = new Summary();
                summary.setStartTime(1);
                summary.setYear(Short.parseShort(yyyyDDmm[0]));
                summary.setMonth((byte) Integer.parseInt(yyyyDDmm[1]));
                summary.setDay((byte) Integer.parseInt(yyyyDDmm[2]));
                list.add(summary);
//                2019-01-09 17:02:00, 2019-01-08 17:01:00, 2019-01-07 17:09:00, 2019-01-02 17:01:00
            }
            //Summary summary=new Summary();
            // LogUtil.log(TAG + " QueryDataTask y:" + year + ",m:" + month + ",size:" + list.size());*/
            handler.obtainMessage(100, list).sendToTarget();
        }
    }

    MonthDataListen monthDataListen;

    public interface MonthDataListen {
        public void getMontData(String strYearAndMonth);
    }

    public void setMonthDataListen(MonthDataListen monthDataListen) {
        this.monthDataListen = monthDataListen;
    }


    public void setSummary(List<String> strings) {
        List<Summary> list = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            String dateStr = strings.get(i);
            String[] split = dateStr.split(" ");
            for (int j = 0; j < split.length; j++) {
                Logger.myLog(split[j]);
            }
            String[] yyyyDDmm = split[0].split("-");
            for (int j = 0; j < yyyyDDmm.length; j++) {
                Logger.myLog(yyyyDDmm[j]);
            }
            Summary summary = new Summary();
            summary.setStartTime(1);
            summary.setYear(Short.parseShort(yyyyDDmm[0]));
            summary.setMonth((byte) Integer.parseInt(yyyyDDmm[1]));
            summary.setDay((byte) Integer.parseInt(yyyyDDmm[2]));
            list.add(summary);
//                2019-01-09 17:02:00, 2019-01-08 17:01:00, 2019-01-07 17:09:00, 2019-01-02 17:01:00
        }
        data = list;
        initCells();
        invalidate();
        requestLayout();
    }

}















