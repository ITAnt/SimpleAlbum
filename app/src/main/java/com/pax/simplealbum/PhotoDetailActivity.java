package com.pax.simplealbum;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

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
public class PhotoDetailActivity extends Activity {
    public static final String PHOTO_URI = "photo_uri";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        PhotoView pv_photo_detail = findViewById(R.id.pv_photo_detail);
        pv_photo_detail.setImageURI(Uri.fromFile(new File(getIntent().getStringExtra(PHOTO_URI))));
    }
}
