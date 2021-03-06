package com.meitu.mapproject;

import android.content.ContentResolver;
import android.net.Uri;

import com.meitu.mapproject.widget.view.CustomView;

import java.util.List;

/**
 * Created by zhangzq on 2017/9/20.
 */

public interface IMain {
    void getBitmap(final ContentResolver cr, final Uri url);

    void setScreenSize(float mWidth, float mHeight);

    void addView(CustomView customView);

    void saveView();

    void releaseResource();
}
