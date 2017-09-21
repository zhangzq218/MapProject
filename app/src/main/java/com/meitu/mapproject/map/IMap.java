package com.meitu.mapproject.map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by zhangzq on 2017/9/18.
 */

public interface IMap {
    /**
     * 设置控件可以移动的范围
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    void setMoveRange(int left, int top, int right, int bottom);

    /**
     * 设置控件的背景图片
     *
     * @param bitmap
     */
    void setBackground(Bitmap bitmap);

    void setBackground(int resId);

    void setBackground(Drawable d);

    /**
     * 设置控件针对旋转、缩放、移动的灵敏度
     *
     * @param f
     */
    void setSensitivity(float f);

    /**
     * 设置控件默认的大小
     *
     * @param width
     * @param height
     */
    void setDefaultSize(int width, int height);

    /**
     * 获取控件经过矩阵变换之后的bitmap
     *
     * @return
     */
    Bitmap getFinalBitmap();
}
