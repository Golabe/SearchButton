package com.github.golabe.searchbutton.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class SearchButton extends View {

    private int searchColor;
    private float searchBorder;
    private Paint searchPaint;
    private Path circlePath;
    private Path searchPath;
    private PathMeasure pathMeasure;
    private RectF searchRectF;
    private RectF circleRectF;
    private int defaultDuration;
    private ValueAnimator startAnimator;
    private Animator.AnimatorListener animatorListener;
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private float animatedValue;
    private MyHandler myHandler;
    private boolean isSearchOver;
    private ValueAnimator searchingAnimator;
    private ValueAnimator endingAnimator;

    public enum State {
        NONE,
        STARTING,
        SEARCHING,
        ENDING
    }

    public State currentState = State.NONE;

    public SearchButton(Context context) {
        this(context, null);
    }

    public SearchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attrs(attrs);
        init();
        initPath();
        initListener();
        initHandler();
        initAnimation();
    }

    private void initHandler() {
        myHandler = new MyHandler();


    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (currentState) {
                case STARTING:
                    isSearchOver = false;
                    currentState = State.SEARCHING;
                    startAnimator.removeAllUpdateListeners();
                    searchingAnimator.start();

                    break;
                case SEARCHING:
                    if (isSearchOver) {
                        currentState = State.ENDING;
                        endingAnimator.start();
                    } else {
                        searchingAnimator.start();
                    }
                    break;
                case ENDING:
                    currentState = State.NONE;
                    break;
            }
        }
    }

    private void initListener() {
        updateListener = new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        animatorListener = new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (myHandler != null) {
                    myHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

    }

    private void initAnimation() {
        startAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        searchingAnimator = ValueAnimator.ofFloat(0, 1);
        searchingAnimator.setDuration(defaultDuration);
        searchingAnimator.setInterpolator(new AccelerateInterpolator());

        endingAnimator = ValueAnimator.ofFloat(1, 0).setDuration(defaultDuration);

        startAnimator.addUpdateListener(updateListener);
        searchingAnimator.addUpdateListener(updateListener);
        endingAnimator.addUpdateListener(updateListener);

        startAnimator.addListener(animatorListener);
        searchingAnimator.addListener(animatorListener);
        endingAnimator.addListener(animatorListener);

    }

    private void initPath() {
        searchPath = new Path();
        circlePath = new Path();
        pathMeasure = new PathMeasure();
        searchRectF = new RectF();
        circleRectF = new RectF();
    }

    private void attrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SearchButton);
            searchColor = a.getColor(R.styleable.SearchButton_search_color, Color.WHITE);
            searchBorder = a.getDimension(R.styleable.SearchButton_search_border, 2F);
            defaultDuration = a.getInt(R.styleable.SearchButton_search_duration, 2000);
            a.recycle();
        }

    }

    private void init() {
        searchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        searchPaint.setStrokeWidth(dp2px(searchBorder));
        searchPaint.setColor(searchColor);
        searchPaint.setStyle(Paint.Style.STROKE);
        searchPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wModel = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (wModel == MeasureSpec.EXACTLY) {
            width = w;
            height = h;
        } else {
            width = dp2px(32F);
            height = dp2px(32F);
        }

        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int gap = w / 6;
        searchRectF.set(gap, gap, w - gap, h - gap);
        searchPath.addArc(searchRectF, 45, 359.9F);
        circleRectF.set(gap, gap, w - gap, h - gap);
        circlePath.addArc(circleRectF, 45, -359.9F);
        Path path = new Path();
        path.addArc(new RectF(0, 0, w, h), 45, -360);
        pathMeasure.setPath(path, false);
        float[] pos = new float[2];
        pathMeasure.getPosTan(0, pos, null);
        searchPath.lineTo(pos[0], pos[1]);

    }

   private Path dst2 = new Path();
    private Path dst3 = new Path();
   private Path dst = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentState) {
            case NONE:
                canvas.drawPath(searchPath, searchPaint);
                break;
            case STARTING:
                pathMeasure.setPath(circlePath, false);
                dst.reset();
                pathMeasure.getSegment(pathMeasure.getLength() * animatedValue, pathMeasure.getLength(), dst, true);
                canvas.drawPath(dst, searchPaint);
                break;
            case SEARCHING:
                pathMeasure.setPath(circlePath, false);
                float stop = pathMeasure.getLength() * animatedValue;
                float start = (float) (stop - ((0.5 - Math.abs(animatedValue - 0.5)) * 100f));
                dst2.reset();
                pathMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, searchPaint);
                break;
            case ENDING:
                pathMeasure.setPath(searchPath, false);
                dst3.reset();
                pathMeasure.getSegment(pathMeasure.getLength() * animatedValue, pathMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, searchPaint);
                break;
        }

    }

    public boolean isSearching() {
        return currentState == State.SEARCHING;
    }

    public void start() {
        currentState = State.STARTING;
        startAnimator.start();
        pathMeasure.setPath(searchPath, false);

    }

    public void searchOver() {
        isSearchOver = true;
    }

    public int getSearchColor() {
        return searchColor;
    }

    public void setSearchColor(int searchColor) {
        this.searchColor = searchColor;
        invalidate();
    }

    public float getSearchBorder() {
        return searchBorder;
    }

    public void setSearchBorder(float searchBorder) {
        this.searchBorder = searchBorder;
        invalidate();
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
        invalidate();
    }

    private int dp2px(float dimens) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dimens + 0.5F);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    private void cancel() {
        startAnimator.removeAllUpdateListeners();
        startAnimator.cancel();
        searchingAnimator.removeAllUpdateListeners();
        searchingAnimator.cancel();
        endingAnimator.removeAllUpdateListeners();
        endingAnimator.cancel();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
    }
}
