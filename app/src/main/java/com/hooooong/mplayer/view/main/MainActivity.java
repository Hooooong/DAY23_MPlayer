package com.hooooong.mplayer.view.main;

import android.Manifest;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hooooong.mplayer.R;
import com.hooooong.mplayer.data.Const;
import com.hooooong.mplayer.data.model.Music;
import com.hooooong.mplayer.util.Player;
import com.hooooong.mplayer.util.PlayerService;
import com.hooooong.mplayer.view.BaseActivity;
import com.hooooong.mplayer.view.main.adapter.ListPagerAdapter;
import com.hooooong.mplayer.view.player.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends BaseActivity implements View.OnClickListener, MusicFragment.OnListFragmentInteractionListener, Player.Listener {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private RelativeLayout relativeLayout;
    private ImageView imgAlbum, imgPrev, imgPlay, imgNext;
    private TextView textTitle, textArtist;

    private Intent serviceIntent;


    public MainActivity() {
        super(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void init() {
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, PlayerService.class);

        load();
        initView();
        initTabLayout();
        initViewPager();
        initListener();
        checkPlayer();
    }

    private void initListener() {
        // TabLayout 을 ViewPager 에 연결
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        // ViewPager 에 변경사항을 TabLayout 에 전달
        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initViewPager() {
        MusicFragment fragmentTitle = MusicFragment.newInstance(1);
        MusicFragment fragmentArtist = MusicFragment.newInstance(1);
        MusicFragment fragmentAlbum = MusicFragment.newInstance(1);
        MusicFragment fragmentGenre = MusicFragment.newInstance(1);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentTitle);
        fragmentList.add(fragmentArtist);
        fragmentList.add(fragmentAlbum);
        fragmentList.add(fragmentGenre);

        ListPagerAdapter customAdapter = new ListPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(customAdapter);
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_Artist)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_Album)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_Genre)));
    }

    private void initView() {
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
        imgAlbum = (ImageView)findViewById(R.id.imgAlbum);
        imgPrev = (ImageView)findViewById(R.id.imgPrev);
        imgPlay = (ImageView)findViewById(R.id.imgPlay);
        imgNext = (ImageView)findViewById(R.id.imgNext);
        textTitle = (TextView) findViewById(R.id.textTitle);
        textArtist = (TextView)findViewById(R.id.textArtist);

        relativeLayout.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgNext.setOnClickListener(this);
    }

    private void load() {
        Music.getInstance().load(this);
        // Load 에서 가장 최근 재생했던 노래를 불러와야 한다.
    }

    @Override
    public List<Music.Item> getList() {
        return Music.getInstance().getItemList();
    }

    @Override
    public void openPlayer(int position) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        // putExtra 의 String 값은 상수의 이름이기 때문에
        // class 를 만들어서
        intent.putExtra(Const.KEY_POSITION, position);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Player.getInstance().addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Player.getInstance().removeListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkPlayer();
    }

    private void checkPlayer() {
        if((Player.getInstance().getStatus() == Const.STAT_PLAY) || (Player.getInstance().getStatus() == Const.STAT_PAUSE)){
            relativeLayout.setVisibility(View.VISIBLE);
            setMusic(Player.getInstance().getCurrent());
        }else{
            relativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setProgress() {

    }

    @Override
    public void setMusic(int current) {
        Music.Item item = Music.getInstance().getItemList().get(current);

        Glide.with(getBaseContext())
                .load(item.albumUri)
                .apply(bitmapTransform(new CircleCrop()))
                .into(imgAlbum);

        textTitle.setText(item.title);
        textTitle.setSelected(true);
        textArtist.setText(item.artist);
        textArtist.setSelected(true);

        if(Player.getInstance().getStatus() == Const.STAT_PLAY) {
            imgPlay.setImageResource(R.drawable.ic_stop_black);
        }else{
            imgPlay.setImageResource(R.drawable.ic_play_arrow_black);
        }
    }

    @Override
    public void setPlay() {
        togglePlayButton(Const.STAT_PLAY);
    }

    @Override
    public void setPause() {
        togglePlayButton(Const.STAT_PAUSE);

    }

    @Override
    public void setStop() {
        relativeLayout.setVisibility(View.GONE);

    }

    private void togglePlayButton(int status) {
        if (status == Const.STAT_PLAY) {
            imgPlay.setImageResource(R.drawable.ic_stop_black);
        } else  {
            imgPlay.setImageResource(R.drawable.ic_play_arrow_black);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.relativeLayout:
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra(Const.KEY_CLICK, 111);
                startActivity(intent);
                break;
            case R.id.imgPrev:
                playerPrev();
                break;
            case R.id.imgNext:
                playerNext();
                break;
            case R.id.imgPlay:
                if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
                    playerPause();
                } else  {
                    playerStart();
                }
                break;
        }

    }

    private void playerPrev() {
        serviceIntent.setAction(Const.ACTION_PREV);
        startService(serviceIntent);
    }

    private void playerNext() {
        serviceIntent.setAction(Const.ACTION_NEXT);
        startService(serviceIntent);
    }

    private void playerStart() {
        serviceIntent.setAction(Const.ACTION_START);
        startService(serviceIntent);
    }

    private void playerPause() {
        serviceIntent.setAction(Const.ACTION_PAUSE);
        startService(serviceIntent);
    }
}
