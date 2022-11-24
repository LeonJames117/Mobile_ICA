package com.A0053718.mobile_ica;

import android.util.Log;

import java.util.Vector;

public class Enemy {

    //Variables
    int XPos = 500;
    int YPos = 500;
    int Current_Column = 0;
    int Current_Row = 0;
    int Movement_Range = 2;
    int Health = 50;
    Vector<GameView.Tile> Enemy_Grid;
    Grid_Utility Enemy_Grid_Help;
    Enemy(Vector<GameView.Tile> Grid, Grid_Utility GridUtil)
    {
        Enemy_Grid = Grid;
        Enemy_Grid_Help = GridUtil;
    }


    public void Enemy_Move(Player Player)
    {
        int Target_Column = Current_Column;
        int Target_Row = Current_Row;

        //Check if already in correct column
        if(Current_Column != Player.Current_Column+1 || Current_Column != Player.Current_Column-1)
        {
            if (Player.Current_Column+1 < Current_Column)
            {
                Target_Column = Current_Column - Movement_Range;
                if(Target_Column < Player.Current_Column+1)
                {
                    Target_Column = Player.Current_Column+1;
                }
            }
            if (Player.Current_Column-1 > Current_Column )
            {
                Target_Column = Current_Column + Movement_Range;
                if(Target_Column > Player.Current_Column+1)
                {
                    Target_Column = Player.Current_Column-1;
                }

            }
        }

        if(Current_Row != Player.Current_Row)
        {
            if (Player.Current_Row < Current_Row)
            {
                Target_Row = Current_Row - Movement_Range;
                if(Target_Row < Player.Current_Row)
                {
                    Target_Row = Player.Current_Row;
                }
            }
            if (Player.Current_Row > Current_Row )
            {
                Target_Row = Current_Row + Movement_Range;
                if(Target_Row > Player.Current_Row)
                {
                    Target_Row = Player.Current_Row;
                }

            }

            GameView.Tile Target_Tile = Enemy_Grid_Help.Find_Tile(Enemy_Grid,Target_Column,Target_Row);
            Set_Location(Target_Tile.T_XPos,Target_Tile.T_YPos,Target_Column,Target_Row);
        }



    }

    public void Set_Location(int New_X, int New_Y, int New_Column, int New_Row)
    {
        XPos = New_X;
        YPos = New_Y;
        Current_Row = New_Row;
        Current_Column = New_Column;
    }
}
