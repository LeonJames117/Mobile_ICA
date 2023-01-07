package com.A0053718.mobile_ica;

import android.util.Log;

public class Player {

    //Variables
    //Helpers/Access
        GameView Player_GV;
        Grid_Utility Player_Grid_Help;
    //Movement
        int XPos;
        int YPos;

        int Current_Row = 0;
        int Current_Column = 0;
        int Move_Range = 2;
    //Combat
        int Health = 100;
        int Slash_Range = 1;
        int Slash_Success_Role = 3;
        boolean Display_Enemy_Not_In_Range_Text;

    //Constructor
    Player (GameView PGV,Grid_Utility PGU)
    {
        Player_GV = PGV;
        Player_Grid_Help = PGU;

    }

    void Setup_Player()
    {

    }
    //Moves player sprite to a given location on the screen and updates grid position
    public void Move_Player (int New_X, int New_Y, int New_Row, int New_Column)
    {
        XPos = New_X;
        YPos = New_Y;
        Current_Row = New_Row;
        Current_Column = New_Column;
    }

    //Player ability that can be used to damage the enemy on a successful dice roll
    public void Slash_Ability()
    {
        if(Player_Grid_Help.Tile_In_Range(Player_GV.GV_Enemy.Current_Row,Player_GV.GV_Enemy.Current_Column,Current_Column,Current_Row,Slash_Range))
        {
            Log.d("GameView", "Enemy in Range");
            Player_GV.Start_Dice_Roll("Slash");
        }
        else
        {
            Display_Enemy_Not_In_Range_Text=true;
        }

    }

    public void Slash_Resolution(int Dice_Roll, Enemy Enemy)
    {
        if(Dice_Roll >= Slash_Success_Role)
        {
            Player_GV.Display_Dice_Success = true;
            Enemy.Health = Enemy.Health -10;

        }
        else
        {
            Player_GV.Display_Dice_Fail = true;
        }
    }

}
