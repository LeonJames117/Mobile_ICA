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
            if (GV_Turn_Handler.Waiting_For_Dice && Change_in_Sense >= 2 && Checking_For_Dice)
            {
                Log.d("GameView", "Change in Sense: " + Change_in_Sense);
                Display_Dice = true;
                Display_Dice_Success = false;
                Display_Dice_Fail = false;
                Dice_Display_Finished = false;
                Checking_For_Dice = false;
                Display_Dice_Prompt = false;
                Log.d("GameView", "Dice Rolled ");

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };



    void Clear_Text()
    {
        Display_Tile_Out_Of_Range_Text = false;
        Display_Tile_Out_Of_Range_Count = 0;
        Display_No_Move_Enemy_Text = false;
        Display_No_Move_Enemy_Text_Count = 0;
        GV_Player.Display_Enemy_Not_In_Range_Text = false;
        Enemy_Not_In_Range_Count = 0;
        Display_Dice_Success = false;
        Display_Dice_Success_Count=0;
        Display_Dice_Fail = false;
        Display_Dice_Fail_Count=0;
    }

    //Dice
        Boolean Display_Dice = false;
        Boolean Dice_Display_Finished = false;
        boolean Display_Dice_Success = false;
        int Display_Dice_Success_Count = 0;
        boolean Display_Dice_Fail = false;
        int Display_Dice_Fail_Count = 0;
        int Final_Dice_Count = 0;
        boolean Checking_For_Dice = false;
        int Frames_Displayed = 0;
        int Number_To_Draw = 1;
        Drawable Dice_1;
        Drawable Dice_2;
        Drawable Dice_3;
        Drawable Dice_4;
        Drawable Dice_5;
        Drawable Dice_6;
        int Latest_Roll;
        String Latest_Ability;
        public void Start_Dice_Roll(String Ability_Used)
        {
            GV_Turn_Handler.Waiting_For_Dice = true;
            Display_Dice_Prompt = true;
            Checking_For_Dice = true;
            GV_Turn_Handler.Display_Ability_Icons = false;
            double Roll = Math.random()*(6-1+1)+1;
            Latest_Roll = (int)Roll;
            Latest_Ability = Ability_Used;
            Log.d("GameView", "Dice_Roll: " + (int)Roll);

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
    Vector<Tile> Grid = new Vector<>(Grid_Rows*Grid_Columns);
    boolean Grid_Setup = false;

    //Entities
    Player GV_Player = new Player(this, Grid_Helper);
    Enemy GV_Enemy = new Enemy(Grid,Grid_Helper);


    //Turn Variables
    Turn_Handler GV_Turn_Handler = new Turn_Handler(GV_Player,GV_Enemy);
    Drawable End_Turn_Drawable;
    Drawable Skip_Movement_Drawable;
    Boolean Game_End = false;
    Boolean Player_Win = false;
    Boolean Enemy_Win = false;

    //Text Variables
    boolean Display_No_Move_Enemy_Text = false;
    int Display_No_Move_Enemy_Text_Count = 0;
    boolean Display_Dice_Prompt = false;
    boolean Display_Tile_Out_Of_Range_Text = false;
    int Display_Tile_Out_Of_Range_Count = 0;
    int Enemy_Not_In_Range_Count = 0;


    //Sprite Variables
    Bitmap Player_Bitmap;
        //Player
            int Player_Frame_W = 350;
            int PLayer_Frame_H = 175;
            int Player_Frame_Count = 9;
            private final Rect Player_Frame_To_Draw = new Rect(0,0,Player_Frame_W,PLayer_Frame_H);
            private final RectF Player_DrawLocation = new RectF(GV_Player.XPos,GV_Player.YPos,GV_Player.XPos+Player_Frame_W,PLayer_Frame_H);
        //Enemy
            Drawable Enemy_Face_Left_Drawable;
            Drawable Enemy_Face_Right_Drawable;
        //Icons
            Drawable Sword_Drawable;
            Drawable Heal_Drawable;
    public GameView(Context context) {
        super(context);
        GV_SurfaceHolder = getHolder();
        Resources Res = context.getResources();

        SensorManager GV_Sense_Manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        //Setup Accelerometer
        if(GV_Sense_Manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0)
        {
            Sensor Accel = GV_Sense_Manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            if(!GV_Sense_Manager.registerListener(GV_Sense_Listener,Accel,SensorManager.SENSOR_DELAY_GAME))
            {
                Log.d("Sensor", "Listener not Registered");
            }
        }

        //Setup Sprites
        Player_Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.player_idle);
        Player_Bitmap = Bitmap.createScaledBitmap(Player_Bitmap,Player_Frame_W * Player_Frame_Count,PLayer_Frame_H,false);

        Background = ResourcesCompat.getDrawable(Res,R.drawable.background,null);

        Rect Dice_Draw = new Rect(850,25,1100,200);
        Dice_1 = ResourcesCompat.getDrawable(Res,R.drawable.dice_1,null);
        assert Dice_1 != null;
        Dice_1.setBounds(Dice_Draw);

        Dice_2 = ResourcesCompat.getDrawable(Res,R.drawable.dice_2,null);
        assert Dice_2 != null;
        Dice_2.setBounds(Dice_Draw);
        Dice_3 = ResourcesCompat.getDrawable(Res,R.drawable.dice_3,null);
        assert Dice_3 != null;
        Dice_3.setBounds(Dice_Draw);

        Dice_4 = ResourcesCompat.getDrawable(Res,R.drawable.dice_4,null);
        assert Dice_4 != null;
        Dice_4.setBounds(Dice_Draw);

        Dice_5 = ResourcesCompat.getDrawable(Res,R.drawable.dice_5,null);
        assert Dice_5 != null;
        Dice_5.setBounds(Dice_Draw);

        Dice_6 = ResourcesCompat.getDrawable(Res,R.drawable.dice_6,null);
        assert Dice_6 != null;
        Dice_6.setBounds(Dice_Draw);

        End_Turn_Drawable = ResourcesCompat.getDrawable(Res,R.drawable.end_turn_button,null);
        assert End_Turn_Drawable != null;
        End_Turn_Drawable.setBounds(1500,725,1500+400,725+200);

        Skip_Movement_Drawable = ResourcesCompat.getDrawable(Res,R.drawable.skip_movement_button,null);
        assert Skip_Movement_Drawable != null;
        Skip_Movement_Drawable.setBounds(1500,725,1500+400,725+200);

        Enemy_Face_Left_Drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.skeleton_face_left,null);
        Enemy_Face_Right_Drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.skeleton,null);
        Enemy_Face_Left_Drawable.setBounds(GV_Enemy.XPos,GV_Enemy.YPos,GV_Enemy.XPos+150,GV_Enemy.YPos+150);
        Enemy_Face_Right_Drawable.setBounds(GV_Enemy.XPos,GV_Enemy.YPos,GV_Enemy.XPos+150,GV_Enemy.YPos+150);

        Sword_Drawable = ResourcesCompat.getDrawable(Res,R.drawable.sword_icon,null);
        assert Sword_Drawable != null;
        Sword_Drawable.setBounds(600,730,820,725+225);

        Heal_Drawable = ResourcesCompat.getDrawable(Res,R.drawable.heal_icon,null);
        assert Heal_Drawable != null;
        Heal_Drawable.setBounds(840,740,1020,725+200);
    }

    //Setup Grid and Player/Enemy Spawn Points
    public void Setup_Game(int Screen_H, int Screen_W)
    {
        if(!Grid_Setup)
        {
            Tile_Width = Screen_W/Grid_Columns;
            Tile_Height = Screen_H/Grid_Rows;
            for (int r = 1; r < Grid_Rows+1;r++)
            {
                    for (int c = 0; c < Grid_Columns + 1; c++) {
                        Tile T = new Tile();
                        T.Tile_Rect = new Rect(c * Tile_Width, r * Tile_Height, Tile_Width, Tile_Height);
                        T.T_Row = r;
                        T.T_Column = c;
                        T.T_XPos = c * Tile_Width;
                        T.T_YPos = r * Tile_Height;
                        Grid.add(T);
                        Log.d("GameView", "Tile Added to grid");
                    }

            }
            Tile Enemy_Spawn = Grid_Helper.Find_Tile(Grid,Grid_Columns-1,Grid_Rows/2);
            GV_Enemy.Set_Location(Enemy_Spawn.T_XPos,Enemy_Spawn.T_YPos,Grid_Columns-1,Grid_Rows/2);
            GV_Player.Move_Player(Grid_Helper.Find_Tile(Grid,0,1).T_XPos,Grid_Helper.Find_Tile(Grid,0,1).T_YPos,1,0);
        }
    }

    @Override
    public void run() {
     // Game loop
        while(Playing)
        {
            // Check for end game conditions
            if(GV_Enemy.Health <= 0)
            {
                Game_End = true;
                Player_Win = true;
            }
            else if(GV_Player.Health <= 0)
            {
                Game_End = true;
                Enemy_Win = true;
            }
            if (!Game_End)
            {
                update();
            }
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




    public void draw()
    {
        if(GV_SurfaceHolder.getSurface().isValid())
        {
            GV_Canvas = GV_SurfaceHolder.lockCanvas();
            // Check for game end
            if (Game_End)
            {
                Paint P = new Paint();
                P.setTextSize(75);
                P.setStrokeWidth(5);

                P.setColor(Color.BLACK);
                P.setStyle(Paint.Style.FILL);
                if(Player_Win)
                {
                    GV_Canvas.drawColor(Color.WHITE);
                    GV_Canvas.drawText("Congratulations You Win!",Screen_Width/3f,Screen_Height/2f,P);
                    GV_Canvas.drawText("Press back to return to Main Menu",Screen_Width/5.5f,Screen_Height/1.5f,P);
                }
                else if (Enemy_Win) {
                    GV_Canvas.drawColor(Color.WHITE);
                    GV_Canvas.drawText("Sorry, you lost :(", Screen_Width / 3f, Screen_Height / 2f, P);
                    GV_Canvas.drawText("Press back to return to Main Menu",Screen_Width/5.5f,Screen_Height/1.5f,P);
                }

            }
            // If game not ended draw everything else
            else
            {
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
                //manageCurrentFrame();
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

                if (GV_Turn_Handler.Display_Skip_Movement)
                {
                    Skip_Movement_Drawable.draw(GV_Canvas);
                }

                P.setColor(Color.BLACK);
                P.setTextSize(70);
                P.setStyle(Paint.Style.FILL);

                GV_Canvas.drawText("Player Health: " + GV_Player.Health, 20,100,P);
                GV_Canvas.drawText("Enemy Health: " + GV_Enemy.Health, Screen_Width-600,100,P);

                if (Display_Dice_Prompt) {
                    Clear_Text();
                    GV_Canvas.drawText("Please Roll Dice", Screen_Width / 3f, 150, P);
                }

                else if(Display_Dice_Success)
                {
                    if (Display_Dice_Success_Count < 50)
                    {
                        GV_Canvas.drawText("Dice Roll " + Latest_Roll + ", Success!", Screen_Width/3f,150, P);
                        //Log.d("GameView", "Text Printed");
                        Display_Dice_Success_Count ++;
                    }
                    else
                    {
                        Display_Dice_Success = false;
                        Display_Dice_Success_Count = 0;
                    }
                }
                else if(Display_Dice_Fail)
                {
                    if (Display_Dice_Fail_Count < 50)
                    {
                        GV_Canvas.drawText("Dice Roll " + Latest_Roll + ", Fail :(", Screen_Width/3f,150, P);
                        //Log.d("GameView", "Text Printed");
                        Display_Dice_Fail_Count ++;
                    }
                    else
                    {
                        Display_Dice_Fail = false;
                        Display_Dice_Fail_Count = 0;
                    }
                }
                else if (GV_Player.Display_Enemy_Not_In_Range_Text)
                {

                    if (Enemy_Not_In_Range_Count < 50)
                    {
                        GV_Canvas.drawText("Enemy is not in range", Screen_Width/3f,150, P);
                        //Log.d("GameView", "Text Printed");
                        Enemy_Not_In_Range_Count ++;
                    }
                    else
                    {
                        GV_Player.Display_Enemy_Not_In_Range_Text = false;
                        Enemy_Not_In_Range_Count = 0;
                    }
                }

                else if (Display_No_Move_Enemy_Text)
                {

                    if (Display_No_Move_Enemy_Text_Count < 50)
                    {
                        GV_Canvas.drawText("You Cannot Move onto an enemy", Screen_Width/4f,150, P);
                        //Log.d("GameView", "Text Printed");
                        Display_No_Move_Enemy_Text_Count ++;
                    }
                    else
                    {
                        Display_No_Move_Enemy_Text = false;
                        Display_No_Move_Enemy_Text_Count = 0;
                    }

                }
                else if (Display_Tile_Out_Of_Range_Text)
                {

                    if (Display_Tile_Out_Of_Range_Count < 50) {
                        GV_Canvas.drawText("Tile Out Of Range", Screen_Width / 3f, 150, P);
                        //Log.d("GameView", "Text Printed");
                        Display_Tile_Out_Of_Range_Count++;
                    } else {
                        Display_Tile_Out_Of_Range_Text = false;
                        Display_Tile_Out_Of_Range_Count = 0;
                    }
                }



                if (GV_Turn_Handler.Display_Ability_Icons)
                {
                    Sword_Drawable.draw(GV_Canvas);
                    Heal_Drawable.draw(GV_Canvas);
                }

                if(Display_Dice)
                {
                    if (Frames_Displayed <= 3)
                    {
                        switch (Number_To_Draw)
                        {
                            case 1:
                                Dice_1.draw(GV_Canvas);
                                Frames_Displayed++;
                                Log.d("GameView", "Number to Display: " + Number_To_Draw);
                                Log.d("GameView", "Dice 1 Displayed");
                                break;
                            case 2:
                                Dice_2.draw(GV_Canvas);
                                Frames_Displayed++;
                                Log.d("GameView", "Number to Display: " + Number_To_Draw);
                                Log.d("GameView", "Dice 2 Displayed");
                                break;
                            case 3:
                                Dice_3.draw(GV_Canvas);
                                Frames_Displayed++;
                                Log.d("GameView", "Number to Display: " + Number_To_Draw);
                                Log.d("GameView", "Dice 3 Displayed");
                                break;
                            case 4:
                                Dice_4.draw(GV_Canvas);
                                Frames_Displayed++;
                                Log.d("GameView", "Number to Display: " + Number_To_Draw);
                                Log.d("GameView", "Dice 4 Displayed");
                                break;
                            case 5:
                                Dice_5.draw(GV_Canvas);
                                Frames_Displayed++;
                                Log.d("GameView", "Number to Display: " + Number_To_Draw);
                                Log.d("GameView", "Dice 5 Displayed");
                                break;
                            case 6:
                                Dice_6.draw(GV_Canvas);
                                Frames_Displayed++;
                                Log.d("GameView", "Number to Display: " + Number_To_Draw);
                                Log.d("GameView", "Dice 6 Displayed");
                                break;
                        }

                        if (Frames_Displayed >= 3 )
                        {
                            Frames_Displayed = 0;
                            Log.d("GameView", "Frames Displayed: " + Frames_Displayed);
                            Number_To_Draw++;
                            Log.d("GameView", "Number to Draw " + Number_To_Draw);
                        }
                        if(Number_To_Draw == 7)
                        {

                            switch (Latest_Roll){
                                case 1:
                                    Dice_1.draw(GV_Canvas);
                                    break;
                                case 2:
                                    Dice_2.draw(GV_Canvas);
                                    break;
                                case 3:
                                    Dice_3.draw(GV_Canvas);
                                    break;
                                case 4:
                                    Dice_4.draw(GV_Canvas);
                                    break;
                                case 5:
                                    Dice_5.draw(GV_Canvas);
                                    break;
                                case 6:
                                    Dice_6.draw(GV_Canvas);
                                    break;

                            }
                            Final_Dice_Count++;
                            Log.d("GameView", "Final Dice Count: " + Final_Dice_Count);
                            if(Final_Dice_Count > 15)
                            {
                                Display_Dice = false;
                                Dice_Display_Finished = true;
                                GV_Turn_Handler.Waiting_For_Dice = false;
                                Number_To_Draw = 1;
                                Final_Dice_Count = 0;
                                if (Latest_Ability.equals("Slash") )
                                {
                                    GV_Player.Slash_Resolution(Latest_Roll,GV_Enemy);
                                }
                                else if(Latest_Ability.equals("Heal"))
                                {
                                    GV_Player.Heal_Resolution(Latest_Roll);
                                }
                                Log.d("GameView", "Dice Finished");
                            }
                        }
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
    // Touch event
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                int Touch_X = (int) event.getX();
                int Touch_Y = (int) event.getY();


            //A button that the player can press to end their turn
            if(GV_Turn_Handler.Display_End_Turn && End_Turn_Drawable.getBounds().contains(Touch_X,Touch_Y))
            {
                Clear_Text();
                GV_Turn_Handler.End_Player_Turn();
                return true;
            }
            // A button that allows the player to skip the movement phase of their turn
            if(GV_Turn_Handler.Display_Skip_Movement && Skip_Movement_Drawable.getBounds().contains(Touch_X,Touch_Y))
            {
                GV_Turn_Handler.Player_Move_Complete = true;
                GV_Turn_Handler.Player_Move_Allowed = false;
                GV_Turn_Handler.Display_Skip_Movement = false;
                return true;
            }
            //Sword Ability Icon
            if(GV_Turn_Handler.Display_Ability_Icons && Sword_Drawable.getBounds().contains(Touch_X,Touch_Y))
            {
                  GV_Player.Slash_Ability();
                  GV_Turn_Handler.Player_Ability_Used = true;
                  return true;
            }
            //Heal Ability Icon
            if(GV_Turn_Handler.Display_Ability_Icons && Heal_Drawable.getBounds().contains(Touch_X,Touch_Y))
            {
                GV_Player.Heal_Ability();
                GV_Turn_Handler.Player_Ability_Used = true;
                return true;
            }
            //Handles Movement input for the player
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
                else if(!Grid_Helper.Tile_In_Range(Touch_Row,Touch_Column,GV_Player.Current_Column,GV_Player.Current_Row,GV_Player.Move_Range))
                {
                    if(Touch_Row != 0)
                    {
                        Log.d("GameView", "Tile out of Range");
                        Display_Tile_Out_Of_Range_Text = true;

                        return true;
                    }

                }

                for (Tile t : Grid) {
                    if (t.T_Row == Touch_Row && t.T_Column == Touch_Column)
                    {
                        GV_Player.Move_Player(t.T_XPos,t.T_YPos,Touch_Row,Touch_Column);
                        GV_Turn_Handler.Player_Move_Complete = true;
                        GV_Turn_Handler.Display_Skip_Movement = false;
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
