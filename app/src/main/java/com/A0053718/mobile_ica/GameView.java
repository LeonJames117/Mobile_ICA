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

import org.w3c.dom.Text;

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
    Grid_Utility Grid_Helper = new Grid_Utility();


    //Grid Variables
    int Grid_Rows = 5;
    int Grid_Columns = 10;
    public static class Tile{
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

    //Entities
    Player GV_Player = new Player();
    Enemy GV_Enemy = new Enemy(Grid,Grid_Helper);



    //Turn Variables
    Turn_Handler GV_Turn_Handler = new Turn_Handler(GV_Player,GV_Enemy);
    Drawable End_Turn_Drawable;



    //Sprite Variables
    boolean Is_Moving = true;
    Bitmap Player_Bitmap;
    Bitmap Enemy_Bitmap;
        //Player
            int Player_Frame_W = 350;
            int PLayer_Frame_H = 175;
            int Player_Frame_Count = 9;
            int CurrentFrame = 0;
            private Rect Player_Frame_To_Draw = new Rect(0,0,Player_Frame_W,PLayer_Frame_H);
            private RectF Player_DrawLocation = new RectF(GV_Player.XPos,GV_Player.YPos,GV_Player.XPos+Player_Frame_W,PLayer_Frame_H);
            float lastFrameChangeTime = 10;
            float frameLength_MS = 3;
        //Enemy
            Drawable Enemy_Face_Left_Drawable;
            Drawable Enemy_Face_Right_Drawable;

    public GameView(Context context) {
        super(context);
        GV_SurfaceHolder = getHolder();
        Resources Res = context.getResources();

        Player_Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.player_idle);
        Player_Bitmap = Bitmap.createScaledBitmap(Player_Bitmap,Player_Frame_W * Player_Frame_Count,PLayer_Frame_H,false);



        End_Turn_Drawable = ResourcesCompat.getDrawable(Res,R.drawable.end_turn_button,null);
        End_Turn_Drawable.setBounds(1500,725,1500+400,725+200);

        Enemy_Face_Left_Drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.skeleton_face_left,null);
        Enemy_Face_Right_Drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.skeleton,null);
        Enemy_Face_Left_Drawable.setBounds(GV_Enemy.XPos,GV_Enemy.YPos,GV_Enemy.XPos+150,GV_Enemy.YPos+150);
        Enemy_Face_Right_Drawable.setBounds(GV_Enemy.XPos,GV_Enemy.YPos,GV_Enemy.XPos+150,GV_Enemy.YPos+150);
        lastFrameChangeTime = 0;

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
            Tile Enemy_Spawn = Grid_Helper.Find_Tile(Grid,Grid_Columns-1,Grid_Rows/2);
            GV_Enemy.Set_Location(Enemy_Spawn.T_XPos,Enemy_Spawn.T_YPos,Grid_Columns-1,Grid_Rows/2);
        }
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
            Log.d("Sprite", "Time: "+ time);
            Log.d("Sprite", "Last Frame Change: "+ lastFrameChangeTime+frameLength_MS);
            if(time > lastFrameChangeTime+frameLength_MS)
            {
                Log.d("Sprite", "Frame Change");
                lastFrameChangeTime = time;
                CurrentFrame++;
                if (CurrentFrame>=Player_Frame_Count)
                {
                    //Reset frames to start
                    CurrentFrame = 0;
                }
            }
        }
        Log.d("Sprite", "Current Frame: " + CurrentFrame);
        Player_Frame_To_Draw.left = CurrentFrame * Player_Frame_W;
        Player_Frame_To_Draw.right = Player_Frame_To_Draw.left+Player_Frame_W;
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
            Player_DrawLocation.set(GV_Player.XPos,GV_Player.YPos,GV_Player.XPos+Player_Frame_W,GV_Player.YPos+PLayer_Frame_H);
            manageCurrentFrame();
            GV_Canvas.drawBitmap(Player_Bitmap,Player_Frame_To_Draw,Player_DrawLocation,null);

            if(GV_Enemy.Facing_Right)
            {
                Enemy_Face_Right_Drawable.setBounds(GV_Enemy.XPos,GV_Enemy.YPos,GV_Enemy.XPos+150,GV_Enemy.YPos+150);
                Enemy_Face_Right_Drawable.draw(GV_Canvas);
            }
            else
            {
                Enemy_Face_Left_Drawable.setBounds(GV_Enemy.XPos,GV_Enemy.YPos,GV_Enemy.XPos+150,GV_Enemy.YPos+150);
                Enemy_Face_Left_Drawable.draw(GV_Canvas);
            }

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
                End_Turn_Drawable.draw(GV_Canvas);
            }

            GV_SurfaceHolder.unlockCanvasAndPost(GV_Canvas);
        }
    }

    private void update() {
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
                if (Touch_Column == GV_Enemy.Current_Column && Touch_Row == GV_Enemy.Current_Row)
                {
                    Log.d("GameView", "You cannot move onto an enemy");
                    return true;
                }
                if(Grid_Helper.Tile_In_Range(Touch_Row,Touch_Column,GV_Player.Current_Column,GV_Player.Current_Row,GV_Player.Move_Range))
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
