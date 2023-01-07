package com.A0053718.mobile_ica;

import android.util.Log;

import java.util.Vector;

public class Grid_Utility {

    // Returns a tile in the grid that corresponds to a given column and row
    public GameView.Tile Find_Tile(Vector<GameView.Tile> Grid, int Tile_Column, int Tile_Row) {
        for (GameView.Tile t : Grid)
        {
            if (t.T_Column == Tile_Column && t.T_Row == Tile_Row)
            {
                return t;
            }
        }
        GameView.Tile Fail = new GameView.Tile();
        return Fail ;
    }

    // Returns whether a given tile is within a given range of a position on the grid
    public boolean Tile_In_Range(int Tile_Row, int Tile_Column, int Current_Column, int Current_Row, int Move_Range)
    {

        //Forwards
        if(Tile_Column <= Current_Column + Move_Range && Tile_Column != Current_Column)
        {
            Log.d("GameView", "Tile_In_Range: Forwards ");
            Log.d("GameView", "Tile Column: " + Tile_Column);
            Log.d("GameView", "Current Column + Move Range: " + (Current_Column + Move_Range));
            return true;
        }
        //Backwards
        else if (Tile_Column <= Current_Column - Move_Range && Tile_Column != Current_Column)
        {
            Log.d("GameView", "Tile_In_Range: Backwards ");
            Log.d("GameView", "Tile Column: " + Tile_Column);
            Log.d("GameView", "Current Column: " + (Current_Column + Move_Range));
            return true;
        }
        //Down
        else if (Tile_Row > Current_Row + Move_Range)
        {
            Log.d("GameView", "Tile_In_Range: Down ");
            Log.d("GameView", "Tile Row: " + Tile_Row);
            Log.d("GameView", "Current Row: " + Current_Row);
            return true;
        }
        //Up
        else if (Tile_Row < Current_Row - Move_Range)
        {
            Log.d("GameView", "Tile_In_Range: Up ");
            Log.d("GameView", "Tile Row: " + Tile_Row);
            Log.d("GameView", "Current Row: " + Current_Row);
            return true;
        }
        else
        {
            Log.d("GameView", "Tile_In_Range: False ");
            Log.d("GameView", "Tile Row: " + Tile_Row);
            Log.d("GameView", "Current Row: " + Current_Row);
            Log.d("GameView", "Tile Column: " + Tile_Column);
            Log.d("GameView", "Current Column + Move Range: " + (Current_Column + Move_Range));
            return false;
        }




    }



    public boolean Enemy_Tile_In_Range(int Tile_Row, int Tile_Column, int Current_Column, int Current_Row, int Move_Range,boolean Facing_Right) {

        //Forwards
        if (Tile_Column <= Current_Column + Move_Range && Facing_Right) {
            Log.d("GameView", "Tile_In_Range: Forwards ");
            Log.d("GameView", "Tile Column: " + Tile_Column);
            Log.d("GameView", "Current Column: " + Current_Column);
            return true;
        }
        //Backwards
        else if (Tile_Column >= Current_Column - Move_Range && !Facing_Right) {
            Log.d("GameView", "Tile_In_Range: Backwards ");
            Log.d("GameView", "Tile Column: " + Tile_Column);
            Log.d("GameView", "Current Column: " + Current_Column);
            return true;
        }
        //Down
        else if (Tile_Row > Current_Row + Move_Range) {
            Log.d("GameView", "Tile_In_Range: Down ");
            Log.d("GameView", "Tile Row: " + Tile_Row);
            Log.d("GameView", "Current Row: " + Current_Row);
            return true;
        }
        //Up
        else if (Tile_Row < Current_Row - Move_Range) {
            Log.d("GameView", "Tile_In_Range: Up ");
            Log.d("GameView", "Tile Row: " + Tile_Row);
            Log.d("GameView", "Current Row: " + Current_Row);
            return true;
        } else {
            Log.d("GameView", "Tile_In_Range: False ");
            Log.d("GameView", "Tile Row: " + Tile_Row);
            Log.d("GameView", "Current Row: " + Current_Row);
            return false;
        }

    }
}
