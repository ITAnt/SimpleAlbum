package com.pax.simplealbum;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.pax.simplealbum.adapter.AlbumAdapter;
import com.pax.simplealbum.bean.MediaData;
import com.pax.simplealbum.util.MediaUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MEDIA_CONTENT_CONTROL};

    private GridView gv_album;
    private AlbumAdapter mAdapter;
    private List<MediaData> mMediaDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requestPermissions(REQUIRED_PERMISSIONS, 0);
        gv_album = findViewById(R.id.gv_album);
        mMediaDataList = new ArrayList<>();
        mAdapter = new AlbumAdapter(mMediaDataList, this);
        mAdapter.setOnMediaClickListener(new AlbumAdapter.OnMediaClickListener() {
            @Override
            public void onClick(MediaData mediaData) {
                if (mediaData.getType() == MediaData.TYPE_PHOTO) {
                    // photo
                    Intent detailIntent = new Intent(MainActivity.this, PhotoDetailActivity.class);
                    detailIntent.putExtra(PhotoDetailActivity.PHOTO_URI, mediaData.getRawPath());
                    startActivity(detailIntent);
                } else {
                    // video
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(getApplicationContext(), "com.pax.simplealbum.fileprovider", new File(mediaData.getRawPath()));
                    } else {
                        uri = Uri.fromFile(new File(mediaData.getRawPath()));
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    grantUriPermission(getPackageName(),uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//增加读写权限
                    startActivity(intent);
                }
            }
        });
        gv_album.setAdapter(mAdapter);

        if (!hasPermission()) {
            requestPermission();
        } else {
            loadLocalMedia();
        }
    }

    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
            .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(granted -> {
                if (granted) {
                    loadLocalMedia();
                } else {
                    Toast.makeText(MainActivity.this, "No permission", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private List<MediaData> getMediaData() {
        List<MediaData> mediaDataList = new ArrayList<>();
        mediaDataList.addAll(MediaUtils.getPhotos(this));
        mediaDataList.addAll(MediaUtils.getVideos(this));
        return mediaDataList;
    }

    private void loadLocalMedia() {
        Observable<List<MediaData>> observable=Observable.create(new ObservableOnSubscribe<List<MediaData>>() {
            @Override
            //将事件发射出去, 持有观察者的对象
            public void subscribe(ObservableEmitter<List<MediaData>> emitter) throws Exception {
                emitter.onNext(getMediaData());
                emitter.onComplete();
            }
        });

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MediaData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MediaData> mediaDataList) {
                        // media loaded
                        mMediaDataList.clear();
                        mMediaDataList.addAll(mediaDataList);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "请检查权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(MainActivity.this, "加载完毕", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
