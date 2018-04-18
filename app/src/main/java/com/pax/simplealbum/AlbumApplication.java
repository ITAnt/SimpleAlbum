package com.pax.simplealbum;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2017 - ? Pax Corporation. All rights reserved.
 * Module Date: 2018/4/4
 * Module Author: Jason Zhan
 * Description:
 *
 * ============================================================================
 */
public class AlbumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //从Android 7.0开始，一个应用提供自身文件给其它应用使用时，如果给出一个file://格式的URI的话，
        // 应用会抛出FileUriExposedException。这是由于谷歌认为目标app可能不具有文件权限，
        // 会造成潜在的问题。所以让这一行为快速失败。
        // https://blog.csdn.net/xiaoyu940601/article/details/54406725
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
}
