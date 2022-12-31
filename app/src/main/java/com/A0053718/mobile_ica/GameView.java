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
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

import java.util.Vector;

public class GameView extends SurfaceView implements Runnable  {

    //Setup Variables
    SurfaceHolder GV_SurfaceHolder;
    Thread GameThread;
    volatile boolean Playing = true;
    Canvas GV_Canvas;
    int Screen_Height = 0;
    int Screen_Width = 0;
    Drawable Background;

    //Sensor Variables
    float Sense_X;
    float Sense_Y;
    float Previous_Sense_X = 0;
    float Change_in_Sense=0;
    float Sense_Z;
    SensorEventListener GV_Sense_Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
            Sense_X = sensorEvent.values[0];
            Sense_Y = sensorEvent.values[1];
            Change_in_Sense = Sense_X - Previous_Sense_X;
            Sense_Z = sensorEvent.values[2];
            Previous_Sense_X = Sense_X;

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    //Dice
        Boolean Display_Dice = false;
        Boolean Dice_Display_Finished = false;
        int Frames_Displayed = 0;
        int Number_To_Draw = 1;
        Drawable Dice_1;
        Drawable Dice_2;
        Drawable Dice_3;
        Drawable Dice_4;
        Drawable Dice_5;
        Drawable Dice_6;
        public int Start_Dice_Roll()
        {
            GV_Turn_Handler.Waiting_For_Dice = true;
            while (GV_Turn_Handler.Waiting_For_Dice)
            {
                Display_Dice_Prompt = true;
                if (Change_in_Sense > 0.8)
                {
                    Display_Dice = true;
                    Dice_Display_Finished = false;
                    GV_Turn_Handler.Waiting_For_Dice = false;

                }
            }
            double Roll = Math.random()*(6-1+1)+1;
            return (int)Roll;

        }


    //Grid Variables
    Grid_Utility Grid_Helper = new Grid_Utility();
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
    Player GV_Player = new Player(this);
    Enemy GV_Enemy = new Enemy(Grid,Grid_Helper);



    //Turn Variables
    Turn_Handler GV_Turn_Handler = new Turn_Handler(GV_Player,GV_Enemy);
    Drawable End_Turn_Drawable;

    //Text Variables
    boolean Display_No_Move_Enemy_Text = false;
    int Display_No_Move_Enemy_Text_Count = 0;
    boolean Display_Dice_Prompt = false;
    boolean Display_Tile_Out_Of_Range_Text = false;
    int Display_Tile_Out_Of_Range_Count = 0;
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

        SensorManager GV_Sense_Manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if(GV_Sense_Manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0)
        {
            Sensor Accel = GV_Sense_Manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            if(!GV_Sense_Manager.registerListener(GV_Sense_Listener,Accel,SensorManager.SENSOR_DELAY_GAME))
            {
                Log.d("Sensor", "Listener not Registered");
            }
        }

        Player_Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.player_idle);
        Player_Bitmap = Bitmap.createScaledBitmap(Player_Bitmap,Player_Frame_W * Player_Frame_Count,PLayer_Frame_H,false);

        Background = ResourcesCompat.getDrawable(Res,R.drawable.background,null);
        Rect Dice_Draw = new Rect(Screen_Width/2,Screen_Height/2,1500+Screen_Width/2,725+Screen_Height/2);
        Dice_1 = ResourcesCompat.getDrawable(Res,R.drawable.dice_1,null);
        Dice_1.setBounds(Dice_Draw);
        Dice_2 = ResourcesCompat.getDrawable(Res,R.drawable.dice_2,null);
        Dice_2.setBounds(Dice_Draw);
        Dice_3 = ResourcesCompat.getDrawable(Res,R.drawable.dice_3,null);
        Dice_3.setBounds(Dice_Draw);
        Dice_4 = ResourcesCompat.getDrawable(Res,R.drawable.dice_4,null);
        Dice_4.setBounds(Dice_Draw);
        Dice_5 = ResourcesCompat.getDrawable(Res,R.drawable.dice_5,null);
        Dice_5.setBounds(Dice_Draw);
        Dice_6 = ResourcesCompat.getDrawable(Res,R.drawable.dice_6,null);
        Dice_6.setBounds(Dice_Draw);

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
            if(GV_Player.Waiting_for_Dice)
            {
                draw();
            }
            else
            {
                update();
                draw();
            }
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
                Background.setBounds(0,0,Screen_Width,Screen_Height);
            }
            Setup_Game(getHeight(),getWidth());
            GV_Canvas.drawColor(Color.WHITE);
            Background.draw(GV_Canvas);
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

            P.setColor(Color.BLUE);
            P.setStyle(Paint.Style.STROKE);
            for (Tile T : Grid)
            {
                GV_Canvas.drawRect(T.Tile_Rect,P);
            }

            if (GV_Turn_Handler.Display_End_Turn)
            {
                End_Turn_Drawable.draw(GV_Canvas);
            }

            P.setColor(Color.RED);
            P.setTextSize(70);
            P.setStyle(Paint.Style.FILL);
            if (Display_No_Move_Enemy_Text)
            {
                if (Display_No_Move_Enemy_Text_Count < 50)
                {
                    GV_Canvas.drawText("You Cannot Move onto an enemy", Screen_Width/4,150, P);
                    Log.d("GameView", "Text Printed");
                    Display_No_Move_Enemy_Text_Count ++;
                }
                else
                {
                    Display_No_Move_Enemy_Text = false;
                    Display_No_Move_Enemy_Text_Count = 0;
                }

            }
            if (Display_Tile_Out_Of_Range_Text) {
                if (Display_Tile_Out_Of_Range_Count < 50) {
                    GV_Canvas.drawText("Tile Out Of Range", Screen_Width / 3, 150, P);
                    Log.d("GameView", "Text Printed");
                    Display_Tile_Out_Of_Range_Count++;
                } else {
                    Display_Tile_Out_Of_Range_Text = false;
                    Display_Tile_Out_Of_Range_Count = 0;
                }
            }
            if(Display_Dice)
            {
                if (Frames_Displayed <= 10)
                {
                    switch (Number_To_Draw){
                        case 1:
                            Dice_1.draw(GV_Canvas);
                        case 2:
                            Dice_2.draw(GV_Canvas);
                        case 3:
                            Dice_3.draw(GV_Canvas);
                        case 4:
                            Dice_4.draw(GV_Canvas);
                        case 5:
                            Dice_5.draw(GV_Canvas);
                        case 6:
                            Dice_6.draw(GV_Canvas);
                    }
                    Frames_Displayed++;
                    if(Number_To_Draw == 6)
                    {
                        Dice_Display_Finished = true;
                    }
                    else if (Frames_Displayed == 11 )
                    {
                        Frames_Displayed = 0;
                        Number_To_Draw++;
                    }



                }

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
                    Display_No_Move_Enemy_Text = true;
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
                    Display_Tile_Out_Of_Range_Text = true;

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
