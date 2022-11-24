package com.A0053718.mobile_ica;

public class Player {

    //Variables
    int XPos = 10;
    int YPos = 10;
    int Current_Row = 0;
    int Current_Column = 0;
    int Health = 100;

    public boolean Tile_In_Range(int Tile_Row, int Tile_Column)
    {
        return (Tile_Column > Current_Column + 3 || Tile_Column < Current_Column - 3  || Tile_Row > Current_Row + 3 || Tile_Row < Current_Row - 3);
    }

    public void Move_Player (int New_X, int New_Y, int New_Row, int New_Column)
    {
        XPos = New_X;
        YPos = New_Y;
        Current_Row = New_Row;
        Current_Column = New_Column;
    }


}
