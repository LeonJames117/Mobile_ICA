package com.A0053718.mobile_ica;

import java.util.Vector;

public class Grid_Utility {

    //Variables

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
}
