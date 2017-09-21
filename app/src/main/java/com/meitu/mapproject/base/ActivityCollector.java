package com.meitu.mapproject.base;

import android.app.Activity;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过ActivityCollector对activity进行统一管理
 *
 * @author zhangzq on 2017/9/2.
 */

public class ActivityCollector {
    private static List<Activity> mActivityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    // 获取栈顶Activity
    @Nullable
    public static Activity getTopActivity() {
        if (mActivityList.isEmpty()) return null;
        return mActivityList.get(mActivityList.size() - 1);
    }
}
