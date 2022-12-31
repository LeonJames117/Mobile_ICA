package com.A0053718.mobile_ica;

public class Player {

    //Variables
    int XPos = 10;
    int YPos = 10;
    int Current_Row = 0;
    int Current_Column = 0;
    int Health = 100;
    int Move_Range = 3;
    boolean Waiting_for_Dice;
    GameView Player_GV;
    Player (GameView PGV)
    {
        Player_GV = PGV;
    }



    public void Move_Player (int New_X, int New_Y, int New_Row, int New_Column)
    {
        XPos = New_X;
        YPos = New_Y;
        Current_Row = New_Row;
        Current_Column = New_Column;
    }

    public void Slash_Ability()
    {
        Player_GV.Start_Dice_Roll();
    }



}
