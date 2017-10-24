package com.hooooong.mplayer.view.player.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hooooong.mplayer.R;
import com.hooooong.mplayer.data.model.Music;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Android Hong on 2017-10-11.
 */

public class PlayerPageAdapter extends PagerAdapter {

    Context context;
    List<Music.Item> musicList;

    public PlayerPageAdapter(Context context, List<Music.Item> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_player, null);
        ImageView imgAlbum = view.findViewById(R.id.imgAlbum);
        ImageView imgFullAlbum = view.findViewById(R.id.imgFullAlbum);

        Music.Item item = musicList.get(position);
        // ImageView setting
        Glide.with(context)
                .load(item.albumUri)
                .apply(bitmapTransform(new CircleCrop()))
                .into(imgAlbum);
        Glide.with(context)
                .load(item.albumUri)
                .apply(bitmapTransform(new BlurTransformation(20)))
                .into(imgFullAlbum);

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
