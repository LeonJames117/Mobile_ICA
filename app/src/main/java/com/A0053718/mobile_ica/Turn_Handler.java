package com.A0053718.mobile_ica;

import android.util.Log;

public class Turn_Handler {

    //Variables
    //Entities
        Player TH_Player;
        Enemy TH_Enemy;
    //Player Turn
        boolean Is_Player_Turn = true;
        boolean Player_Move_Complete = false;
        boolean Player_Move_Allowed = true;
        boolean Display_End_Turn = false;
        boolean Start_of_Player_Turn = true;
    //Enemy Turn
        boolean Is_Enemy_Turn = false;


    Turn_Handler(Player Player, Enemy Enemy) {
        TH_Player = Player;
        TH_Enemy = Enemy;
    }

    public void Turn_Update()
    {
        if(Is_Player_Turn)
        {
            Player_Turn();
        }
        if(Is_Enemy_Turn)
        {
            Enemy_Turn();
        }
    }

    public void Player_Turn()
    {
        if (Start_of_Player_Turn)
        {
            Player_Move_Allowed = true;
            Player_Move_Complete = false;
            Start_of_Player_Turn = false;
            Log.d("Game", "Player Turn Started");
        }

        if (Player_Move_Complete)
        {
            Display_End_Turn=true;
        }
    }

    public void End_Player_Turn()
    {
        Is_Player_Turn = false;
        Is_Enemy_Turn = true;
        Display_End_Turn=false;
        Log.d("Game", "Player Turn Ended");
    }

    public void Enemy_Turn()
    {
        TH_Enemy.Enemy_Move(TH_Player);
        TH_Enemy.Enemy_Attack(TH_Player);
        End_Enemy_Turn();
    }

    public void End_Enemy_Turn()
    {
        Is_Player_Turn = true;
        Start_of_Player_Turn = true;
        Is_Enemy_Turn = false;
        Log.d("Game", "Enemy Turn Ended");
    }

}
