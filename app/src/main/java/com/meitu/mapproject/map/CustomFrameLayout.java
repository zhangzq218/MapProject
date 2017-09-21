package com.meitu.mapproject.map;

import android.content.ContentUris;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by zhangzq on 2017/9/21.
 */

public class CustomFrameLayout extends FrameLayout {
    private CustomView mCurrentView;
    private float mCurrentX, mCurrentY;
    private List<CustomView> mCustomViewList;
    private float mSensity = 1;

    public CustomFrameLayout(@NonNull Context context) {
        super(context);
        mCustomViewList = new ArrayList<>();
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mCustomViewList = new ArrayList<>();
    }

    public void setCurrentView(CustomView currentView) {
        mCurrentView = currentView;
        mCustomViewList.add(currentView);
    }

    public void setSensity(float sensity) {
        mSensity = sensity;
        if (mCurrentView != null) {
            mCurrentView.setSensitivity(mSensity);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_DOWN:
                if (isFun(ev.getX(), ev.getY()))//置于最上层
                //设置当前mCurrentView
                {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    //记录ev的当前点击位置
                    mCurrentX = ev.getX();
                    mCurrentY = ev.getY();
                    return super.onInterceptTouchEvent(ev);
                }
                //i
            case ACTION_UP:
                if (ev.getX() == mCurrentX && ev.getY() == mCurrentY && !isFun(ev.getX(), ev.getY())) {
                    //并且当前的ev的位置等于落下的位置，那么mCurrentView=null
                    mCurrentView = null;
                    return super.onInterceptTouchEvent(ev);
                }
            default:
                if (mCurrentView != null) {
                    return super.onInterceptTouchEvent(ev);
                }
                return true;
        }
    }

    /**
     * 判断（x,y）是否在当前view当中，如果存在直接返回这个view，否则返回false
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isFun(float x, float y) {
        int size = mCustomViewList.size();
        for (int i = size - 1; i >= 0; i--) {
            CustomView customView = mCustomViewList.get(i);
            if (customView.isContains(x, y)) {
                mCurrentView = customView;
                mCurrentView.bringToFront();
                mCurrentView.setSensitivity(mSensity);
                mCustomViewList.add(mCurrentView);
                mCustomViewList.remove(i);
                return true;
            }
        }
        return false;
    }
}
