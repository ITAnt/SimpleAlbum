package com.pax.simplealbum.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pax.simplealbum.R;
import com.pax.simplealbum.bean.MediaData;
import com.pax.simplealbum.util.MediaUtils;

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
public class AlbumAdapter extends BaseAdapter {
    private List<MediaData> mMediaDataList;
    private Context mContext;
    private int mPhotoHeight;
    private int mSpace;

    public AlbumAdapter(List<MediaData> mMediaDataList, Context context) {
        this.mMediaDataList = mMediaDataList;
        this.mContext = context;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mPhotoHeight = displayMetrics.widthPixels / 3;

        mSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return mMediaDataList == null ? 0 : mMediaDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMediaDataList == null ? null : mMediaDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MediaData mediaData = mMediaDataList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_album, null);
            mHolder = new ViewHolder();
            mHolder.rl_photo = convertView.findViewById(R.id.rl_photo);
            mHolder.iv_thumbnail = convertView.findViewById(R.id.iv_thumbnail);
            mHolder.tv_title = convertView.findViewById(R.id.tv_title);
            mHolder.iv_is_video = convertView.findViewById(R.id.iv_is_video);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        ViewGroup.LayoutParams layoutParams = mHolder.rl_photo.getLayoutParams();
        if (layoutParams == null) {
            // 必须这样做，否则每次都new LayoutParams的话，会让第一个item无法响应点击事件
            layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mPhotoHeight);
        }

        int index = position % 3;
        switch (index) {
            case 0:
                if (position >= mMediaDataList.size()-3) {
                    mHolder.rl_photo.setPadding(mSpace, mSpace, mSpace/2, mSpace);
                } else {
                    mHolder.rl_photo.setPadding(mSpace, mSpace, mSpace/2, 0);
                }
                break;
            case 1:
                if (position >= mMediaDataList.size()-3) {
                    mHolder.rl_photo.setPadding(mSpace/2, mSpace, mSpace/2, mSpace);
                } else {
                    mHolder.rl_photo.setPadding(mSpace/2, mSpace, mSpace/2, 0);
                }
                break;
            case 2:
                if (position >= mMediaDataList.size()-3) {
                    mHolder.rl_photo.setPadding(mSpace/2, mSpace, mSpace, mSpace);
                } else {
                    mHolder.rl_photo.setPadding(mSpace/2, mSpace, mSpace, 0);
                }
                break;
        }

        mHolder.rl_photo.setLayoutParams(layoutParams);
        mHolder.rl_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMediaClickListener != null) {
                    onMediaClickListener.onClick(mediaData);
                }
            }
        });

        Bitmap bitmap = MediaUtils.readBitMap(mediaData.getThumbnailPath());
        if (bitmap != null) {
            mHolder.iv_thumbnail.setImageBitmap(bitmap);
        }
        mHolder.tv_title.setText(mediaData.getName());

        if (mediaData.getType() == MediaData.TYPE_VIDEO) {
            mHolder.iv_is_video.setVisibility(View.VISIBLE);
        } else {
            mHolder.iv_is_video.setVisibility(View.GONE);
        }

        return convertView;
    }

    private ViewHolder mHolder;
    private static class ViewHolder {
        private RelativeLayout rl_photo;
        private ImageView iv_thumbnail;
        private TextView tv_title;
        private ImageView iv_is_video;
    }

    private OnMediaClickListener onMediaClickListener;
    public interface OnMediaClickListener {
        void onClick(MediaData mediaData);
    }

    public OnMediaClickListener getOnMediaClickListener() {
        return onMediaClickListener;
    }

    public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
        this.onMediaClickListener = onMediaClickListener;
    }
}
