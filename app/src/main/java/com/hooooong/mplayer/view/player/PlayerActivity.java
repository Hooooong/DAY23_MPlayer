package com.hooooong.mplayer.view.player;

import android.Manifest;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hooooong.mplayer.R;
import com.hooooong.mplayer.data.Const;
import com.hooooong.mplayer.data.model.Music;
import com.hooooong.mplayer.util.Player;
import com.hooooong.mplayer.util.PlayerService;
import com.hooooong.mplayer.view.BaseActivity;
import com.hooooong.mplayer.view.player.adapter.PlayerPageAdapter;


public class PlayerActivity extends BaseActivity implements View.OnClickListener, Player.Listener {

    
    private boolean seekBarFlag = false;
    private Music music;
    private int current = -1;
    private int click = -1;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textDuration;
    private TextView textTitle;
    private TextView textArtist;
    private ImageView imgPlay;
    private ImageView imgFf;
    private ImageView imgRew;
    private ImageView imgNext;
    private ImageView imgPrev;

    private Intent serviceIntent;

    public PlayerActivity() {
        super(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void init() {
        setContentView(R.layout.activity_player);

        intentSetting();
        load();
        initView();
        initViewPager();
        initPlayer();
        initSeekBar();
    }

    // Service Intent 설정
    private void intentSetting() {
        serviceIntent = new Intent(this, PlayerService.class);
    }

    private void load() {
        music = Music.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            current = intent.getIntExtra(Const.KEY_POSITION, 0);
            click = intent.getIntExtra(Const.KEY_CLICK, -1);
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textTitle = toolbar.findViewById(R.id.textTitle);
        textTitle.setSelected(true);
        textArtist = toolbar.findViewById(R.id.textArtist);
        textArtist.setSelected(true);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textCurrentTime = (TextView) findViewById(R.id.textCurrentTime);
        textDuration = (TextView) findViewById(R.id.textDuration);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgFf = (ImageView) findViewById(R.id.imgFf);
        imgRew = (ImageView) findViewById(R.id.imgRew);
        imgPrev = (ImageView) findViewById(R.id.imgPrev);
        imgNext = (ImageView) findViewById(R.id.imgNext);

        imgPlay.setOnClickListener(this);
        imgFf.setOnClickListener(this);
        imgRew.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
    }

    private void initViewPager() {
        PlayerPageAdapter playerPageAdapter = new PlayerPageAdapter(this, music.getItemList());
        viewPager.setAdapter(playerPageAdapter);
    }

    private void initPlayer() {
        if(click != 111){
            // 목록을 눌렀을 경우 를 눌렀을 경우
            playerSet();
            playerStart();
        }else{
            // 하단 Player 를 눌렀을 경우 를 눌렀을 경우
            playerCheck(Player.getInstance().getCurrent());
            togglePlayButton(Player.getInstance().getStatus());
        }
    }

    private void playerCheck(int current) {
        this.current = current;
        viewPager.clearOnPageChangeListeners();
        initPlayerView();
        viewPager.setCurrentItem(current);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            current = position;
            initPlayerView();
            playerSet();
            if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
                playerStart();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      
            int runFlag;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("PlayerActivity", "onStartTrackingTouch()");
                // 멈추고
                seekBarFlag = true;
                runFlag = Player.getInstance().getStatus();
                if(runFlag == Const.STAT_PLAY){
                    
                    playerPause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("PlayerActivity", "onStopTrackingTouch()");
                // 다시 시작하고
                textCurrentTime.setText(milliToSec(seekBar.getProgress()));
                if(runFlag == Const.STAT_STOP){
                    // 종료 상태일 때
                    playerSet();
                    playerChange(seekBar.getProgress());
                }else if(runFlag == Const.STAT_PLAY){
                    // 시작 일 때
                    playerChange(seekBar.getProgress());
                    playerStart();
                }else{
                    // 정지 상태일 때
                    playerChange(seekBar.getProgress());
                }
            }
        });
    }
    /**
     * Player 에 관련된 View Setting
     */
    private void initPlayerView() {
        Music.Item item = music.getItemList().get(current);
        seekBar.setMax(item.duration);
        textDuration.setText(milliToSec(item.duration));

        if(Player.getInstance().getStatus() == Const.STAT_PAUSE){
            seekBar.setProgress(Player.getInstance().getCurrentPosition());
            textCurrentTime.setText(milliToSec(Player.getInstance().getCurrentPosition()));
        }
        // Title, Artist 세팅
        textTitle.setText(item.title);
        textArtist.setText(item.artist);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPlay:
                if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
                    playerPause();
                } else if (Player.getInstance().getStatus() == Const.STAT_PAUSE) {
                    playerStart();
                } else {
                    if(!seekBarFlag){
                        playerSet();
                    }
                    playerStart();
                }
                break;
            case R.id.imgFf:
                break;
            case R.id.imgRew:
                break;
            case R.id.imgPrev:
                if(viewPager.getCurrentItem()-1 != 0) {
                    current = viewPager.getCurrentItem() - 1;
                    playerSet();
                    if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
                        playerStart();
                    }
                }
                //viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                break;
            case R.id.imgNext:
                if(viewPager.getCurrentItem()+1 < Music.getInstance().getItemList().size()) {
                    current = viewPager.getCurrentItem() + 1;
                    playerSet();
                    if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
                        playerStart();
                    }
                }
                //viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    /**
     * Appbar 메뉴 선택 이벤트
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Player.getInstance().addListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        playerCheck(Player.getInstance().getCurrent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Player.getInstance().removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void playerSet() {
        serviceIntent.setAction(Const.ACTION_SET);
        serviceIntent.putExtra(Const.KEY_POSITION, current);
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

    private void playerChange(int progress) {
        serviceIntent.setAction(Const.ACTION_CHANGE);
        serviceIntent.putExtra(Const.KEY_POSITION, progress);
        startService(serviceIntent);
    }

    private void togglePlayButton(int status) {
        if (status == Const.STAT_PLAY) {
            imgPlay.setBackgroundResource(R.drawable.ic_stop);
        } else if (status == Const.STAT_PAUSE) {
            imgPlay.setBackgroundResource(R.drawable.ic_play_arrow);
        } else {
            imgPlay.setBackgroundResource(R.drawable.ic_play_arrow);
        }
    }

    @Override
    public void setProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 화면 세팅
                seekBar.setProgress(Player.getInstance().getCurrentPosition());
                textCurrentTime.setText(milliToSec(Player.getInstance().getCurrentPosition()));
            }
        });
    }

    @Override
    public void setMusic(int current) {
        playerCheck(current);
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
        seekBar.setProgress(0);
        textCurrentTime.setText("00:00");
        togglePlayButton(Const.STAT_STOP);
    }

    /**
     * 1110101 -> 04:00 으로 변환하는 메소드
     *
     * @param milli
     * @return
     */
    private String milliToSec(int milli) {
        int sec = milli / 1000;
        int min = sec / 60;
        sec = sec % 60;

        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }
}

