package com.A0053718.mobile_ica;

public class Player {

    //Variables
    int XPos = 10;
    int YPos = 10;
    int Current_Row = 0;
    int Current_Column = 0;
    int Health = 100;
    int Move_Range = 3;
    int Slash_Range = 1;
    boolean Waiting_for_Dice;
    boolean Enemy_Not_In_Range_Text;
    GameView Player_GV;
    Player (GameView PGV,Grid_Utility PGU)
    {
        Player_GV = PGV;
        Player_Grid_Help = PGU;
    }
    Grid_Utility Player_Grid_Help;


    public void Move_Player (int New_X, int New_Y, int New_Row, int New_Column)
    {
        XPos = New_X;
        YPos = New_Y;
        Current_Row = New_Row;
        Current_Column = New_Column;
    }

    public void Slash_Ability()
    {
        if(Player_Grid_Help.Tile_In_Range(Player_GV.GV_Enemy.Current_Row,Player_GV.GV_Enemy.Current_Column,Current_Column,Current_Row,Slash_Range))
        {
            int Dice_Roll = Player_GV.Start_Dice_Roll();
        }
        else{
            Enemy_Not_In_Range_Text=true;
        }

    }



}
