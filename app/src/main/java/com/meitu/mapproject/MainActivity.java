package com.meitu.mapproject;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.meitu.mapproject.base.BaseActivity;
import com.meitu.mapproject.base.PermissionListener;
import com.meitu.mapproject.map.CustomFrameLayout;
import com.meitu.mapproject.map.CustomView;
import com.meitu.mapproject.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends BaseActivity implements MainCallback {
    @BindView(R.id.map_fl)
    CustomFrameLayout mMapFl;
    @BindView(R.id.add_btn)
    Button mAddBtn;
    @BindView(R.id.img_iv)
    ImageView mImgIv;
    @BindView(R.id.save_btn)
    Button mSaveBtn;
    public static final int REQUEST_CODE_PICK_IMAGE = 300; //从图库中选择图片之后返回结果的请求码
    private int mDefaultWidth = 300, mDefaultHeight = 300;
    private int mSpaceSize = 0;
    private IMain mainImpl;
    private Uri mUri;
    private CustomView mCustomView;
    private float mSensity = 1;
    private boolean isEnter = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViewsAndEvents(Bundle savedInstanceState) {
        requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                choosePhoto();
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        //获取图库中的图片并将其展示
                        mUri = data.getData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 打开选择图片的界面
     */
    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @OnClick({R.id.add_btn, R.id.save_btn, R.id.add_sensity_btn, R.id.sub_sensity_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                addView();
                break;
            case R.id.save_btn:
                mainImpl.saveView();
                break;
            case R.id.add_sensity_btn:
                mSensity += 0.2;
                mMapFl.setSensity(mSensity);
                break;
            case R.id.sub_sensity_btn:
                if (mSensity > 0.2) {
                    mSensity -= 0.2;
                    mMapFl.setSensity(mSensity);
                }
                break;
        }
    }

    @Override
    protected void initData() {
        mainImpl = new MainImpl(getContext(), this);
    }

    private void setImage(Bitmap bitmap) {
        mImgIv.setImageBitmap(bitmap);
    }


    /**
     * 添加贴图
     */
    private void addView() {
        mCustomView = new CustomView(this, mMapFl.getWidth(), mMapFl.getHeight());
        mCustomView.setImageResource(R.drawable.photo_ex_ic);
        mCustomView.setDefaultSize(mDefaultWidth, mDefaultHeight);
        mCustomView.setMoveRange(mMapFl.getLeft() + mSpaceSize, mMapFl.getTop() + mSpaceSize,
                mMapFl.getRight() - mSpaceSize, mMapFl.getBottom() - mSpaceSize);
        mainImpl.addView(mCustomView);
        mMapFl.addView(mCustomView);
        mMapFl.setCurrentView(mCustomView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isEnter) {
            mainImpl.setScreenSize(mMapFl.getWidth(), mMapFl.getHeight());
            mainImpl.getBitmap(getContentResolver(), mUri);
            isEnter = false;
        }
    }

    @Override
    protected void onDestroy() {
        mainImpl.releaseResource();
        super.onDestroy();
    }

    @Override
    public void onGetBitmapSuccess(Bitmap bitmap) {
        if (bitmap != null) {
            setImage(bitmap);
        }
    }

    @Override
    public void onSaveBitmapSuccess() {
        ToastUtils.showShort(this, R.string.save_btn_success);
        finish();
    }
}