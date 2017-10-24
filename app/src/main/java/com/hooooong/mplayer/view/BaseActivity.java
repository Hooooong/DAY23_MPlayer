package com.hooooong.mplayer.view;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQ_CODE = 999;
    private static String permissions[];

    public abstract void init();

    public BaseActivity(String permissions[]){
        this.permissions = permissions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 볼룜 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // 0. App 버전 체크
        // 마시멜로우는 version_code 가 이니셜로 되어 있어야 한다.
        // 마시멜로우 이후에만 Permission 정책이 바뀌었기 때문에
        // 현재 버전이 마시멜로우 이상이라면 Permission Check 를 하라고 알려주는 것이다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            init();
        }
    }

    // @RequiresApi 애너테이션으로 API 의 메서드에 최소 API 레벨을 나타내는 예다.
    // 컴파일러에게 현재 버전이 마시멜로우 이상일 때 메소드를 실행하는 것이라고 알려주는 Annotation
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        // 1. 권한 유무 확인
        // 호환성 처리를 수동으로 해줘야 한다.
        // checkSelfPermission(Permission String)
        // RETURN : Integer (PackageManager.PERMISSION_GRANTED , PackageManager.PERMISSION_DENIED)
        List<String> requires = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // READ_EXTERNAL_STORAGE 이 없다면
                requires.add(permission);
            }
        }

        if (requires.size() > 0) {
            // 승인이 되지 않은 권한이 있을 경우에는 권한 요청
            String perms[] = requires.toArray(new String[requires.size()]);
            requestPermissions(perms, REQ_CODE);
        } else {
            init();
        }

        /*
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            // 1-1. 이미 승인이 되어 있는 경우
            // 초기화 실행
            init();
        }else{
            // 1-2. 권한이 승인이 되지 않으면
            // 2. 권한 승인
            // 2-1. 요청할 권한을 정의
            // 2-2. 권한 요청
            requestPermissions(permissions, REQ_CODE);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQ_CODE:
                boolean flag = true;
                for(int grantResult : grantResults){
                    if(grantResult != PackageManager.PERMISSION_GRANTED){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    init();
                }else{
                    Toast.makeText(this, "권한 승인을 하지 않으면 APP 을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}
