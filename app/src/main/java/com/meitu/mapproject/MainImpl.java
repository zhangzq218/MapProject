package com.meitu.mapproject;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.meitu.mapproject.map.CustomView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhangzq on 2017/9/20.
 */

public class MainImpl implements IMain {
    private MainCallback mainCallback;
    private Context mContext;
    private List<CustomView> mDataList;
    private float mWidth, mHeight;
    private Bitmap mBitmap, bitmap;
    private float mFinalScale;

    public MainImpl(Context context, MainCallback mainCallback) {
        mContext = context.getApplicationContext();
        this.mainCallback = mainCallback;
        mDataList = new ArrayList<>();
    }

    public void setScreenSize(float mWidth, float mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    @Override
    public void addView(CustomView customView) {
        mDataList.add(customView);
    }

    @Override
    public void saveView() {
        rx.Observable.just("").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                Bitmap originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(originalBitmap);
                for (CustomView customView : mDataList) {
                    Matrix matrix = new Matrix();
                    matrix.set(customView.getMatrix(mBitmap.getWidth(), mBitmap.getHeight()));
                    matrix.postScale(1 / mFinalScale, 1 / mFinalScale);
                    canvas.drawBitmap(customView.getFinalBitmap(), matrix, null);
                }
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), originalBitmap, "", "");
                return null;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mainCallback.onSaveBitmapSuccess();
                    }
                });

    }

    /**
     * 释放资源
     */
    @Override
    public void releaseResource() {
        mBitmap.recycle();
        bitmap.recycle();
    }

    public void getBitmap(final ContentResolver cr, final Uri url) {
        rx.Observable.just(url).map(new Func1<Uri, Bitmap>() {
            @Override
            public Bitmap call(Uri uri) {
                InputStream input = null;
                try {
                    input = cr.openInputStream(url);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap = BitmapFactory.decodeStream(input);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scalWidth = mWidth / bitmap.getWidth();
                float scalHeight = mHeight / bitmap.getHeight();
                mFinalScale = Math.min(scalWidth, scalHeight);
                Matrix matrix = new Matrix();
                matrix.postScale(mFinalScale, mFinalScale);
                mBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                return mBitmap;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        mainCallback.onGetBitmapSuccess(bitmap);
                    }
                });
    }

}
