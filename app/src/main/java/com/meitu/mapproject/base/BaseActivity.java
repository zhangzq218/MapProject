package com.meitu.mapproject.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 所有Activity的基类，】
 *
 * @author zhangzq on 2017/9/2.
 */
public abstract class BaseActivity extends Activity {
    public static PermissionListener mListener;
    public static final int REQUEST_CODE = 1;//用于运行时权限请求的请求码
    private Unbinder mUnbinder;
    protected Context mContext;
    private ProgressDialog mProgressDialog;

    // onCreate 中保存Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(getLayoutId());
        mContext = this;
        ActivityCollector.addActivity(this);
        mUnbinder = ButterKnife.bind(this);
        initViewsAndEvents(savedInstanceState);
        initData();

    }

    public abstract int getLayoutId();

    public abstract void initViewsAndEvents(Bundle savedInstanceState);

    protected abstract void initData();

    public void showProgressDialog() {
        showProgressDialog("");
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, true);
    }

    public void showProgressDialog(String message, boolean cancelable) {
        mProgressDialog = ProgressDialog.show(mContext, "", message);
        mProgressDialog.setCancelable(cancelable);
    }

    public void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        ActivityCollector.removeActivity(this);
    }

    public Context getContext() {
        return this;
    }

    public static void requestRuntimePermission(String[] permissions, PermissionListener listener) {
        // 获取栈顶Activity
        Activity topActivity = ActivityCollector.getTopActivity();
        if (topActivity == null) return;
        mListener = listener;
        // 需要请求的权限列表
        List<String> requestPermisssionList = new ArrayList<>();
        // 检查权限 是否已被授权
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED)
                // 未授权时添加该权限
                requestPermisssionList.add(permission);
        }
        if (requestPermisssionList.isEmpty())
            // 所有权限已经被授权过 回调Listener onGranted方法 已授权
            listener.onGranted();
        else
            // 进行请求权限操作
            ActivityCompat.requestPermissions(topActivity, requestPermisssionList.toArray(new String[requestPermisssionList.size()]), REQUEST_CODE);
    }

    // 请求权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                List<String> deniedPermissionList = new ArrayList<>();
                // 检查返回授权结果不为空
                if (grantResults.length > 0) {
                    // 判断授权结果
                    for (int i = 0; i < grantResults.length; i++) {
                        int result = grantResults[i];
                        if (result != PackageManager.PERMISSION_GRANTED)
                            // 保存被用户拒绝的权限
                            deniedPermissionList.add(permissions[i]);
                    }
                    if (deniedPermissionList.isEmpty())
                        // 都被授权 回调Listener onGranted方法 已授权
                        mListener.onGranted();
                    else // 有权限被拒绝 回调Listner onDeynied方法
                        mListener.onDenied(deniedPermissionList);
                }
                break;
            }
            default:
                break;
        }
    }
}

