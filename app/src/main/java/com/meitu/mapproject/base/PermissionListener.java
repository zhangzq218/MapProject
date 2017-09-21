package com.meitu.mapproject.base;

import java.util.List;

/**
 * @author zhangzq on 2017/9/2.
 */

public interface PermissionListener {
    /**
     * 授权成功
     */
    void onGranted();

    /**
     * 授权失败
     *
     * @param deniedPermission
     */
    void onDenied(List<String> deniedPermission);
}