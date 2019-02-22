package com.ameer.codes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class EmotionalFaceView extends View {

    private static final String TAG = "EmotionalFaceView";

    private static final int DEFAULT_FACE_COLOR = Color.YELLOW;
    private static final int DEFAULT_EYES_COLOR = Color.BLACK;
    private static final int DEFAULT_MOUTH_COLOR = Color.BLACK;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final float DEFAULT_BORDER_WIDTH = 4.0f;
    private static final float DEFAULT_RADIUS = 200; //denotes no of pixels
    private static final int DEFAULT_FACE_STATE = 0; //default state happy


    private int faceColor = DEFAULT_FACE_COLOR;
    private int eyesColor = DEFAULT_EYES_COLOR;
    private int mouthColor = DEFAULT_MOUTH_COLOR;
    private int borderColor = DEFAULT_BORDER_COLOR;
    private float borderWidth = DEFAULT_BORDER_WIDTH;
    private float radius = DEFAULT_RADIUS;
    private int state = DEFAULT_FACE_STATE;


    private boolean hasRadius = false;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();

    int emotionalViewWidth;
    int emotionalViewHeight;


    public EmotionalFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmotionalFaceView(Context context) {
        super(context);
        init(null);

    }

    public EmotionalFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    private void init(AttributeSet attrs){
        if (attrs == null) return;
        TypedArray attributes = getContext().obtainStyledAttributes(attrs,R.styleable.EmotionalFaceView);

        faceColor = attributes.getColor(R.styleable.EmotionalFaceView_face_color,DEFAULT_FACE_COLOR);
        eyesColor = attributes.getColor(R.styleable.EmotionalFaceView_eyes_color,DEFAULT_EYES_COLOR);
        mouthColor = attributes.getColor(R.styleable.EmotionalFaceView_mouth_color,DEFAULT_MOUTH_COLOR);
        borderColor = attributes.getColor(R.styleable.EmotionalFaceView_border_color,DEFAULT_BORDER_COLOR);
        borderWidth =attributes.getDimension(R.styleable.EmotionalFaceView_border_width,DEFAULT_BORDER_WIDTH);
        hasRadius = attributes.hasValue(R.styleable.EmotionalFaceView_radius);
        if(hasRadius){
            radius = attributes.getDimension(R.styleable.EmotionalFaceView_radius,DEFAULT_RADIUS);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: "+getWidth()+  "  "+getHeight());
        emotionalViewWidth = getWidth();
        emotionalViewHeight = getHeight();
        float diffToAddToXAxis = 0f;
        float diffToAddToYAxis = 0f;
        if(emotionalViewWidth == emotionalViewHeight){
            if(!hasRadius){
                radius = emotionalViewHeight/2f;
            }
        }else if(emotionalViewWidth < emotionalViewHeight){
            if(!hasRadius){
                radius = emotionalViewWidth/2f;
            }
            if(radius <emotionalViewWidth){
                diffToAddToXAxis = (emotionalViewWidth - (radius * 2))/2f;
            }
            diffToAddToYAxis = (emotionalViewHeight - (radius * 2))/2f;
        }else{
            if(!hasRadius){
                radius = emotionalViewHeight/2f;
                Log.d(TAG, "onDraw: radius"+radius);
            }

            diffToAddToXAxis = (emotionalViewWidth - (radius * 2))/2f;
            if(radius <emotionalViewHeight){
                diffToAddToYAxis = (emotionalViewHeight - (radius * 2))/2f;
            }
        }
        drawFaceBackground(canvas,diffToAddToXAxis,diffToAddToYAxis);
        drawEyes(canvas,diffToAddToXAxis,diffToAddToYAxis);
        drawMouth(canvas,diffToAddToXAxis,diffToAddToYAxis);
    }

    private void drawFaceBackground(Canvas canvas,float x,float y){
        paint.setColor(faceColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(emotionalViewWidth/2,emotionalViewHeight/2,radius,paint);

        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);

        canvas.drawCircle(emotionalViewWidth/2,emotionalViewHeight/2,radius - (borderWidth/2f),paint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void drawEyes(Canvas canvas,float x,float y){

        paint.setColor(eyesColor);
        paint.setStyle(Paint.Style.FILL);

        RectF eyesLeft = new RectF(x+(radius*2 * 0.32f), y+(radius*2 * 0.23f), x+(radius*2 * 0.43f), y+(radius*2 * 0.50f));
        canvas.drawOval(eyesLeft,paint);
        RectF eyesRight = new RectF(x + (radius*2 * 0.57f), y+ (radius*2 * 0.23f), x + (radius*2 * 0.68f), y+ (radius*2 * 0.50f));
        canvas.drawOval(eyesRight,paint);

    }
    private void drawMouth(Canvas canvas,float x,float y){
        path.moveTo(x+(radius*2 * 0.22f), y+(radius*2 * 0.7f));
        path.quadTo(x+(radius *2 * 0.50f), y+(radius*2 * 0.80f), x+(radius *2* 0.78f), y+(radius*2 * 0.70f));
        path.quadTo(x+(radius*2 * 0.50f), y+(radius *2* 0.90f), x+(radius *2* 0.22f), y+(radius*2 * 0.70f));
        paint.setColor(mouthColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: "+getLayoutParams().width+"  "
                + getLayoutParams().height+"  "+getMeasuredWidth()
                +"  "+getMeasuredHeight());
        /*if(hasRadius){
            setMeasuredDimension((int)radius*2,(int)radius*2);
        }*/
    }
}
