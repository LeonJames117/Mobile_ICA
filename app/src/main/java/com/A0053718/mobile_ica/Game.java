package com.A0053718.mobile_ica;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

public class Game extends AppCompatActivity {

    GameView MA_GameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        MA_GameView = new GameView(this);
        MA_GameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(MA_GameView);

    }
    @Override
    protected void onPause() {
        super.onPause();
        MA_GameView.Pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MA_GameView.Resume();
    }
}