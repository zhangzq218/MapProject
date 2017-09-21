package com.meitu.mapproject;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.meitu.mapproject.base.BaseActivity;
import com.meitu.mapproject.base.PermissionListener;
import com.meitu.mapproject.map.CustomView;
import com.meitu.mapproject.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainCallback {
    @BindView(R.id.map_rl)
    FrameLayout mMapRl;
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
    int i=0;

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

    @OnClick({R.id.add_btn, R.id.save_btn,R.id.add_sensity_btn,R.id.sub_sensity_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                addView();
                break;
            case R.id.save_btn:
                mainImpl.saveView();
                break;
            case R.id.add_sensity_btn:
                mSensity+=0.2;
                mCustomView.setSensitivity(mSensity);
                break;
            case R.id.sub_sensity_btn:
                if (mSensity>0.2) {
                    mSensity -= 0.2;
                    mCustomView.setSensitivity(mSensity);
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
        mCustomView = new CustomView(this, mMapRl.getWidth(), mMapRl.getHeight(),i);
        mCustomView.setSensitivity(mSensity);
        mCustomView.setBackground(R.drawable.photo_ic);
        mCustomView.setImageResource(R.drawable.bc_ic);
        mCustomView.setDefaultSize(mDefaultWidth, mDefaultHeight);
        mCustomView.setMoveRange(mMapRl.getLeft() + mSpaceSize, mMapRl.getTop() + mSpaceSize,
                mMapRl.getRight() - mSpaceSize, mMapRl.getBottom() - mSpaceSize);
        mainImpl.addView(mCustomView);
        mMapRl.addView(mCustomView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mainImpl.setScreenSize(mMapRl.getWidth(), mMapRl.getHeight());
        mainImpl.getBitmap(getContentResolver(), mUri);
    }

    @Override
    protected void onDestroy() {
        mainImpl.releaseResource();
        super.onDestroy();
    }

    @Override
    public void onGetBitmapSuccess(Bitmap bitmap) {
        setImage(bitmap);
    }

    @Override
    public void onSaveBitmapSuccess() {
        ToastUtils.showShort(this, "保存成功");
    }
}