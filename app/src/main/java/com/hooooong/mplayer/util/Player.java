package com.hooooong.mplayer.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.hooooong.mplayer.data.Const;
import com.hooooong.mplayer.data.model.Music;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by Android Hong on 2017-10-12.
 */

public class Player {

    private List<Listener> listenerList = new CopyOnWriteArrayList<>();
    private MediaPlayer mediaPlayer;
    private PlayerThread playerThread;
    private boolean loop = false;

    private int status = Const.STAT_STOP;
    private int current = -1;

    // Singleton
    private static Player player;
    private Context context;

    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    private Player() {
    }

    // 음원 세팅
    public void set(Context context, int current) {
        this.current = current;
        this.context = context;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(context, Music.getInstance().getItemList().get(current).musicUri);
        mediaPlayer.setLooping(loop);
        mediaPlayer.setOnCompletionListener(completionListener);

        for(Listener listener : listenerList){
            listener.setMusic(current);
        }
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // loop check;
            if(current < Music.getInstance().getItemList().size() ){
                current += 1;
                set(context, current);
                start();
            }
        }
    };

    // mediaPlayer 실행
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            if (playerThread != null) {
                playerThread.setStop();
                playerThread = null;
            }
            playerThread = new PlayerThread();
            playerThread.start();
            status = Const.STAT_PLAY;
        }

        for(Listener listener : listenerList){
            listener.setPlay();
        }
    }

    // mediaPlayer 일시정지
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            if (playerThread != null) {
                playerThread.setStop();
                playerThread = null;
            }
            status = Const.STAT_PAUSE;
        }
        for(Listener listener : listenerList){
            listener.setPause();
        }
    }

    // mediaPlayer 멈춤
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if (playerThread != null) {
                playerThread.setStop();
                playerThread = null;
            }
            status = Const.STAT_STOP;
        }

        for(Listener listener : listenerList){
            listener.setStop();
        }
    }

    public void addListener(Listener listener) {
        listenerList.add(listener);
    }

    public void removeListener(Listener listener) {
        listenerList.remove(listener);
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getCurrent() {
        return current;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void change(int progress) {
        if(mediaPlayer != null){
            mediaPlayer.seekTo(progress);
        }
    }

    public class PlayerThread extends Thread {
        boolean check = true;
            @Override
            public void run() {
                while (check) {
                    for (Listener listener : listenerList) {
                        listener.setProgress();
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        public void setStop(){
            check = false;
        }
    }

    public interface Listener {
        void setProgress();
        void setMusic(int current);
        void setPlay();
        void setPause();
        void setStop();
    }
}
