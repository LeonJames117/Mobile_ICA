package com.A0053718.mobile_ica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    //Setup Variables
    SurfaceHolder GV_SurfaceHolder;
    Thread GameThread;
    volatile boolean Playing = true;
    Canvas GV_Canvas;
    //Running Guy Variables
    boolean Is_Moving = true;
    Bitmap Running_Bitmap;
    int Frame_W = 115;
    int Frame_H = 137;
    int Frame_Count = 8;
    int RunGuy_XPos = 10;
    int RunGuy_YPos = 10;
    int Run_Speed = 200;
    int fps = 60;
    int CurrentFrame = 0;
    private Rect Frame_To_Draw = new Rect(0,0,Frame_W,Frame_H);
    private RectF DrawLocation = new RectF(RunGuy_XPos,RunGuy_YPos,RunGuy_XPos+Frame_W,Frame_H);
    float lastFrameChangeTime;
    float frameLength_MS;


    public GameView(Context context) {
        super(context);
        GV_SurfaceHolder = getHolder();
        Running_Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.running_guy);
        Running_Bitmap = Bitmap.createScaledBitmap(Running_Bitmap,Frame_W * Frame_Count,Frame_H,false);
    }

    @Override
    public void run() {
        while(Playing)
        {
            update();
            draw();
        }

    }
    public void Pause(){
        Playing=false;
        try{
            GameThread.join();
        } catch (InterruptedException e) {
            Log.e("GameView","Interrupted");
        }

    }
    public void Resume(){
    Playing=true;
    GameThread = new Thread(this);
    GameThread.start();
    }

    public void manageCurrentFrame()
    {
        long time = System.currentTimeMillis();
        if(Is_Moving)
        {
            if(time > lastFrameChangeTime+frameLength_MS)
            {
                lastFrameChangeTime = time;
                CurrentFrame++;
                if (CurrentFrame>=Frame_Count)
                {
                    //Reset frames to start
                    CurrentFrame = 0;
                }
            }
        }
        Frame_To_Draw.left = CurrentFrame * Frame_W;
        Frame_To_Draw.right = Frame_To_Draw.left+Frame_W;
    }

    public void draw()
    {
        if(GV_SurfaceHolder.getSurface().isValid())
        {
            GV_Canvas = GV_SurfaceHolder.lockCanvas();
            GV_Canvas.drawColor(Color.WHITE);
            DrawLocation.set(RunGuy_XPos,RunGuy_YPos,RunGuy_XPos+Frame_W,RunGuy_YPos+Frame_H);
            manageCurrentFrame();
            GV_Canvas.drawBitmap(Running_Bitmap,Frame_To_Draw,DrawLocation,null);
            GV_SurfaceHolder.unlockCanvasAndPost(GV_Canvas);
        }


    }


    private void update(){
        if(Is_Moving)
        {
            RunGuy_XPos = RunGuy_XPos + Run_Speed / fps;
            if(RunGuy_XPos > getWidth())
            {
                RunGuy_YPos += Frame_H;
                RunGuy_XPos = 10;
            }
            if (RunGuy_YPos+Frame_H > getHeight())
            {
                RunGuy_YPos = 10;
            }

        }
    }

}
