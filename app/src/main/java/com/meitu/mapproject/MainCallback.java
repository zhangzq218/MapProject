package com.meitu.mapproject;

import android.graphics.Bitmap;

/**
 * Created by zhangzq on 2017/9/20.
 */

public interface MainCallback {
    void onGetBitmapSuccess(Bitmap bitmap);
    void onSaveBitmapSuccess();
}
