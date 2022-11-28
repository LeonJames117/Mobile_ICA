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
    int Attack_Range = 1;
    int Attack_Power = 10;
    boolean Same_Column = false;
    int Health = 50;
    boolean Facing_Right = false;
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
            {//Enemy right of player
                Target_Column = Current_Column - Movement_Range;
                if(Target_Column < Player.Current_Column+1)
                {
                    Target_Column = Player.Current_Column+1;
                }
                Facing_Right=false;
            }
            if (Player.Current_Column-1 > Current_Column )
            {//Enemy Left of player
                Target_Column = Current_Column + Movement_Range;
                if(Target_Column > Player.Current_Column-1)
                {
                    Target_Column = Player.Current_Column-1;
                }
                Facing_Right=true;
            }
        }
        else
        {
            if (Player.Current_Column+1 < Current_Column)
            {//Enemy right of player
                Facing_Right=false;
            }
            else
            {//Enemy Left of player
                Facing_Right=true;
            }
        }

        if(Current_Row != Player.Current_Row)
        {
            if (Player.Current_Column == Current_Column)
            {
                Same_Column = true;
            }
            else
            {
                Same_Column = false;
            }
            if (Player.Current_Row < Current_Row)
            { // Enemy above player


                    if (Player.Current_Column == Current_Column)
                    {
                        Target_Row = Player.Current_Row+1;
                    }
                    else
                    {
                        Target_Row = Player.Current_Row;
                    }

            }
            if (Player.Current_Row > Current_Row )
            {  //Enemy bellow player

                if (Player.Current_Column == Current_Column)
                {
                    Target_Row = Player.Current_Row-1;
                }
                else
                {
                    Target_Row = Player.Current_Row;
                }

            }

        }

        GameView.Tile Target_Tile = Enemy_Grid_Help.Find_Tile(Enemy_Grid,Target_Column,Target_Row);
        Set_Location(Target_Tile.T_XPos,Target_Tile.T_YPos,Target_Column,Target_Row);

    }

    public void Set_Location(int New_X, int New_Y, int New_Column, int New_Row)
    {
        XPos = New_X + 40;
        YPos = New_Y + 35;
        Current_Row = New_Row;
        Current_Column = New_Column;
    }

    public void Enemy_Attack(Player Target)
    {
        if(!Enemy_Grid_Help.Tile_In_Range(Target.Current_Row, Target.Current_Column, Current_Column,Current_Row,Attack_Range))
        {
            Target.Health = Target.Health-Attack_Power;
            Log.d("Game", "Enemy Attacked");
            Log.d("Game", "Player Health: " + Target.Health);
        }
    }


}
