package com.A0053718.mobile_ica;

import android.util.Log;

public class Turn_Handler {

    //Variables
    //Entities
        Player TH_Player;
        Enemy TH_Enemy;
    //Player Turn
        boolean Is_Player_Turn = true;
        boolean Display_Skip_Movement = false;
        boolean Player_Move_Complete = false;
        boolean Player_Move_Allowed = true;
        boolean Player_Ability_Used = false;
        boolean Display_End_Turn = false;
        boolean Start_of_Player_Turn = true;
        boolean Display_Ability_Icons = false;
    //Enemy Turn
        boolean Is_Enemy_Turn = false;
    //Dice
        boolean Waiting_For_Dice = false;


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
            Display_Skip_Movement = true;
            Player_Move_Complete = false;
            Player_Ability_Used = false;
            Start_of_Player_Turn = false;
            Log.d("Game", "Player Turn Started");
        }

        if (Player_Move_Complete && !Waiting_For_Dice)
        {
            if (!Player_Ability_Used)
            {
                Display_Ability_Icons = true;
            }
            Display_End_Turn=true;
        }
        else
        {
            Display_End_Turn = false;
        }
    }

    public void End_Player_Turn()
    {
        Is_Player_Turn = false;
        Is_Enemy_Turn = true;
        Display_End_Turn = false;
        Display_Ability_Icons = false;

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
