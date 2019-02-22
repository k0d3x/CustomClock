package com.ameer.codes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.text.format.DateFormat;

import java.util.Calendar;

public class CustomClock extends View {

    private static final float MIN_TIME_SIZE = 120; //pixels
    private static final float MIN_SECOND_SIZE = 80; //pixels
    private static final float MIN_MERIDIEM_SIZE = 80; //pixels

    private static final String DEFAULT_SUFFIX = "PM";
    private static final int DEFAULT_FORMAT = 0;
    private static final String SEPARATOR = ":";

    private String hourValue;
    private String minuteValue;
    private String secondValue;
    private String meridiemValue;


    private float time_size = MIN_TIME_SIZE;
    private float second_size = MIN_SECOND_SIZE;
    private int clock_format = DEFAULT_FORMAT;
    private int timeColor = Color.BLACK;
    private float meridiem_size = MIN_MERIDIEM_SIZE;
    private float paddingColonLeft = 20;
    private float paddingColonRight = 20;
    private float paddingLeftSecond = 20;
    private float paddingLeft = 0;
    private float paddingRight= 0;
    private float paddingLeftMeridiem = 10;

    //private boolean showSecond = true;

    private Paint mPaintHour;
    private Paint mPaintMinute;
    private Paint mPaintSecond;
    private Paint mPaintMeridiem;
    private Paint mPaintSeparator;

    private Rect mHourBounds = new Rect();
    private Rect mSeparatorBounds = new Rect();
    private Rect mMinuteBounds = new Rect();
    private Rect mSecondBounds = new Rect();

    private Handler mHandler;
    private Calendar mCalendar;
    private boolean mTickerStopped = false;
    private Runnable mTicker;

    private float xHour;
    private float xSeparator;
    private float xMinute;
    private float xSecond;
    private float hourMinuteBaseline;
    private float separatorBaseline;
    private float secondBaseline;

    private float allContentWidth;
    private float allContentHeight;
    private int viewMeasuredWidth;
    private int viewMeasuredHeight;


    public CustomClock(Context context) {
        this(context,null);

    }

    public CustomClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    private void init(AttributeSet attrs){
        TypedArray attributes = getContext().obtainStyledAttributes(attrs,R.styleable.CustomClock);
        time_size = attributes.getDimension(R.styleable.CustomClock_time_size,MIN_TIME_SIZE);
        second_size = attributes.getDimension(R.styleable.CustomClock_second_size,MIN_SECOND_SIZE);
        paddingColonLeft = paddingColonRight = attributes.getDimension(R.styleable.CustomClock_paddingColon,10);
        paddingLeftSecond = attributes.getDimension(R.styleable.CustomClock_paddingLeftSecond,10);
        timeColor = attributes.getColor(R.styleable.CustomClock_timeColor,Color.BLACK);
        meridiem_size = attributes.getDimension(R.styleable.CustomClock_meridiem_size,MIN_MERIDIEM_SIZE);
        clock_format = attributes.getInt(R.styleable.CustomClock_clock_format,DEFAULT_FORMAT);

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();

        //showSecond = attributes.getBoolean(R.styleable.CustomClock_show_second,true);

        mPaintHour = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintHour.setTextSize(time_size);
        mPaintHour.setColor(timeColor);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/Roboto-Thin.ttf");
        mPaintHour.setTypeface(tf);
        mPaintHour.setTextAlign(Paint.Align.CENTER);

        mPaintMinute = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMinute.setTextSize(time_size);
        mPaintMinute.setColor(timeColor);
        mPaintMinute.setTypeface(tf);
        mPaintMinute.setTextAlign(Paint.Align.CENTER);

        mPaintSecond = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSecond.setTextSize(second_size);
        mPaintSecond.setColor(timeColor);
        mPaintSecond.setTypeface(tf);
        mPaintSecond.setTextAlign(Paint.Align.CENTER);

        mPaintMeridiem = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMeridiem.setTextSize(meridiem_size);
        mPaintMeridiem.setColor(timeColor);
        mPaintMeridiem.setTypeface(tf);
        mPaintMeridiem.setTextAlign(Paint.Align.CENTER);

        mPaintSeparator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSeparator.setColor(timeColor);
        mPaintSeparator.setTextSize(time_size);
        mPaintSeparator.setTypeface(tf);
        mPaintSeparator.setTextAlign(Paint.Align.CENTER);

        if(clock_format == DEFAULT_FORMAT){
            // initialize hourValue as 12 hr format using time calender or date format
            // initialize minuteValue as 12 hr format using time calender or date format
            // initialize secondValue as 12 hr format using time calender or date format
            // initialize meridiemValue as 12 hr format using time calender or date format
        }
        else {
            // initialize hourValue as 24 hr format using time calender or date format
            // initialize minuteValue as 24 hr format using time calender or date format
        }
        //showSecond = true;
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mHourBounds = getBounds(mPaintHour,"00",mHourBounds);
        mSeparatorBounds = getBounds(mPaintSeparator,":",mSeparatorBounds);
        mMinuteBounds = getBounds(mPaintMinute,"00",mMinuteBounds);
        mSecondBounds = getBounds(mPaintSecond,"00",mSecondBounds);

        calculateAllWidthAndHeight();
        //calculateXOfText();
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                switch (clock_format){
                    case 0:
                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                        hourValue = DateFormat.format("hh",mCalendar).toString();
                        minuteValue = DateFormat.format("mm",mCalendar).toString();
                        secondValue = DateFormat.format("ss",mCalendar).toString();
                        meridiemValue = DateFormat.format("aa",mCalendar).toString();
                    case 1:
                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                        hourValue = DateFormat.format("HH",mCalendar).toString();
                        minuteValue = DateFormat.format("mm",mCalendar).toString();
                        secondValue = DateFormat.format("ss",mCalendar).toString();
                        break;
                }
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);

            }
        };
        mTicker.run();
    }

    private void calculateXOfText(){

        xHour = (getWidth() - allContentWidth)/2 +mHourBounds.width()/2;
        xSeparator = xHour + mHourBounds.width()/2 + paddingColonLeft + mSeparatorBounds.width()/2;
        xMinute = xSeparator + mSeparatorBounds.width()/2 + paddingColonRight + mMinuteBounds.width()/2;
        xSecond = xMinute + mMinuteBounds.width()/2 + paddingLeftSecond + mSecondBounds.width()/2;

        hourMinuteBaseline = getHeight()/2 + mHourBounds.height()/2 - mHourBounds.bottom/2;
        separatorBaseline = hourMinuteBaseline - mHourBounds.height()/2 + mSeparatorBounds.height()/2;
        secondBaseline = hourMinuteBaseline + mHourBounds.top + mSecondBounds.height() - mSecondBounds.bottom/2;
    }

    private void calculateAllWidthAndHeight(){

        allContentWidth = mHourBounds.width() + paddingColonLeft + mSeparatorBounds.width()
                    + paddingColonRight + mMinuteBounds.width() + paddingLeftSecond + mSecondBounds.width();
        allContentHeight = mHourBounds.height();
    }

    private Rect getBounds(Paint paint, String text, Rect rect){
        paint.getTextBounds(text,0,text.length(),rect);
        return rect;
    }

    private int measureSize(int specType, int contentSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = Math.max(contentSize, specSize);
        } else {
            result = contentSize;

            if (specType == 1) {
                // width
                result += (getPaddingLeft() + getPaddingRight());
            } else {
                // height
                result += (getPaddingTop() + getPaddingBottom());
            }
        }

        return result;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int contentAllWidth = (int)allContentWidth;
        int contentAllHeight = (int)allContentHeight;
        int viewWidth = measureSize(1, contentAllWidth, widthMeasureSpec);
        int viewHeight = measureSize(2, contentAllHeight, heightMeasureSpec);
        //viewMeasuredWidth = viewWidth;
        //viewMeasuredHeight = viewHeight;
        setMeasuredDimension(viewWidth, viewHeight);
        //calculateXOfText();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateXOfText();
        if(clock_format == 0){
            canvas.drawText(hourValue,xHour,hourMinuteBaseline,mPaintHour);
            canvas.drawText(SEPARATOR,xSeparator,separatorBaseline, mPaintSeparator);
            canvas.drawText(minuteValue,xMinute,hourMinuteBaseline,mPaintMinute);
            canvas.drawText(secondValue,xSecond,secondBaseline,mPaintSecond);
            canvas.drawText(meridiemValue,xSecond,hourMinuteBaseline,mPaintMeridiem);
        }
        else if (clock_format == 1){
            canvas.drawText(hourValue,xHour,hourMinuteBaseline,mPaintHour);
            canvas.drawText(SEPARATOR,xSeparator,separatorBaseline, mPaintSeparator);
            canvas.drawText(minuteValue,xMinute,hourMinuteBaseline,mPaintMinute);
            canvas.drawText(secondValue,xSecond,secondBaseline,mPaintSecond);
        }
    }
}
