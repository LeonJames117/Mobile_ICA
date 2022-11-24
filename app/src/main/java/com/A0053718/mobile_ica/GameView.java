package com.A0053718.mobile_ica;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewDebug;

import androidx.core.content.res.ResourcesCompat;

import java.sql.Struct;
import java.util.Vector;

public class GameView extends SurfaceView implements Runnable  {

    //Setup Variables
    SurfaceHolder GV_SurfaceHolder;
    Thread GameThread;
    volatile boolean Playing = true;
    Canvas GV_Canvas;
    int Screen_Height = 0;
    int Screen_Width = 0;

    //Debugging Variables
    Rect Touch_Rect = new Rect(0,500,50,50);
    //Entities
    Player GV_Player = new Player();
    Enemy GV_Enemy = new Enemy();

    //Turn Variables
    Turn_Handler GV_Turn_Handler = new Turn_Handler(GV_Player,GV_Enemy);
    Drawable End_Turn_Drawable;

    //Grid Variables
    int Grid_Rows = 5;
    int Grid_Columns = 10;
    class Tile{
       public Rect Tile_Rect;
       public int T_XPos=0;
       public int T_YPos=0;
       public int T_Column=0;
       public int T_Row=0;
    }
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
    int CurrentFrame = 0;
    private Rect Frame_To_Draw = new Rect(0,0,Frame_W,Frame_H);
    private RectF DrawLocation = new RectF(RunGuy_XPos,RunGuy_YPos,RunGuy_XPos+Frame_W,Frame_H);
    float lastFrameChangeTime = 0;
    float frameLength_MS = 3;



    public GameView(Context context) {
        super(context);
        GV_SurfaceHolder = getHolder();
        Resources Res = context.getResources();
        Running_Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.run_guy);
        End_Turn_Drawable = ResourcesCompat.getDrawable(Res,R.drawable.end_turn_button,null);
        End_Turn_Drawable.setBounds(1500,725,1500+400,725+200);
        Running_Bitmap = Bitmap.createScaledBitmap(Running_Bitmap,Frame_W * Frame_Count,Frame_H,false);

    }

    public void Setup_Game(int Screen_H, int Screen_W)
    {
        if(!Grid_Setup)
        {

            Tile_Width = Screen_W/Grid_Columns;
            Tile_Height = Screen_H/Grid_Rows;
            for (int r = 0; r < Grid_Rows+1;r++)
            {
                for (int c = 0; c < Grid_Columns+1;c++)
                {
                    Tile T = new Tile ();
                    T.Tile_Rect = new Rect (c*Tile_Width,r*Tile_Height,Tile_Width,Tile_Height);
                    T.T_Row = r;
                    T.T_Column = c;
                    T.T_XPos = c*Tile_Width;
                    T.T_YPos = r*Tile_Height;
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
            if (!Grid_Setup)
            {
                Screen_Height = getHeight();
                Screen_Width =  getWidth();
                Setup_Game(getHeight(),getWidth());
                Grid_Setup=true;
            }
            Setup_Game(getHeight(),getWidth());
            GV_Canvas.drawColor(Color.WHITE);
            DrawLocation.set(GV_Player.XPos,GV_Player.YPos,GV_Player.XPos+Frame_W,GV_Player.YPos+Frame_H);
            manageCurrentFrame();
            GV_Canvas.drawBitmap(Running_Bitmap,Frame_To_Draw,DrawLocation,null);
            Paint P = new Paint();

            P.setStrokeWidth(5);

            P.setColor(Color.RED);
            P.setStyle(Paint.Style.STROKE);

            for (Tile T : Grid)
            {
                GV_Canvas.drawRect(T.Tile_Rect,P);
            }

            if (GV_Turn_Handler.Display_End_Turn)
            {
                P.setStyle(Paint.Style.FILL);
                P.setColor(Color.BLACK);
                End_Turn_Drawable.draw(GV_Canvas);
            }

            GV_SurfaceHolder.unlockCanvasAndPost(GV_Canvas);
        }
    }


    private void update()
    {
      GV_Turn_Handler.Turn_Update();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                int Touch_X = (int) event.getX();
                int Touch_Y = (int) event.getY();

            if(GV_Turn_Handler.Display_End_Turn && End_Turn_Drawable.getBounds().contains(Touch_X,Touch_Y))
            {
                GV_Turn_Handler.End_Player_Turn();
                return true;
            }

            if (GV_Turn_Handler.Player_Move_Allowed)
            {
                int Touch_Column = Touch_X / Tile_Width;
                int Touch_Row = Touch_Y / Tile_Height;

                if(GV_Player.Tile_In_Range(Touch_Row,Touch_Column))
                {
                    Log.d("GameView", "Player Column: " + GV_Player.Current_Column);
                    Log.d("GameView", "Player Row: " + GV_Player.Current_Row);
                    Log.d("GameView", "Touch Column: " + Touch_Column);
                    Log.d("GameView", "Touch Row: " + Touch_Row);
                    Log.d("GameView", "Tile out of Range");
                    return true;
                }

                for (Tile t : Grid) {
                    if (t.T_Row == Touch_Row && t.T_Column == Touch_Column)
                    {
                        GV_Player.Move_Player(t.T_XPos,t.T_YPos,Touch_Row,Touch_Column);
                        GV_Turn_Handler.Player_Move_Complete = true;
                        GV_Turn_Handler.Player_Move_Allowed = false;
                        return true;
                    }
                }
            }
            else
            {
                Log.d("GameView", "Movement not allowed");
            }
        }
        return super.onTouchEvent(event);
    }
}
