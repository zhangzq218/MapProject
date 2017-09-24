package com.meitu.mapproject.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by zhangzq on 2017/9/21.
 */

public class CustomFrameLayout extends FrameLayout {
    private float mCurrentX, mCurrentY;
    private IEvent iEvent;

    public CustomFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_DOWN:
                //开启硬件加速
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
                if (iEvent.isFun(ev.getX(), ev.getY()))
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
                //关闭硬件加速
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                if (ev.getX() == mCurrentX && ev.getY() == mCurrentY && !iEvent.isFun(ev.getX(), ev.getY())) {
                    //并且当前的ev的位置等于落下的位置，那么mCurrentView=null
                    iEvent.setCurrentView(null);
                    //清楚view的所有状态
                    iEvent.setViewFocused(-1);
                    return super.onInterceptTouchEvent(ev);
                }
            default:
                if (iEvent.getCurrentView() != null) {
                    return super.onInterceptTouchEvent(ev);
                }
                return true;
        }
    }

    public void setiEvent(IEvent iEvent) {
        this.iEvent = iEvent;
    }
}
