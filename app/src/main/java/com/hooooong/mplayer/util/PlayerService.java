package com.hooooong.mplayer.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.hooooong.mplayer.R;
import com.hooooong.mplayer.data.Const;
import com.hooooong.mplayer.data.model.Music;
import com.hooooong.mplayer.view.main.MainActivity;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service implements Player.Listener {
    Player player = null;
    int current = -1;

    public PlayerService() {
    }

    // Context 를 구하려면 onCreate 에서 getBaseContext() 를 하는것이 좋다.
    // 생성자에서는 제거를 할 수 없기 떄문에
    @Override
    public void onCreate() {
        super.onCreate();
        player = Player.getInstance();
        player.addListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case Const.ACTION_INTENT:
                    startIntent();
                    break;
                case Const.ACTION_SET:
                    current = intent.getIntExtra(Const.KEY_POSITION, -1);
                    playerSet();
                    break;
                case Const.ACTION_START:
                    playerStart();
                    break;
                case Const.ACTION_PAUSE:
                    playerPause();
                    break;
                case Const.ACTION_STOP:
                    playerStop();
                    break;
                case Const.ACTION_PREV:
                    playerPrev();
                    break;
                case Const.ACTION_NEXT:
                    playerNext();
                    break;
                case Const.ACTION_CHANGE:
                    int progress = intent.getIntExtra(Const.KEY_POSITION, -1);
                    playerChange(progress);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startIntent() {
        ActivityManager mActivityManager = null;
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rt = mActivityManager.getRunningTasks(1);
        String runningTask = rt.get(0).topActivity.toString();
        runningTask = runningTask.substring(runningTask.lastIndexOf(".") + 1, runningTask.indexOf("}"));

        Intent intent;
        if ("LauncherActivity".equals(runningTask)) {
            intent = new Intent(getBaseContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void playerSet() {
        if (current > -1) {
            player.set(getBaseContext(), current);
            initNotification(current);
        }
    }

    private void playerStart() {
        player.start();
    }

    private void playerPause() {
        player.pause();
    }

    private void playerStop() {
        player.stop();
    }

    private void playerNext() {
        if (Music.getInstance().getItemList().size() > player.getCurrent() + 1) {
            player.set(getBaseContext(), player.getCurrent() + 1);
            if (player.getStatus() == Const.STAT_PLAY) {
                player.start();
            }
            initNotification(current);
        }
    }

    private void playerPrev() {
        if (player.getCurrent() - 1 >= 0) {
            player.set(getBaseContext(), player.getCurrent() - 1);
            if (player.getStatus() == Const.STAT_PLAY) {
                player.start();
            }
            initNotification(current);
        }
    }

    private void playerChange(int progress) {
        player.change(progress);
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.removeListener(this);
            player.stop();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void setProgress() {
        // current 값을 통해 가져온다.
    }

    @Override
    public void setMusic(int current) {
        this.current = current;
        initNotification(current);
    }

    @Override
    public void setPlay() {
        initNotification(current);
    }

    @Override
    public void setPause() {
        initNotification(current);
    }

    @Override
    public void setStop() {
        stopForeground(true);
        stopSelf();
    }

    private void initNotification(int current) {
        Music.Item item = Music.getInstance().getItemList().get(current);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)  // 최상단 스테이터스 바에 나타나는 아이콘
                .setContent(customNotiView(item))
                .build();


        // Notification Bar 를 Click 했을 때 처리
        Intent startIntent = new Intent(getBaseContext(), PlayerService.class);
        startIntent.setAction(Const.ACTION_INTENT); //  <- intent.getAction() 에서 처리해야 하는 값들
        PendingIntent mainIntent = PendingIntent.getService(getBaseContext(), 1, startIntent, 0);
        builder.setContentIntent(mainIntent);

        /*
        // Click 을 했을 경우 Notification 을 멈추는 명령을 서비스에서 다시 받아서 처리
        // 팬딩인텐트
        Intent pauseIntent = new Intent(getBaseContext(), MyService.class);
        pauseIntent.setAction(cmd); //  <- intent.getAction() 에서 처리해야 하는 값들
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, pauseIntent, 0);
        */

        Notification notification = builder.build();
        startForeground(Const.FLAG_NOTI, notification);
    }

    private RemoteViews customNotiView(Music.Item item) {
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), item.albumUri);
        } catch (IOException e) {
            e.printStackTrace();

        }
        if (Player.getInstance().getStatus() == Const.STAT_PAUSE) {
            contentView.setImageViewResource(R.id.imgPlay, R.drawable.ic_play_arrow_black);
        } else if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
            contentView.setImageViewResource(R.id.imgPlay, R.drawable.ic_stop_black);
        }
        contentView.setImageViewResource(R.id.imgPrev, R.drawable.ic_skip_previous_black);
        contentView.setImageViewResource(R.id.imgNext, R.drawable.ic_skip_next_black);
        contentView.setImageViewResource(R.id.imgStop, R.drawable.ic_close_black);

        contentView.setImageViewBitmap(R.id.imgAlbum, bitmap);
        contentView.setTextViewText(R.id.textTitle, item.title);
        contentView.setTextViewText(R.id.textArtist, item.artist);

        contentView.setOnClickPendingIntent(R.id.imgPrev, getPrevPendingIntent());
        contentView.setOnClickPendingIntent(R.id.imgPlay, getPlayPendingIntent());
        contentView.setOnClickPendingIntent(R.id.imgNext, getNextPendingIntent());
        contentView.setOnClickPendingIntent(R.id.imgStop, getStopPendingIntent());

        return contentView;
    }

    private PendingIntent getPlayPendingIntent() {
        Intent intent = new Intent(getBaseContext(), PlayerService.class);
        if (Player.getInstance().getStatus() == Const.STAT_PAUSE) {
            intent.setAction(Const.ACTION_START);
        } else if (Player.getInstance().getStatus() == Const.STAT_PLAY) {
            intent.setAction(Const.ACTION_PAUSE);
        }
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, intent, 0);
        return pendingIntent;
    }

    private PendingIntent getStopPendingIntent() {
        Intent intent = new Intent(getBaseContext(), PlayerService.class);
        intent.setAction(Const.ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, intent, 0);
        return pendingIntent;
    }

    private PendingIntent getPrevPendingIntent() {
        Intent intent = new Intent(getBaseContext(), PlayerService.class);
        intent.setAction(Const.ACTION_PREV);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, intent, 0);
        return pendingIntent;
    }

    private PendingIntent getNextPendingIntent() {
        Intent intent = new Intent(getBaseContext(), PlayerService.class);
        intent.setAction(Const.ACTION_NEXT);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, intent, 0);
        return pendingIntent;
    }

}
