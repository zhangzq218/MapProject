package com.meitu.mapproject.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhangzq on 2017/9/2.
 */

public class ToastUtils {
    private static boolean mIsShow = true;//默认显示  
    private static Toast mToast = null;//全局唯一的Toast  

    // private控制不应该被实例化
    private ToastUtils() {
        throw new UnsupportedOperationException("不能被实例化");
    }

    /**
     *  
     * * 全局控制是否显示Toast 
     * * @param isShowToast 
     *      
     */
    public static void controlShow(boolean isShowToast) {
        mIsShow = isShowToast;
    }

    /**
     *  
     * * 取消Toast显示 
     *      
     */
    public void cancelToast() {
        if (mIsShow && mToast != null) {
            mToast.cancel();
        }
    }

    /**
     *  
     * * 短时间显示Toast 
     * * @param context 
     * * @param message     
     */
    public static void showShort(Context context, CharSequence message) {
        if (mIsShow) {
            if (mToast == null) {
                mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * * 短时间显示Toast 
     * * @param context 
     * * @param resId 资源ID:getResources().getString(R.string.xxxxxx);     
     */
    public static void showShort(Context context, int resId) {
        if (mIsShow) {
            if (mToast == null) {
                mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }

    /**
     *  
     * * 长时间显示Toast
     * * @param context 
     * * @param message 
     *      
     */
    public static void showLong(Context context, CharSequence message) {
        if (mIsShow) {
            if (mToast == null) {
                mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     *  
     * * 长时间显示Toast
     * * @param context 
     * * @param resId 资源ID:getResources().getString(R.string.xxxxxx);      
     */
    public static void showLong(Context context, int resId) {
        if (mIsShow) {
            if (mToast == null) {
                mToast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }

    /**
     * * 自定义显示Toast时间 
     * * @param context 
     * * @param message 
     * * @param duration 单位:毫秒     
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (mIsShow) {
            if (mToast == null) {
                mToast = Toast.makeText(context, message, duration);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    /**
     * * 自定义显示Toast时间 
     * * @param context 
     * * @param resId 资源ID:getResources().getString(R.string.xxxxxx); 
     * * @param duration 单位:毫秒 
     */
    public static void show(Context context, int resId, int duration) {
        if (mIsShow) {
            if (mToast == null) {
                mToast = Toast.makeText(context, resId, duration);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }
}
