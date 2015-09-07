package com.scorpioneal.rulerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;


/**
 * TODO 效果有了，感觉还是有点不太好
 * Created by ScorpioNeal on 15/8/24.
 */
public class RulerView extends View {

    private static final String TAG = RulerView.class.getSimpleName();

    private Context mContext;

    /**
     * 短线的高度
     */
    private float mShortLineHeight;
    /**
     * 长线的高度
     */
    private float mHighLineHeight;
    /**
     * 短线的宽度
     */
    private float mShortLineWidth;
    /**
     * 长线的宽度
     */
    private float mHighLineWidth;
    /**
     * 两个长线间间隔数量
     */
    private int mSmallPartitionCount;
    /**
     * 指示器的宽度的一半
     */
    private float mIndicatorHalfWidth;
    /**
     * 指示器数字距离上边的距离
     */
    private float mIndicatorTextTopMargin;
    /**
     * 短线长线的上边距
     */
    private float mLineTopMargin;

    /**
     * 起止数值, 暂定为int
     */
    private int mStartValue;
    private int mEndValue;
    /**
     * 两个长线之间相差多少值 暂定为int
     */
    private int mPartitionValue;
    /**
     * 长线间隔宽度
     */
    private float mPartitionWidth;

    /**
     * 设置的初始值
     */
    private int mOriginValue;
    private int mOriginValueSmall;
    /**
     * 当前值
     */
    private int mCurrentValue;


    /**
     * 刻度的大小
     */
    private int mScaleTextsize;
    /**
     * 最小速度
     */
    protected int mMinVelocity;

    private float mWidth, mHeight;


    private Paint mBgPaint;
    private Paint mShortLinePaint;
    private Paint mHighLinePaint;
    private Paint mIndicatorTxtPaint;
    private Paint mIndicatorViewPaint;


    private float mRightOffset;
    private float mLeftOffset;

    private Scroller mScroller;
    protected VelocityTracker mVelocityTracker;

    private OnValueChangeListener listener;

    public interface OnValueChangeListener {
        void onValueChange(int intVal, int fltval);
    }

    public void setValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        mScroller = new Scroller(context);

        mMinVelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();

        initValue();

        initPaint();

    }

    private void initPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(Color.argb(255, 224, 95, 23));

        mShortLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShortLinePaint.setColor(Color.WHITE);
        mShortLinePaint.setStrokeWidth(mShortLineWidth);

        mHighLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighLinePaint.setColor(Color.WHITE);
        mHighLinePaint.setStrokeWidth(mHighLineWidth);

        mIndicatorTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorTxtPaint.setColor(Color.WHITE);
        mIndicatorTxtPaint.setTextSize(mScaleTextsize);

        mIndicatorViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorViewPaint.setColor(Color.WHITE);
    }

    private void initValue() {
        mIndicatorHalfWidth = Utils.convertDpToPixel(mContext, 9);
        mPartitionWidth = Utils.convertDpToPixel(mContext, 140.3f);
        mHighLineWidth = Utils.convertDpToPixel(mContext, 1.67f);
        mShortLineWidth = Utils.convertDpToPixel(mContext, 1.67f);
        mLineTopMargin = Utils.convertDpToPixel(mContext, 0.33f);
        mHighLineHeight = Utils.convertDpToPixel(mContext, 15.3f);
        mShortLineHeight = Utils.convertDpToPixel(mContext, 7.3f);
        mIndicatorTextTopMargin = Utils.convertDpToPixel(mContext, 15f);

        mSmallPartitionCount = 3;
        mOriginValue = 100;
        mOriginValueSmall = 0;
        mPartitionValue = 10;
        mStartValue = 50;
        mEndValue = 250;
        mScaleTextsize = 44;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        drawIndicator(canvas);

        drawLinePartition(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeight, mBgPaint);
    }

    private void drawIndicator(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mWidth / 2 - mIndicatorHalfWidth, 0);
        path.lineTo(mWidth / 2, mIndicatorHalfWidth);
        path.lineTo(mWidth / 2 + mIndicatorHalfWidth, 0);

        canvas.drawPath(path, mIndicatorViewPaint);
    }


    private float mMoveX = 0f;
    private float mOffset = 0f;

    //TODO 这个需要在mPartitionWidth和mSmallPartitionCount设置好之后才能用
    public void setOriginValueSmall(int small) {
        this.mOriginValueSmall = small;
        mMoveX = -mOriginValueSmall * (mPartitionWidth / mSmallPartitionCount);
        invalidate();
    }

    private void drawLinePartition(Canvas canvas) {
        int halfCount = (int) (mWidth / 2 / mPartitionWidth);

        mCurrentValue = mOriginValue - (int) (mMoveX / mPartitionWidth) * mPartitionValue;
        mOffset = mMoveX - (int) (mMoveX / mPartitionWidth) * mPartitionWidth;


        if (null != listener) {
            listener.onValueChange(mCurrentValue, -(int) (mOffset / (mPartitionWidth / mSmallPartitionCount)));
        }

        // draw high line and short line
        for (int i = -halfCount - 1; i <= halfCount + 1; i++) {
            int val = mCurrentValue + i * mPartitionValue;
            if (val >= mStartValue && val <= mEndValue) {
                //draw high line
                float startx = mWidth / 2 + mOffset + i * mPartitionWidth;
                if (startx > 0 && startx < mWidth) {
                    canvas.drawLine(mWidth / 2 + mOffset + i * mPartitionWidth, 0 + mLineTopMargin,
                            mWidth / 2 + mOffset + i * mPartitionWidth, 0 + mLineTopMargin + mHighLineHeight, mHighLinePaint);

                    //draw scale
                    canvas.drawText(val + "", mWidth / 2 + mOffset + i * mPartitionWidth - mIndicatorTxtPaint.measureText(val + "") / 2,
                            0 + mLineTopMargin + mHighLineHeight + mIndicatorTextTopMargin + Utils.calcTextHeight(mIndicatorTxtPaint, val + ""), mIndicatorTxtPaint);
                }

                //draw short line
                if (val != mEndValue) {

                    for (int j = 1; j < mSmallPartitionCount; j++) {
                        float start_x = mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount;
                        if (start_x > 0 && start_x < mWidth) {
                            canvas.drawLine(mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount, 0 + mLineTopMargin,
                                    mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount, 0 + mLineTopMargin + mShortLineHeight, mShortLinePaint);
                        }
                    }
                }

            }

        }
    }

    private boolean isActionUp = false;
    private float mLastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float xPosition = event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isActionUp = false;
                Log.d("ACTION_", "actiondown...");
                mScroller.forceFinished(true);
                if(null != animator) {
                    Log.d("MMM", "pause anim");
                    animator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                Log.d("ACTION_", "actionmove...");
                float off = xPosition - mLastX;

                if ((mMoveX <= mRightOffset) && off < 0 || (mMoveX >= mLeftOffset) && off > 0) {

                } else {
                    mMoveX += off;
                    postInvalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d("ACTION_", "actionup...");
                isActionUp = true;
                f = true;
                countVelocityTracker(event);
                return false;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private ValueAnimator animator;

    private boolean isCancel = false;
    private void startAnim() {
        isCancel = false;
        Log.d("ANIM", "start anim...");
        float smallWidth = mPartitionWidth / mSmallPartitionCount;
        float neededMoveX;
        if (mMoveX < 0) {
            neededMoveX = (int) (mMoveX / smallWidth - 0.5f) * smallWidth;
        } else {
            neededMoveX = (int) (mMoveX / smallWidth + 0.5f) * smallWidth;
        }
        float offset = neededMoveX - mMoveX;
        Log.d(TAG, "mMoveX: " + mMoveX + " needMoveX " + neededMoveX);
        animator = new ValueAnimator().ofFloat(mMoveX, neededMoveX);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("MMM", "anim....");
                if (!isCancel) {
                    mMoveX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("MMM", "receive status cancel");
                isCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private boolean f = true;

    @Override
    public void computeScroll() {
        super.computeScroll();
        Log.d("ACTION_", "computerscroll...");

        if (mScroller.computeScrollOffset()) {
            float off = mScroller.getFinalX() - mScroller.getCurrX();
            off = off * functionSpeed();
            Log.d(TAG, "computeScroll.................... " + " final " + mScroller.getFinalX() + " start " + mScroller.getCurrX());
            if ((mMoveX <= mRightOffset) && off < 0) {
                mMoveX = mRightOffset;
            } else if ((mMoveX >= mLeftOffset) && off > 0) {
                mMoveX = mLeftOffset;
            } else {
                mMoveX += off;
                if (mScroller.isFinished()) {
                    Log.d("MMM", "here isFinished start anim");
                    startAnim();
                } else {
                    postInvalidate();
                    mLastX = mScroller.getFinalX();
                }
                Log.d(TAG, "mMoveX here " + mMoveX + " off " + off + " isFinished " + mScroller.isFinished());


            }

        } else {
            if (isActionUp && f) {
                Log.d("MMM", "here computerscroll start anim");
                startAnim();
                f = false;

            }
            Log.d("ACTION_", "isActionUp " + isActionUp);
        }
    }

    /**
     * TODO change animation
     *
     * @return
     */
    private float functionSpeed() {
//        float afterOff = (float)Math.sin(off/mWidth * Math.PI / 2);
//        Log.d("OFF", off + " " + afterOff + "");
//        return (float)Math.sin(off/580 * Math.PI / 2);
        return 0.2f;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000, 3000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            Log.d("VLC", ">.....");
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        } else {
            Log.d("VLC", "<=.....");
        }
    }

    public int getmStartValue() {
        return mStartValue;
    }

    public void setmStartValue(int mStartValue) {
        this.mStartValue = mStartValue;
        calculatorOffsetRange();
        invalidate();
    }

    public int getmEndValue() {
        return mEndValue;
    }

    public void setmEndValue(int mEndValue) {
        this.mEndValue = mEndValue;
        calculatorOffsetRange();
        invalidate();
    }

    public int getmPartitionValue() {
        return mPartitionValue;
    }

    public void setmPartitionValue(int mPartitionValue) {
        this.mPartitionValue = mPartitionValue;
        calculatorOffsetRange();
        invalidate();
    }

    public float getmPartitionWidth() {
        return mPartitionWidth;
    }

    public void setmPartitionWidthInDP(float mPartitionWidth) {
        this.mPartitionWidth = Utils.convertDpToPixel(mContext, mPartitionWidth);
        recalculat();
        calculatorOffsetRange();
        invalidate();
    }

    public int getmValue() {
        return mCurrentValue;
    }

    public void setmValue(int mValue) {
        this.mCurrentValue = mValue;
        invalidate();
    }

    public int getmSmallPartitionCount() {
        return mSmallPartitionCount;
    }

    public void setmSmallPartitionCount(int mSmallPartitionCount) {
        this.mSmallPartitionCount = mSmallPartitionCount;
        recalculat();
        invalidate();
    }

    public int getmOriginValue() {
        return mOriginValue;
    }

    public void setmOriginValue(int mOriginValue) {
        this.mOriginValue = mOriginValue;
        calculatorOffsetRange();
        invalidate();
    }

    private void calculatorOffsetRange() {
        mRightOffset = -1 * (mEndValue - mOriginValue) * mPartitionWidth / mPartitionValue;
        mLeftOffset = -1 * (mStartValue - mOriginValue) * mPartitionWidth / mPartitionValue;
    }

    private void recalculat() {
        mMoveX = -mOriginValueSmall * (mPartitionWidth / mSmallPartitionCount);
        invalidate();
    }
}

