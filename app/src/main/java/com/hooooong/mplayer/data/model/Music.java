package com.hooooong.mplayer.data.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Hong on 2017-10-11.
 */

public class Music {

    private static Music music;

    private Music() {}

    public static Music getInstance() {
        if (music == null) {
            music = new Music();
        }
        return music;
    }

    private List<Item> itemList = new ArrayList<>();

    // music data load
    public void load(Context context) {
        ContentResolver resolver = context.getContentResolver();
        // 1. 테이블 명 정의
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // 2. 불러올 컬럼명 정의
        String projections[] = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        };
        // 3. Query
        Cursor cursor = resolver.query(uri, projections, null, null, projections[3] + " ASC");

        // 4. Query 결과를 담은 Cursor 를 통해 데이터 가져오기
        itemList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Item item = new Item();
                item.id = getValue(cursor, projections[0]);
                item.albumId = getValue(cursor, projections[1]);
                item.artist = getValue(cursor, projections[2]);
                item.title = getValue(cursor, projections[3]);
                item.duration = Integer.parseInt(getValue(cursor, projections[4]));
                item.musicUri = makeMusicUri(item.id);
                item.albumUri = makeAlbumUri(item.albumId);
                itemList.add(item);
            }
        }
    }

    private static String getValue(Cursor cursor, String name) {
        int index = cursor.getColumnIndex(name);
        return cursor.getString(index);
    }

    public List<Item> getItemList() {
        return itemList;
    }

    private Uri makeMusicUri(String musicId) {
        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(contentUri, musicId);
    }

    private Uri makeAlbumUri(String albumId){
        String contentUri = "content://media/external/audio/albumart/";
        return Uri.parse(contentUri + albumId);
    }

    // 실제 뮤직 데이터 class
    public class Item {
        public String id;           // music ID
        public String albumId;      // album ID
        public String artist;
        public String title;
        public int duration;

        public Uri musicUri;        // music uri
        public Uri albumUri;        // Album image uri
    }
}
