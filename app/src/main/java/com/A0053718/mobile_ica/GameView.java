package com.A0053718.mobile_ica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewDebug;

import java.sql.Struct;
import java.util.Vector;

public class GameView extends SurfaceView implements Runnable {

    //Setup Variables
    SurfaceHolder GV_SurfaceHolder;
    Thread GameThread;
    volatile boolean Playing = true;
    Canvas GV_Canvas;

    //Grid Variables
    int Grid_Rows = 5;
    int Grid_Columns = 10;
    class Tile{
       public Rect Tile_Rect;
       public int Tile_XPos=0;
        public int Tile_YPos=0;
    }
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int Tile_Width;
    int Tile_Height;
    Vector<Tile> Grid = new Vector<Tile>(Grid_Rows*Grid_Columns);
    boolean Grid_Setup = false;

    //Running Guy Variables
    boolean Is_Moving = true;
    Bitmap Running_Bitmap;
    int Frame_W = 115;
    int Frame_H = 137;
    int Frame_Count = 8;
    int RunGuy_XPos = 10;
    int RunGuy_YPos = 10;
    int Run_Speed = 200;
    int fps = 25;
    int CurrentFrame = 0;
    private Rect Frame_To_Draw = new Rect(0,0,Frame_W,Frame_H);
    private RectF DrawLocation = new RectF(RunGuy_XPos,RunGuy_YPos,RunGuy_XPos+Frame_W,Frame_H);
    float lastFrameChangeTime = 0;
    float frameLength_MS = 3;


    public GameView(Context context) {
        super(context);
        GV_SurfaceHolder = getHolder();
        Running_Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.run_guy);
        Running_Bitmap = Bitmap.createScaledBitmap(Running_Bitmap,Frame_W * Frame_Count,Frame_H,false);
        //Setup_Game();
    }

    public void Setup_Game(int Screen_H, int Screen_W)
    {
        if(Grid_Setup == false)
        {
            Tile_Width = Screen_W/Grid_Columns;
            Tile_Height = Screen_H/Grid_Rows;
            for (int r = 0; r < Grid_Rows+1;r++)
            {
                for (int c = 0; c < Grid_Columns+1;c++)
                {
                    Tile T = new Tile ();
                    T.Tile_Rect = new Rect (c*Tile_Width,r*Tile_Height,Tile_Width,Tile_Height);
                    Grid.add(T);
                    Log.d("GameView", "Tile Added to grid");

                }
            }
        }
        //Grid_Setup=true;
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
            if (Grid_Setup == false)
            {
                Setup_Game(getHeight(),getWidth());
                Grid_Setup=true;
                Log.d("GameView", "Gird Setup = "+ Grid_Setup);
            }

            Setup_Game(getHeight(),getWidth());
            GV_Canvas.drawColor(Color.WHITE);
            DrawLocation.set(RunGuy_XPos,RunGuy_YPos,RunGuy_XPos+Frame_W,RunGuy_YPos+Frame_H);
            manageCurrentFrame();
            GV_Canvas.drawBitmap(Running_Bitmap,Frame_To_Draw,DrawLocation,null);
            Paint P = new Paint();
            P.setStyle(Paint.Style.STROKE);
            P.setColor(Color.RED);
            P.setStrokeWidth(5);
            for (Tile T : Grid)
            {
                GV_Canvas.drawRect(T.Tile_Rect,P);
            }
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
