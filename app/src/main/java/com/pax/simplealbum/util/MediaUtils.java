package com.pax.simplealbum.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.pax.simplealbum.bean.MediaData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2017 - ? Pax Corporation. All rights reserved.
 * Module Date: 2018/4/2
 * Module Author: Jason Zhan
 * Description:
 *
 * ============================================================================
 */
public class MediaUtils {
    public static List<MediaData> getPhotos(Context context) {
        List<MediaData> photoList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        //先得到缩略图的URL和对应的图片id
        Cursor thumbnailCursor = contentResolver.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Thumbnails.IMAGE_ID,
                        MediaStore.Images.Thumbnails.DATA
                },
                null,
                null,
                null);

        if (thumbnailCursor != null && thumbnailCursor.moveToFirst()) {
            do {
                MediaData photoData = new MediaData();
                photoData.setThumbnailPath(thumbnailCursor.getString(1));

                // 获取图片原路径
                Cursor rawCursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE
                        },
                        MediaStore.Audio.Media._ID+"="+thumbnailCursor.getInt(0),
                        null,
                        null
                );

                if (rawCursor != null && rawCursor.moveToFirst()) {
                    do {
                        photoData.setRawPath(rawCursor.getString(0));
                        photoData.setName(rawCursor.getString(1));
                    } while (rawCursor.moveToNext());
                    rawCursor.close();
                }

                photoList.add(photoData);
            } while (thumbnailCursor.moveToNext());
            thumbnailCursor.close();
        }

        return photoList;
    }

    public static List<MediaData> getVideos(Context context) {
        List<MediaData> videoList = new ArrayList<>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.VIDEO_ID,
                MediaStore.Video.Thumbnails.DATA};
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION, MediaStore.Video.Media.TITLE};

        Cursor rawCursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns,
                null,
                null,
                null);

        if (rawCursor == null) {
            return videoList;
        }
        if (rawCursor.moveToFirst()) {
            do {
                MediaData videoData = new MediaData();
                int id = rawCursor.getInt(rawCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String name = rawCursor.getString(rawCursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                Cursor thumbCursor = context.getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id,
                        null,
                        null);
                if (thumbCursor != null && thumbCursor.moveToFirst()) {
                    // 缩略图路径
                    videoData.setThumbnailPath(thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                    videoData.setName(name);
                    videoData.setType(MediaData.TYPE_VIDEO);
                    videoData.setRawPath(rawCursor.getString(rawCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                    videoData.setDuration(rawCursor.getInt(rawCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                    videoList.add(videoData);

                    thumbCursor.close();
                }

            } while (rawCursor.moveToNext());

            rawCursor.close();
        }
        return videoList;
    }

    public static Bitmap readBitMap(String path){
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is,null,opt);
    }
}
